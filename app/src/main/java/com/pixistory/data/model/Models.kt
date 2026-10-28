package com.pixistory.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

enum class DeviceType(val label: String, val code: String) {
    PHONE("Phone", "PH"),
    FOLDABLE("Foldable", "FD"),
    TABLET("Tablet", "TB"),
    WATCH("Watch", "WT"),
    EARBUDS("Earbuds", "EB"),
    TV("TV / streamer", "TV"),
    LAPTOP("Laptop", "LP"),
    CAMERA("Camera", "CM"),
    ACCESSORY("Accessory", "AC"),
    OTHER("Other", "OT");

    companion object {
        fun of(name: String?): DeviceType = entries.firstOrNull { it.name == name } ?: OTHER
    }
}

enum class DeviceStatus(val label: String) {
    OWNED("Owned"),
    WISHLIST("Wishlist"),
    SOLD("Sold / gone");

    companion object {
        fun of(name: String?): DeviceStatus = entries.firstOrNull { it.name == name } ?: OWNED
    }
}

enum class Condition(val label: String) {
    NEW("New / sealed"),
    EXCELLENT("Excellent"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor"),
    BROKEN("Broken / for parts");

    companion object {
        fun of(name: String?): Condition = entries.firstOrNull { it.name == name } ?: GOOD
    }
}

/** One device in the collection. Photos and files are stored as paths relative to the app's files dir. */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String = "",
    val model: String = "",
    val type: String = DeviceType.PHONE.name,
    val status: String = DeviceStatus.OWNED.name,
    val releaseYear: Int? = null,
    /** Android API level the device runs now (for example 35 for Android 15). */
    val apiLevel: Int? = null,
    val androidVersion: String = "",
    /** yyyy-MM-dd */
    val securityPatch: String = "",
    val buildNumber: String = "",
    val serial: String = "",
    val colorHex: String = "",
    val colorName: String = "",
    val storageGb: Int? = null,
    val ramGb: Int? = null,
    val purchaseDate: Long? = null,
    val price: Double? = null,
    val currency: String = "PKR",
    val condition: String = Condition.GOOD.name,
    val notes: String = "",
    val groupName: String = "",
    val favorite: Boolean = false,
    val assetTag: String = "",
    val photoPath: String = "",
    val customFieldsJson: String = "{}",
    val upgradesJson: String = "[]",
    val createdAt: Long,
    val updatedAt: Long,
    val deleted: Boolean = false,
)

@Entity(tableName = "device_files")
data class DeviceFileEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val displayName: String,
    val mime: String,
    val path: String,
    val addedAt: Long,
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** "user" or "assistant" */
    val role: String,
    val text: String,
    val createdAt: Long,
)

data class Upgrade(val date: Long, val from: String, val to: String, val note: String)

val DeviceEntity.typeEnum: DeviceType get() = DeviceType.of(type)
val DeviceEntity.statusEnum: DeviceStatus get() = DeviceStatus.of(status)
val DeviceEntity.conditionEnum: Condition get() = Condition.of(condition)

fun DeviceEntity.customFields(): Map<String, String> = try {
    val o = JSONObject(customFieldsJson)
    val out = LinkedHashMap<String, String>()
    val keys = o.keys()
    while (keys.hasNext()) {
        val k = keys.next()
        out[k] = o.optString(k)
    }
    out
} catch (e: Exception) {
    emptyMap()
}

fun Map<String, String>.toJsonString(): String {
    val j = JSONObject()
    for ((k, v) in this) j.put(k, v)
    return j.toString()
}

fun DeviceEntity.upgrades(): List<Upgrade> = try {
    val a = JSONArray(upgradesJson)
    (0 until a.length()).map { i ->
        val o = a.getJSONObject(i)
        Upgrade(o.optLong("date"), o.optString("from"), o.optString("to"), o.optString("note"))
    }
} catch (e: Exception) {
    emptyList()
}

