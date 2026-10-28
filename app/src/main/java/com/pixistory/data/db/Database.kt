package com.pixistory.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pixistory.data.model.ChatMessageEntity
import com.pixistory.data.model.DeviceEntity
import com.pixistory.data.model.DeviceFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices WHERE deleted = 0 ORDER BY favorite DESC, name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<DeviceEntity>>

    @Query("SELECT * FROM devices")
    suspend fun allIncludingDeleted(): List<DeviceEntity>

    @Query("SELECT * FROM devices WHERE deleted = 0")
    suspend fun allActive(): List<DeviceEntity>

    @Query("SELECT * FROM devices WHERE id = :id")
    suspend fun get(id: String): DeviceEntity?

    @Query("SELECT * FROM devices WHERE deleted = 0 AND (assetTag = :code OR serial = :code) LIMIT 1")
    suspend fun findByCode(code: String): DeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(device: DeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(devices: List<DeviceEntity>)

    @Query("DELETE FROM devices WHERE deleted = 1 AND updatedAt < :before")
    suspend fun purgeTombstones(before: Long)

    @Query("SELECT assetTag FROM devices WHERE assetTag != ''")
    suspend fun assetTags(): List<String>

    @Query("DELETE FROM devices")
    suspend fun clearAll()
}

@Dao
interface FileDao {
    @Query("SELECT * FROM device_files WHERE deviceId = :deviceId ORDER BY addedAt DESC")
    fun observeFor(deviceId: String): Flow<List<DeviceFileEntity>>

    @Query("SELECT * FROM device_files")
    suspend fun all(): List<DeviceFileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: DeviceFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(files: List<DeviceFileEntity>)

    @Query("DELETE FROM device_files WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM device_files WHERE deviceId = :deviceId")
    suspend fun deleteForDevice(deviceId: String)

    @Query("DELETE FROM device_files")
    suspend fun clearAll()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY createdAt ASC, id ASC")
    fun observe(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY createdAt DESC, id DESC LIMIT :limit")
    suspend fun recent(limit: Int): List<ChatMessageEntity>

    @Insert
    suspend fun insert(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clear()
}

@Database(
    entities = [DeviceEntity::class, DeviceFileEntity::class, ChatMessageEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class PixistoryDb : RoomDatabase() {
    abstract fun devices(): DeviceDao
    abstract fun files(): FileDao
    abstract fun chat(): ChatDao

    companion object {
        fun build(context: Context): PixistoryDb =
            Room.databaseBuilder(context.applicationContext, PixistoryDb::class.java, "pixistory.db").build()
    }
}
