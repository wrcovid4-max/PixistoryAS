package com.pixistory.data.pixel

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** English + Urdu text. */
class LText(val en: String, val ur: String) {
    fun get(lang: String): String = if (lang == "ur" && ur.isNotBlank()) ur else en
}

data class PixelGen(val id: String, val name: LText, val year: Int, val history: LText, val features: List<LText>)
data class PixelFault(val text: LText, val official: Boolean)
data class PixelModel(
    val id: String,
    val name: String,
    val gen: String,
    val type: String,
    val released: String,
    val launchApi: Int,
    val supportEnds: String,
    val specs: Map<String, String>,
    val faults: List<PixelFault>,
)
data class DropDetail(val title: String, val desc: String, val devices: List<String>)
data class PixelDrop(
    val id: String,
    val date: String,
    val title: LText,
    val tie: LText,
    val summary: LText,
    val features: List<LText>,
    val devices: List<String>,
    val note: LText?,
    val details: List<DropDetail>,
)

private fun JSONObject.lt(key: String): LText {
    val o = optJSONObject(key)
    return LText(o?.optString("en").orEmpty(), o?.optString("ur").orEmpty())
}

private fun JSONArray.lts(): List<LText> = (0 until length()).map { i ->
    val o = getJSONObject(i)
    LText(o.optString("en"), o.optString("ur"))
}

/**
 * Built-in Pixel reference data: the history of each generation, tech specs, what was new, known faults
 * and the history of Pixel Drops with the exact phones that received them. It is compiled from Google's
 * announcements and press coverage (see tools/ and the About page) and can be wrong or out of date.
 */
class PixelKnowledge(private val context: Context) {
    private fun read(name: String): JSONObject =
        context.assets.open(name).bufferedReader(Charsets.UTF_8).use { JSONObject(it.readText()) }

    val gens: List<PixelGen> by lazy {
        try {
            val a = read("pixel_knowledge.json").getJSONArray("generations")
            (0 until a.length()).map { i ->
                val o = a.getJSONObject(i)
                PixelGen(o.getString("id"), o.lt("name"), o.optInt("year"), o.lt("history"), o.optJSONArray("features")?.lts().orEmpty())
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    val models: List<PixelModel> by lazy {
        try {
            val a = read("pixel_knowledge.json").getJSONArray("models")
            (0 until a.length()).map { i ->
                val o = a.getJSONObject(i)
                val specs = LinkedHashMap<String, String>()
                o.optJSONObject("specs")?.let { s ->
                    val keys = s.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        specs[k] = s.optString(k)
                    }
                }
                val faults = o.optJSONArray("faults")?.let { fa ->
                    (0 until fa.length()).map { j ->
                        val f = fa.getJSONObject(j)
                        PixelFault(LText(f.optString("en"), f.optString("ur")), f.optBoolean("official", false))
                    }
                }.orEmpty()
                PixelModel(
                    o.getString("id"), o.getString("name"), o.optString("gen"), o.optString("type"),
                    o.optString("released"), o.optInt("launchApi"), o.optString("supportEnds"), specs, faults,
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    val drops: List<PixelDrop> by lazy {
        try {
            val a = read("pixel_drops.json").getJSONArray("drops")
            (0 until a.length()).map { i ->
                val o = a.getJSONObject(i)
                val devs = o.optJSONArray("devices")?.let { d -> (0 until d.length()).map { d.getString(it) } }.orEmpty()
                val details = o.optJSONArray("details")?.let { d ->
                    (0 until d.length()).map { j ->
                        val x = d.getJSONObject(j)
                        val ids = x.optJSONArray("devices")?.let { v -> (0 until v.length()).map { v.getString(it) } }.orEmpty()
                        DropDetail(x.optString("title"), x.optString("desc"), ids)
                    }
                }.orEmpty()
                PixelDrop(
                    o.getString("id"), o.getString("date"), o.lt("title"), o.lt("tie"), o.lt("summary"),
                    o.optJSONArray("features")?.lts().orEmpty(), devs,
                    if (o.has("note") && !o.isNull("note")) o.lt("note") else null, details,
                )
            }.sortedByDescending { it.date }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun gen(id: String?): PixelGen? = gens.firstOrNull { it.id == id }
    fun model(id: String): PixelModel? = models.firstOrNull { it.id == id }
    fun dropsFor(modelId: String): List<PixelDrop> = drops.filter { modelId in it.devices }

    private fun norm(s: String): String =
        s.lowercase().removePrefix("google").replace(Regex("[^a-z0-9]"), "")

    /** Finds the Pixel in the database that a device entry is: "Google Pixel 9 Pro" matches "Pixel 9 Pro". */
    fun match(name: String, model: String = ""): PixelModel? {
        val candidates = listOf(name, model).map { norm(it) }.filter { it.startsWith("pixel") }
        if (candidates.isEmpty()) return null
        return models.firstOrNull { m -> norm(m.name) in candidates }
    }
}
