package com.pixistory.sync

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.pixistory.R

/**
 * Manual Firebase start-up from res/values/firebase.xml. While the three required values are blank
 * Firebase never starts and the app simply works offline, so no google-services plugin is needed.
 */
object FirebaseGate {
    @Volatile
    private var configured = false

    val isConfigured: Boolean get() = configured

    fun init(context: Context): Boolean {
        if (configured) return true
        val appId = context.getString(R.string.firebase_app_id)
        val apiKey = context.getString(R.string.firebase_api_key)
        val projectId = context.getString(R.string.firebase_project_id)
        if (appId.isBlank() || apiKey.isBlank() || projectId.isBlank()) return false
        if (FirebaseApp.getApps(context).isEmpty()) {
            val bucket = context.getString(R.string.firebase_storage_bucket).ifBlank { "$projectId.appspot.com" }
            val options = FirebaseOptions.Builder()
                .setApplicationId(appId)
                .setApiKey(apiKey)
                .setProjectId(projectId)
                .setStorageBucket(bucket)
                .build()
            FirebaseApp.initializeApp(context, options)
        }
        configured = true
        return true
    }

    fun webClientId(context: Context): String = context.getString(R.string.firebase_web_client_id)
}
