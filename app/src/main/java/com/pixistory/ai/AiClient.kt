package com.pixistory.ai

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

class AiException(message: String) : Exception(message)

/**
 * Talks to the AI provider chosen in Settings with the user's own API key: Claude (Anthropic) or
 * Gemini (Google). The key is only ever sent to that provider.
 */
class AiClient(private val settings: SettingsStore) {
    private val http = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()
    private val json = "application/json".toMediaType()

    val isConfigured: Boolean
        get() = settings.current.let { if (it.aiProvider == "gemini") it.geminiKey.isNotBlank() else it.anthropicKey.isNotBlank() }

    suspend fun ask(system: String, turns: List<AiTurn>): String = withContext(Dispatchers.IO) {
        val s = settings.current
        val clean = normalise(turns)
        if (clean.isEmpty()) throw AiException("Nothing to ask")
        if (s.aiProvider == "gemini") gemini(s.geminiKey, s.geminiModel, system, clean)
        else anthropic(s.anthropicKey, s.anthropicModel, system, clean)
    }

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
        if (key.isBlank()) throw AiException("Add your Claude API key in Settings")
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
            if (!resp.isSuccessful) throw AiException(errorMessage(text, resp.code))
            val content = JSONObject(text).optJSONArray("content") ?: throw AiException("Empty reply")
            val sb = StringBuilder()
            for (i in 0 until content.length()) {
                val block = content.getJSONObject(i)
                if (block.optString("type") == "text") sb.append(block.optString("text"))
            }
            return sb.toString().trim().ifBlank { throw AiException("Empty reply") }
        }
    }

    private fun gemini(key: String, model: String, system: String, turns: List<AiTurn>): String {
        if (key.isBlank()) throw AiException("Add your Gemini API key in Settings")
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
            if (!resp.isSuccessful) throw AiException(errorMessage(text, resp.code))
            val parts = JSONObject(text).optJSONArray("candidates")
                ?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")
                ?: throw AiException("Empty reply")
            val sb = StringBuilder()
            for (i in 0 until parts.length()) sb.append(parts.getJSONObject(i).optString("text"))
            return sb.toString().trim().ifBlank { throw AiException("Empty reply") }
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
        return when (code) {
            401, 403 -> "The AI provider rejected the API key ($code). ${detail.take(140)}".trim()
            429 -> "Rate limit reached, try again in a moment."
            else -> "AI request failed ($code). ${detail.take(160)}".trim()
        }
    }
}
