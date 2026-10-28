package com.pixistory.data.repo

import android.content.Context
import android.net.Uri
import com.pixistory.data.db.PixistoryDb
import com.pixistory.data.model.ChatMessageEntity
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceFileEntity
import com.pixistory.data.model.DeviceType
import com.pixistory.sync.SyncScheduler
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class DeviceRepository(
    private val context: Context,
    val db: PixistoryDb,
    val settings: SettingsStore,
    val files: FileStore,
) {
    fun observeAll(): Flow<List<DeviceEntity>> = db.devices().observeAll()
    fun observeFiles(deviceId: String): Flow<List<DeviceFileEntity>> = db.files().observeFor(deviceId)
    fun observeChat(): Flow<List<ChatMessageEntity>> = db.chat().observe()

    suspend fun get(id: String): DeviceEntity? = db.devices().get(id)
    suspend fun active(): List<DeviceEntity> = db.devices().allActive()
    suspend fun findByCode(code: String): DeviceEntity? = db.devices().findByCode(code.trim())

    /** Stamps the update time and queues a cloud sync. */
    suspend fun save(device: DeviceEntity): DeviceEntity {
        val stamped = device.copy(updatedAt = System.currentTimeMillis())
        db.devices().upsert(stamped)
        requestSync()
        return stamped
    }

    suspend fun delete(id: String) {
        val d = db.devices().get(id) ?: return
        d.photoPath.takeIf { it.isNotBlank() }?.let { files.delete(it) }
        db.files().all().filter { it.deviceId == id }.forEach { files.delete(it.path) }
        db.files().deleteForDevice(id)
        // A tombstone (not a hard delete) so the deletion reaches other devices on the next sync.
        db.devices().upsert(d.copy(deleted = true, photoPath = "", updatedAt = System.currentTimeMillis()))
        requestSync()
    }

    suspend fun toggleFavorite(id: String) {
        val d = db.devices().get(id) ?: return
        save(d.copy(favorite = !d.favorite))
    }

    /** Next free asset tag; uses and advances the numbering from settings. */
    suspend fun nextAssetTag(type: DeviceType): String {
        val used = db.devices().assetTags().toSet()
        var tag: String
        do {
            val s = settings.current
            tag = AssetTags.format(s.assetPrefix, type, s.nextAssetNumber, s.assetDigits, s.assetUseCategory)
            settings.update { it.copy(nextAssetNumber = it.nextAssetNumber + 1) }
        } while (tag in used)
        return tag
    }

    /** Gives every active device without a tag one. Returns how many were tagged. */
    suspend fun tagAllUntagged(): Int {
        var n = 0
        for (d in db.devices().allActive()) {
            if (d.assetTag.isBlank()) {
                save(d.copy(assetTag = nextAssetTag(d.type.let { DeviceType.of(it) })))
                n++
            }
        }
        return n
    }

    suspend fun addPhoto(device: DeviceEntity, uri: Uri): DeviceEntity? {
        val rel = files.savePhoto(uri, device.id) ?: return null
        if (device.photoPath.isNotBlank() && device.photoPath != rel) files.delete(device.photoPath)
        return save(device.copy(photoPath = rel))
    }

    suspend fun addDocument(deviceId: String, uri: Uri): Boolean {
        val (rel, name, mime) = files.saveDocument(uri) ?: return false
        db.files().insert(DeviceFileEntity(UUID.randomUUID().toString(), deviceId, name, mime, rel, System.currentTimeMillis()))
        return true
    }

    suspend fun removeDocument(file: DeviceFileEntity) {
        files.delete(file.path)
        db.files().delete(file.id)
    }

    // ---- chat -------------------------------------------------------------------------------
    suspend fun addChat(role: String, text: String) {
        db.chat().insert(ChatMessageEntity(role = role, text = text, createdAt = System.currentTimeMillis()))
    }

    suspend fun recentChat(limit: Int): List<ChatMessageEntity> = db.chat().recent(limit).reversed()
    suspend fun clearChat() = db.chat().clear()

    private fun requestSync() {
        if (settings.current.autoSync) SyncScheduler.requestSync(context)
    }
}