fun List<Upgrade>.toUpgradesJson(): String {
    val a = JSONArray()
    for (u in this) {
        a.put(JSONObject().put("date", u.date).put("from", u.from).put("to", u.to).put("note", u.note))
    }
    return a.toString()
}

// ---- Map codec: used by cloud sync, JSON export and backups ---------------------------------

fun DeviceEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "brand" to brand,
    "model" to model,
    "type" to type,
    "status" to status,
    "releaseYear" to releaseYear,
    "apiLevel" to apiLevel,
    "androidVersion" to androidVersion,
    "securityPatch" to securityPatch,
    "buildNumber" to buildNumber,
    "serial" to serial,
    "colorHex" to colorHex,
    "colorName" to colorName,
    "storageGb" to storageGb,
    "ramGb" to ramGb,
    "purchaseDate" to purchaseDate,
    "price" to price,
    "currency" to currency,
    "condition" to condition,
    "notes" to notes,
    "groupName" to groupName,
    "favorite" to favorite,
    "assetTag" to assetTag,
    "photoPath" to photoPath,
    "customFieldsJson" to customFieldsJson,
    "upgradesJson" to upgradesJson,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt,
    "deleted" to deleted,
)

private fun Map<String, Any?>.str(k: String): String = (this[k] as? String).orEmpty()
private fun Map<String, Any?>.int(k: String): Int? = (this[k] as? Number)?.toInt()
private fun Map<String, Any?>.lng(k: String): Long? = (this[k] as? Number)?.toLong()
private fun Map<String, Any?>.dbl(k: String): Double? = (this[k] as? Number)?.toDouble()
private fun Map<String, Any?>.bool(k: String): Boolean = (this[k] as? Boolean) ?: false

fun deviceFromMap(m: Map<String, Any?>): DeviceEntity? {
    val id = m.str("id")
    if (id.isBlank()) return null
    val now = System.currentTimeMillis()
    return DeviceEntity(
        id = id,
        name = m.str("name"),
        brand = m.str("brand"),
        model = m.str("model"),
        type = m.str("type").ifBlank { DeviceType.PHONE.name },
        status = m.str("status").ifBlank { DeviceStatus.OWNED.name },
        releaseYear = m.int("releaseYear"),
        apiLevel = m.int("apiLevel"),
        androidVersion = m.str("androidVersion"),
        securityPatch = m.str("securityPatch"),
        buildNumber = m.str("buildNumber"),
        serial = m.str("serial"),
        colorHex = m.str("colorHex"),
        colorName = m.str("colorName"),
        storageGb = m.int("storageGb"),
        ramGb = m.int("ramGb"),
        purchaseDate = m.lng("purchaseDate"),
        price = m.dbl("price"),
        currency = m.str("currency").ifBlank { "PKR" },
        condition = m.str("condition").ifBlank { Condition.GOOD.name },
        notes = m.str("notes"),
        groupName = m.str("groupName"),
        favorite = m.bool("favorite"),
        assetTag = m.str("assetTag"),
        photoPath = m.str("photoPath"),
        customFieldsJson = m.str("customFieldsJson").ifBlank { "{}" },
        upgradesJson = m.str("upgradesJson").ifBlank { "[]" },
        createdAt = m.lng("createdAt") ?: now,
        updatedAt = m.lng("updatedAt") ?: now,
        deleted = m.bool("deleted"),
    )
}

fun DeviceEntity.toJson(): JSONObject {
    val j = JSONObject()
    for ((k, v) in toMap()) {
        if (v != null) j.put(k, v)
    }
    return j
}

fun deviceFromJson(j: JSONObject): DeviceEntity? {
    val m = HashMap<String, Any?>()
    val keys = j.keys()
    while (keys.hasNext()) {
        val k = keys.next()
        m[k] = if (j.isNull(k)) null else j.get(k)
    }
    return deviceFromMap(m)
}
