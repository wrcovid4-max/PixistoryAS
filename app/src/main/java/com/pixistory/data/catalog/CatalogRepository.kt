package com.pixistory.data.catalog

import android.content.Context
import com.pixistory.data.model.DeviceType
import org.json.JSONArray

data class CatalogItem(
    val name: String,
    val brand: String,
    val type: DeviceType,
    val year: Int?,
    val api: Int?,
    val pixelId: String? = null,
) {
    val title: String get() = if (name.startsWith(brand, ignoreCase = true) || brand.isBlank()) name else "$brand $name"
}

/** A built-in starter catalogue of popular Android devices. It is only a head start: every field can be edited. */
class CatalogRepository(private val context: Context) {
    private val items: List<CatalogItem> by lazy { load() }

    fun search(query: String, limit: Int = 60): List<CatalogItem> {
        val words = query.trim().lowercase().split(' ').filter { it.isNotBlank() }
        if (words.isEmpty()) return items.take(limit)
        return items.filter { item ->
            val hay = "${item.brand} ${item.name} ${item.year ?: ""}".lowercase()
            words.all { it in hay }
        }.take(limit)
    }

    private fun load(): List<CatalogItem> = try {
        val text = context.assets.open("catalog.json").bufferedReader().use { it.readText() }
        val a = JSONArray(text)
        (0 until a.length()).map { i ->
            val o = a.getJSONObject(i)
            CatalogItem(
                name = o.getString("name"),
                brand = o.optString("brand"),
                type = DeviceType.of(o.optString("type")),
                year = if (o.has("year")) o.getInt("year") else null,
                api = if (o.has("api")) o.getInt("api") else null,
                pixelId = if (o.has("kid")) o.getString("kid") else null,
            )
        }.sortedByDescending { it.year ?: 0 }
    } catch (e: Exception) {
        emptyList()
    }
}
