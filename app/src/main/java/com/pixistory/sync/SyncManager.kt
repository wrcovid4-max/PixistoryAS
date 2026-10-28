package com.pixistory.sync

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pixistory.data.db.PixistoryDb
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.deviceFromMap
import com.pixistory.data.model.toMap
import com.pixistory.data.repo.FileStore
import com.pixistory.data.repo.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

sealed interface SyncState {
    data object Idle : SyncState
    data object Syncing : SyncState
    data class Done(val pulled: Int, val pushed: Int, val at: Long) : SyncState
    data class Error(val message: String) : SyncState
}

/**
 * Two-way sync with Firestore at users/{uid}/devices/{id}. The newest `updatedAt` wins per device,
 * deletions travel as tombstones, and photos go to Firebase Storage at users/{uid}/photos/….
 * Receipts and other attached files stay on the phone (they are in the backup ZIP).
 */
class SyncManager(
    private val db: PixistoryDb,
    private val settings: SettingsStore,
    private val auth: AuthManager,
    private val files: FileStore,
) {
    private val _state = MutableStateFlow<SyncState>(SyncState.Idle)
    val state: StateFlow<SyncState> = _state.asStateFlow()
    private val mutex = Mutex()

    suspend fun sync(): SyncState = mutex.withLock {
        val uid = auth.uid
        if (uid == null) {
            val s = SyncState.Error("Sign in with Google to sync")
            _state.value = s
            return s
        }
        _state.value = SyncState.Syncing
        try {
            val fs = FirebaseFirestore.getInstance()
            val col = fs.collection("users").document(uid).collection("devices")
            val snap = col.get().await()
            val remote: Map<String, DeviceEntity> = snap.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                deviceFromMap(data + ("id" to doc.id))
            }.associateBy { it.id }
            val local = db.devices().allIncludingDeleted().associateBy { it.id }

            val toLocal = remote.values.filter { r -> local[r.id]?.let { r.updatedAt > it.updatedAt } ?: true }
            val toRemote = local.values.filter { l -> remote[l.id]?.let { l.updatedAt > it.updatedAt } ?: true }

            // Photos first, so a record never points at an image that failed to upload.
            val storage = FirebaseStorage.getInstance().reference
            for (l in toRemote) {
                val file = files.resolve(l.photoPath) ?: continue
                if (!l.deleted && remote[l.id]?.photoPath != l.photoPath) {
                    runCatching { storage.child("users/$uid/${l.photoPath}").putFile(Uri.fromFile(file)).await() }
                }
            }
            for (r in toLocal) {
                if (r.photoPath.isNotBlank() && !r.deleted && files.resolve(r.photoPath) == null) {
                    runCatching {
                        val target = files.file(r.photoPath)
                        target.parentFile?.mkdirs()
                        storage.child("users/$uid/${r.photoPath}").getFile(target).await()
                    }
                }
            }

            toRemote.chunked(400).forEach { chunk ->
                val batch = fs.batch()
                chunk.forEach { batch.set(col.document(it.id), it.toMap()) }
                batch.commit().await()
            }
            if (toLocal.isNotEmpty()) db.devices().upsertAll(toLocal)
            db.devices().purgeTombstones(System.currentTimeMillis() - 30L * 24 * 3600 * 1000)

            val now = System.currentTimeMillis()
            settings.update { it.copy(lastSync = now) }
            val done = SyncState.Done(toLocal.size, toRemote.size, now)
            _state.value = done
            done
        } catch (e: Exception) {
            val s = SyncState.Error(e.message ?: "Sync failed")
            _state.value = s
            s
        }
    }
}
