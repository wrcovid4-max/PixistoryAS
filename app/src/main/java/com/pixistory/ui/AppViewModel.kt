package com.pixistory.ui

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pixistory.AppContainer
import com.pixistory.ai.AiTurn
import com.pixistory.ai.CollectionContext
import com.pixistory.data.model.ChatMessageEntity
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceFileEntity
import com.pixistory.data.model.DeviceType
import com.pixistory.data.model.Upgrade
import com.pixistory.data.model.upgrades
import com.pixistory.data.model.toUpgradesJson
import com.pixistory.data.repo.AppSettings
import com.pixistory.export.openFile
import com.pixistory.export.shareFile
import com.pixistory.notify.PatchAlerts
import com.pixistory.sync.SyncScheduler
import com.pixistory.util.AndroidVersions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

enum class ExportKind { JSON, CSV, TEXT, PDF, CARD, LABELS, BACKUP }

class AppViewModel(val container: AppContainer) : ViewModel() {
    private val repo = container.repository
    private val app = container.app

    val devices: StateFlow<List<DeviceEntity>> =
        repo.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val chat: StateFlow<List<ChatMessageEntity>> =
        repo.observeChat().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val settings: StateFlow<AppSettings> = container.settings.state
    val account = container.auth.account
    val syncState = container.sync.state

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val messages = _messages.asSharedFlow()
    private fun say(text: String) { _messages.tryEmit(text) }

    // ---- editor state (kept here so it survives fold / unfold and rotation) ----------------
    private val _editing = MutableStateFlow<DeviceEntity?>(null)
    val editing: StateFlow<DeviceEntity?> = _editing.asStateFlow()
    var editingIsNew: Boolean = false
        private set
    var editorStartsWithCatalog: Boolean = false
        private set

    fun startNew(seed: DeviceEntity? = null, catalogFirst: Boolean = false) {
        val now = System.currentTimeMillis()
        editingIsNew = true
        editorStartsWithCatalog = catalogFirst
        _editing.value = (seed ?: DeviceEntity(
            id = java.util.UUID.randomUUID().toString(),
            name = "",
            currency = settings.value.currency,
            createdAt = now,
            updatedAt = now,
        ))
    }

    fun startEdit(device: DeviceEntity) {
        editingIsNew = false
        editorStartsWithCatalog = false
        _editing.value = device
    }

    fun closeEditor() { _editing.value = null }

    // ---- devices ------------------------------------------------------------------------------
    fun save(device: DeviceEntity, then: (DeviceEntity) -> Unit = {}) {
        viewModelScope.launch { then(repo.save(device)) }
    }

    fun delete(id: String) {
        viewModelScope.launch { repo.delete(id); say("Deleted") }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch { repo.toggleFavorite(id) }
    }

    fun filesFor(deviceId: String) = repo.observeFiles(deviceId)

    fun addDocument(deviceId: String, uri: Uri) {
        viewModelScope.launch { if (!repo.addDocument(deviceId, uri)) say("Could not attach that file") }
    }

    fun removeDocument(file: DeviceFileEntity) {
        viewModelScope.launch { repo.removeDocument(file) }
    }

    fun openDocument(file: DeviceFileEntity) {
        val f = container.files.resolve(file.path)
        if (f == null) say("File is missing on this phone") else openFile(app, f, file.mime)
    }

    fun newTag(type: DeviceType, onTag: (String) -> Unit) {
        viewModelScope.launch { onTag(repo.nextAssetTag(type)) }
    }

    fun findByCode(code: String, onResult: (DeviceEntity?) -> Unit) {
        viewModelScope.launch { onResult(repo.findByCode(code)) }
    }

