package com.pixistory.ai

import com.pixistory.BuildConfig
import com.pixistory.data.repo.SettingsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiTurn(val role: String, val text: String)

/** [code] is the HTTP status when the provider answered, 0 otherwise. */
class AiException(message: String, val code: Int = 0) : Exception(message)

/**
 * Talks to Claude (Anthropic) or Gemini (Google).
 *
 * Keys come from Settings > Ask Pixi (stored on the phone), falling back to the ones baked in from
 * local.properties at build time. A key is only ever sent to its own provider.
 *
 * Gemini models: the free tier moves quickly (the 2.5 models are being retired), so unless a model is
 * chosen explicitly Pixi tries the current free models in turn and remembers the one that works.
 */
class AiClient(private val settings: SettingsStore) {
    private val http = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()
    private val json = "application/json".toMediaType()

    @Volatile private var workingGeminiModel: String? = null

    private fun anthropicKey() = settings.current.anthropicKey.trim().ifBlank { BuildConfig.ANTHROPIC_API_KEY.trim() }
    private fun geminiKey() = settings.current.geminiKey.trim().ifBlank { BuildConfig.GEMINI_API_KEY.trim() }

    /** Claude is used when its key is set, otherwise Gemini. */
    val isConfigured: Boolean
        get() = anthropicKey().isNotBlank() || geminiKey().isNotBlank()

    suspend fun ask(system: String, turns: List<AiTurn>): String = withContext(Dispatchers.IO) {
        val clean = normalise(turns)
        if (clean.isEmpty()) throw AiException("Nothing to ask")
        val claudeKey = anthropicKey()
        if (claudeKey.isNotBlank()) {
            anthropic(claudeKey, settings.current.anthropicModel.ifBlank { BuildConfig.ANTHROPIC_MODEL }, system, clean)
        } else {
            geminiWithFallback(geminiKey(), system, clean)
        }
    }

    /** A tiny round trip, so Settings can say plainly whether the key works. */
    suspend fun test(): String = ask("You are a connection test.", listOf(AiTurn("user", "Reply with the single word OK.")))

    /** Providers need the first turn from the user and strictly alternating roles. */
    private fun normalise(turns: List<AiTurn>): List<AiTurn> {
        val out = ArrayList<AiTurn>()
        for (t in turns) {
            if (t.text.isBlank()) continue
            if (out.isEmpty() && t.role != "user") continue
            if (out.isNotEmpty() && out.last().role == t.role) {
                out[out.size - 1] = AiTurn(t.role, out.last().text + "\n\n" + t.text)
            } else {
                out.add(t)
            }
        }
        return out
    }

    private fun anthropic(key: String, model: String, system: String, turns: List<AiTurn>): String {
        val messages = JSONArray()
        turns.forEach { messages.put(JSONObject().put("role", it.role).put("content", it.text)) }
        val body = JSONObject()
            .put("model", model)
            .put("max_tokens", 1500)
            .put("system", system)
            .put("messages", messages)
        val request = Request.Builder()
            .url("https://api.anthropic.com/v1/messages")
            .header("x-api-key", key.trim())
            .header("anthropic-version", "2023-06-01")
            .post(body.toString().toRequestBody(json))
            .build()
        http.newCall(request).execute().use { resp ->
            val text = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) throw AiException(errorMessage(text, resp.code), resp.code)
            val content = JSONObject(text).optJSONArray("content") ?: throw AiException("Empty reply")
            val sb = StringBuilder()
            for (i in 0 until content.length()) {
                val block = content.getJSONObject(i)
                if (block.optString("type") == "text") sb.append(block.optString("text"))
            }
            return sb.toString().trim().ifBlank { throw AiException("Empty reply") }
        }
    }

    private fun geminiWithFallback(key: String, system: String, turns: List<AiTurn>): String {
        if (key.isBlank()) throw AiException("Add a Gemini API key in Settings")
        // A model the user typed in themselves goes first; the old default is treated as "automatic".
        val chosen = settings.current.geminiModel.trim().takeIf { it.isNotBlank() && it != OLD_DEFAULT_MODEL }
        val candidates = (listOfNotNull(chosen, workingGeminiModel) + GEMINI_FREE_MODELS).distinct()

        var last: AiException? = null
        for (model in candidates) {
            try {
                val reply = gemini(key, model, system, turns)
                workingGeminiModel = model
                return reply
            } catch (e: AiException) {
                last = e
                if (!e.canTryAnotherModel()) throw e
            }
        }
        throw last ?: AiException("No Gemini model answered")
    }

    /** Wrong or retired model name, a per-model quota, or a busy server: another model may well work. */
    private fun AiException.canTryAnotherModel() = code == 404 || code == 429 || code == 500 || code == 503 ||
        (code == 400 && message.orEmpty().contains("not found", ignoreCase = true)) ||
        (code == 400 && message.orEmpty().contains("not supported", ignoreCase = true))

    private fun gemini(key: String, model: String, system: String, turns: List<AiTurn>): String {
        val contents = JSONArray()
        turns.forEach {
            contents.put(
                JSONObject()
                    .put("role", if (it.role == "assistant") "model" else "user")
                    .put("parts", JSONArray().put(JSONObject().put("text", it.text))),
            )
        }
        val body = JSONObject()
            .put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", system))))
            .put("contents", contents)
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/${model.trim()}:generateContent")
            .header("x-goog-api-key", key.trim())
            .post(body.toString().toRequestBody(json))
            .build()
        http.newCall(request).execute().use { resp ->
            val text = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) throw AiException(errorMessage(text, resp.code), resp.code)
            val parts = JSONObject(text).optJSONArray("candidates")
                ?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")
                ?: throw AiException("Empty reply", 200)
            val sb = StringBuilder()
            for (i in 0 until parts.length()) sb.append(parts.getJSONObject(i).optString("text"))
            return sb.toString().trim().ifBlank { throw AiException("Empty reply", 200) }
        }
    }

    private fun errorMessage(body: String, code: Int): String {
        val detail = try {
            val e = JSONObject(body).opt("error")
            when (e) {
                is JSONObject -> e.optString("message")
                is String -> e
                else -> ""
            }
        } catch (ex: Exception) {
            ""
        }
        return when {
            // Gemini answers 400 (not 401) for a key it doesn't recognise.
            code == 401 || code == 403 || (code == 400 && detail.contains("API key", ignoreCase = true)) ->
                "The AI provider rejected the API key ($code). ${detail.take(140)}".trim()
            code == 429 -> "Rate limit reached, try again in a moment."
            else -> "AI request failed ($code). ${detail.take(160)}".trim()
        }
    }

    private companion object {
        /** The default that shipped before; Google is retiring it, so it now means "choose for me". */
        const val OLD_DEFAULT_MODEL = "gemini-2.5-flash"

        /** Free-tier Gemini models, newest first (Nov 2026). The last one is not on the deprecation list. */
        val GEMINI_FREE_MODELS = listOf(
            "gemini-3.8-flash",
            "gemini-3.6-flash",
            "gemini-3.5-flash-lite",
            "gemini-3.1-flash-lite",
            "gemini-2.5-flash-lite",
        )
    }
}
