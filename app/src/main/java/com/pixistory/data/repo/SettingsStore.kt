package com.pixistory.data.repo

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppSettings(
    /** "anthropic" or "gemini" */
    val aiProvider: String = "anthropic",
    val anthropicKey: String = "",
    val geminiKey: String = "",
    val anthropicModel: String = "claude-sonnet-5-5",
    val geminiModel: String = "gemini-2.5-flash",
    /** "system", "light" or "dark" */
    val theme: String = "system",
    val patchAlerts: Boolean = false,
    /** Tint the Shelf card with the shown device's colour. */
    val shelfMatchColor: Boolean = true,
    val notifDrops: Boolean = false,
    val notifDevices: Boolean = false,
    val notifLeaks: Boolean = false,
    val notifKilled: Boolean = false,
    val notifAvailability: Boolean = false,
    val patchDays: Int = 180,
    val autoSync: Boolean = true,
    val lastSync: Long = 0L,
    val currency: String = "PKR",
    val assetPrefix: String = "PX",
    val assetDigits: Int = 4,
    val assetUseCategory: Boolean = true,
    val nextAssetNumber: Int = 1,
)

/** Small key-value store in app-private storage. API keys never leave the phone except to the AI provider. */
class SettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("pixistory_settings", Context.MODE_PRIVATE)
    private val _state = MutableStateFlow(load())
    val state: StateFlow<AppSettings> = _state.asStateFlow()

    val current: AppSettings get() = _state.value

    @Synchronized
    fun update(block: (AppSettings) -> AppSettings) {
        val next = block(_state.value)
        _state.value = next
        save(next)
    }

    private fun load(): AppSettings {
        val d = AppSettings()
        return AppSettings(
            aiProvider = prefs.getString("aiProvider", d.aiProvider) ?: d.aiProvider,
            anthropicKey = prefs.getString("anthropicKey", "") ?: "",
            geminiKey = prefs.getString("geminiKey", "") ?: "",
            anthropicModel = prefs.getString("anthropicModel", d.anthropicModel) ?: d.anthropicModel,
            geminiModel = prefs.getString("geminiModel", d.geminiModel) ?: d.geminiModel,
            theme = prefs.getString("theme", d.theme) ?: d.theme,
            patchAlerts = prefs.getBoolean("patchAlerts", d.patchAlerts),
            shelfMatchColor = prefs.getBoolean("shelfMatchColor", true),
            notifDrops = prefs.getBoolean("notifDrops", false),
            notifDevices = prefs.getBoolean("notifDevices", false),
            notifLeaks = prefs.getBoolean("notifLeaks", false),
            notifKilled = prefs.getBoolean("notifKilled", false),
            notifAvailability = prefs.getBoolean("notifAvailability", false),
            patchDays = prefs.getInt("patchDays", d.patchDays),
            autoSync = prefs.getBoolean("autoSync", d.autoSync),
            lastSync = prefs.getLong("lastSync", 0L),
            currency = prefs.getString("currency", d.currency) ?: d.currency,
            assetPrefix = prefs.getString("assetPrefix", d.assetPrefix) ?: d.assetPrefix,
            assetDigits = prefs.getInt("assetDigits", d.assetDigits),
            assetUseCategory = prefs.getBoolean("assetUseCategory", d.assetUseCategory),
            nextAssetNumber = prefs.getInt("nextAssetNumber", d.nextAssetNumber),
        )
    }

    private fun save(s: AppSettings) {
        prefs.edit()
            .putString("aiProvider", s.aiProvider)
            .putString("anthropicKey", s.anthropicKey)
            .putString("geminiKey", s.geminiKey)
            .putString("anthropicModel", s.anthropicModel)
            .putString("geminiModel", s.geminiModel)
            .putString("theme", s.theme)
            .putBoolean("patchAlerts", s.patchAlerts)
            .putBoolean("shelfMatchColor", s.shelfMatchColor)
            .putBoolean("notifDrops", s.notifDrops)
            .putBoolean("notifDevices", s.notifDevices)
            .putBoolean("notifLeaks", s.notifLeaks)
            .putBoolean("notifKilled", s.notifKilled)
            .putBoolean("notifAvailability", s.notifAvailability)
            .putInt("patchDays", s.patchDays)
            .putBoolean("autoSync", s.autoSync)
            .putLong("lastSync", s.lastSync)
            .putString("currency", s.currency)
            .putString("assetPrefix", s.assetPrefix)
            .putInt("assetDigits", s.assetDigits)
            .putBoolean("assetUseCategory", s.assetUseCategory)
            .putInt("nextAssetNumber", s.nextAssetNumber)
            .apply()
    }
}