    /** Records an upgrade (for example Android 14 to 15) and updates the device's current version. */
    fun logUpgrade(device: DeviceEntity, toVersion: String, patch: String, note: String) {
        viewModelScope.launch {
            val from = AndroidVersions.label(device.apiLevel, device.androidVersion).removePrefix("Android ")
            val api = AndroidVersions.apiFromVersion(toVersion)
            val upgrade = Upgrade(System.currentTimeMillis(), from, toVersion, note)
            repo.save(
                device.copy(
                    androidVersion = toVersion,
                    apiLevel = api ?: device.apiLevel,
                    securityPatch = patch.ifBlank { device.securityPatch },
                    upgradesJson = (device.upgrades() + upgrade).toUpgradesJson(),
                ),
            )
            say("Upgrade logged")
        }
    }

    // ---- settings -----------------------------------------------------------------------------
    fun updateSettings(block: (AppSettings) -> AppSettings) = container.settings.update(block)

    fun setAutoSync(enabled: Boolean) {
        container.settings.update { it.copy(autoSync = enabled) }
        SyncScheduler.applyAutoSync(app, enabled && container.auth.isAvailable)
    }

    fun setPatchAlerts(enabled: Boolean) {
        container.settings.update { it.copy(patchAlerts = enabled) }
        PatchAlerts.apply(app, enabled)
    }

    // ---- account & sync -----------------------------------------------------------------------
    val signInAvailable: Boolean get() = container.auth.isAvailable
    val signInIntent: Intent get() = container.auth.signInIntent

    fun completeSignIn(data: Intent?) {
        viewModelScope.launch {
            try {
                container.auth.completeSignIn(data)
                say("Signed in")
                if (settings.value.autoSync) SyncScheduler.applyAutoSync(app, true)
                container.sync.sync()
            } catch (e: Exception) {
                say(e.message ?: "Sign-in failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch { container.auth.signOut(); say("Signed out") }
    }

    fun syncNow() {
        viewModelScope.launch { container.sync.sync() }
    }

    // ---- AI -----------------------------------------------------------------------------------
    private val _aiBusy = MutableStateFlow(false)
    val aiBusy: StateFlow<Boolean> = _aiBusy.asStateFlow()
    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()
    val aiConfigured: Boolean get() = container.ai.isConfigured

    fun askAi(text: String) {
        val q = text.trim()
        if (q.isEmpty() || _aiBusy.value) return
        viewModelScope.launch {
            _aiError.value = null
            _aiBusy.value = true
            try {
                repo.addChat("user", q)
                val history = repo.recentChat(20).map { AiTurn(it.role, it.text) }
                val system = CollectionContext.systemPrompt(repo.active(), settings.value.currency)
                val reply = container.ai.ask(system, history)
                repo.addChat("assistant", reply)
            } catch (e: Exception) {
                _aiError.value = e.message ?: "The AI request failed"
            } finally {
                _aiBusy.value = false
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch { repo.clearChat(); _aiError.value = null }
    }

    // ---- export / backup ----------------------------------------------------------------------
    fun export(kind: ExportKind) {
        viewModelScope.launch {
            try {
                val ex = container.exporter
                val (file: File, mime: String) = when (kind) {
                    ExportKind.JSON -> ex.json() to "application/json"
                    ExportKind.CSV -> ex.csv() to "text/csv"
                    ExportKind.TEXT -> ex.text() to "text/plain"
                    ExportKind.PDF -> ex.pdf() to "application/pdf"
                    ExportKind.CARD -> ex.card(settings.value.currency) to "image/png"
                    ExportKind.LABELS -> {
                        val tagged = repo.tagAllUntagged()
                        if (tagged > 0) say("Created $tagged new asset tags")
                        ex.labels() to "application/pdf"
                    }
                    ExportKind.BACKUP -> ex.backup() to "application/zip"
                }
                shareFile(app, file, mime, "Share")
            } catch (e: Exception) {
                say("Export failed: ${e.message}")
            }
        }
    }

    fun restore(uri: Uri) {
        viewModelScope.launch {
            try {
                val n = container.exporter.restore(uri)
                say("Restored $n device${if (n == 1) "" else "s"}")
                if (settings.value.autoSync) SyncScheduler.requestSync(app)
            } catch (e: Exception) {
                say("Restore failed: ${e.message}")
            }
        }
    }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer { AppViewModel(container) }
        }
    }
}
