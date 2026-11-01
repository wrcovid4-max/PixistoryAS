package com.pixistory.data.features

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Country(val name: String, val iso: String, val slug: String) {
    val flag: String
        get() = if (iso.length != 2) "" else {
            val base = 0x1F1E6
            String(Character.toChars(base + (iso[0].uppercaseChar() - 'A'))) + String(Character.toChars(base + (iso[1].uppercaseChar() - 'A')))
        }
    val url: String get() = "https://www.whereisenabled.com/country/$slug/"
}

data class AvailFeature(
    val id: String,
    val name: String,
    val service: String,
    val fromPixel: String,
    val locationBased: Boolean,
    val languageBased: Boolean,
    val pixelExclusive: Boolean,
    val desc: String,
    val note: String,
    /** Names of countries where it is available, or null when no country list is bundled for this feature. */
    val available: Set<String>?,
)

enum class Avail { AVAILABLE, NOT_AVAILABLE, VARIES }

data class GraveItem(val name: String, val opened: String, val closed: String, val type: String, val description: String, val link: String)
data class FoldApp(val name: String, val pkg: String, val publisher: String, val category: String, val note: String, val source: String, val url: String)

/**
 * Bundled reference data: Google feature availability by country (from whereisenabled.com, a community
 * project), Killed by Google (the open graveyard.json) and apps Google named as optimised for the Pixel Fold.
 */
class FeatureData(private val context: Context) {
    private fun read(name: String): String = context.assets.open(name).bufferedReader(Charsets.UTF_8).use { it.readText() }

    private val avail: JSONObject by lazy { runCatching { JSONObject(read("availability.json")) }.getOrDefault(JSONObject()) }

    val availabilitySource: String get() = avail.optString("source", "https://www.whereisenabled.com/")
    val availabilityDate: String get() = avail.optString("retrieved", "")

    val countries: List<Country> by lazy {
        val a = avail.optJSONArray("countries") ?: JSONArray()
        (0 until a.length()).map { val o = a.getJSONObject(it); Country(o.getString("name"), o.optString("iso"), o.optString("slug")) }
    }

    val features: List<AvailFeature> by lazy {
        val a = avail.optJSONArray("features") ?: JSONArray()
        (0 until a.length()).map { i ->
            val o = a.getJSONObject(i)
            val list = o.optJSONArray("available")?.let { l -> (0 until l.length()).map { l.getString(it) }.toSet() }
            AvailFeature(
                o.getString("id"), o.getString("name"), o.optString("service"), o.optString("from"),
                o.optBoolean("location"), o.optBoolean("language"), o.optBoolean("exclusive"),
                o.optString("desc"), o.optString("note"), list,
            )
        }
    }

    /** Features whose country list is bundled. */
    val trackedCount: Int get() = features.count { it.available != null }

    fun statusOf(feature: AvailFeature, country: Country): Avail = when {
        feature.available == null -> Avail.VARIES
        country.name in feature.available -> Avail.AVAILABLE
        else -> Avail.NOT_AVAILABLE
    }

    val graveyard: List<GraveItem> by lazy {
        runCatching {
            val a = JSONArray(read("graveyard.json"))
            (0 until a.length()).map {
                val o = a.getJSONObject(it)
                GraveItem(o.optString("name"), o.optString("dateOpen"), o.optString("dateClose"), o.optString("type"), o.optString("description"), o.optString("link"))
            }
        }.getOrDefault(emptyList())
    }

    private val fold: JSONObject by lazy { runCatching { JSONObject(read("foldapps.json")) }.getOrDefault(JSONObject()) }
    val foldIntro: String get() = fold.optString("intro")
    val foldApps: List<FoldApp> by lazy {
        val a = fold.optJSONArray("apps") ?: JSONArray()
        (0 until a.length()).map {
            val o = a.getJSONObject(it)
            FoldApp(o.optString("name"), o.optString("pkg"), o.optString("publisher"), o.optString("category"), o.optString("note"), o.optString("source"), o.optString("url"))
        }
    }
}
