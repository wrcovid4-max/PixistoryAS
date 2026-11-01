package com.pixistory.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pixistory.BuildConfig
import com.pixistory.MainActivity
import com.pixistory.PixistoryApp
import com.pixistory.R
import com.pixistory.data.repo.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Pixel news alerts. A small JSON feed (feed/news.json in the project's GitHub repo, or the URL in
 * local.properties as NEWS_FEED_URL) lists items with a type: drop, device, leak, killed, availability.
 * The phone checks it every few hours and notifies only for the types you switched on. The first check
 * only records what already exists, so you are never flooded with old items.
 */
object NewsAlerts {
    private const val CHANNEL = "pixel_news"

    fun wantsFeed(s: AppSettings) = s.notifDrops || s.notifDevices || s.notifLeaks || s.notifKilled || s.notifAvailability

    fun apply(context: Context, s: AppSettings) {
        val wm = WorkManager.getInstance(context)
        if (wantsFeed(s)) {
            wm.enqueueUniquePeriodicWork(
                "pixistory-news", ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<NewsWorker>(6, TimeUnit.HOURS).build(),
            )
        } else {
            wm.cancelUniqueWork("pixistory-news")
        }
    }

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(NotificationChannel(CHANNEL, "Pixel news", NotificationManager.IMPORTANCE_DEFAULT))
        }
    }

    fun notify(context: Context, id: Int, title: String, body: String, url: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        ensureChannel(context)
        val intent = if (url.isNotBlank()) Intent(Intent.ACTION_VIEW, Uri.parse(url)) else Intent(context, MainActivity::class.java)
        val open = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val n = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(id, n)
    }
}

class NewsWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val container = (applicationContext as PixistoryApp).container
        val s = container.settings.current
        if (!NewsAlerts.wantsFeed(s)) return Result.success()
        val text = try {
            withContext(Dispatchers.IO) {
                val http = OkHttpClient.Builder().callTimeout(30, TimeUnit.SECONDS).build()
                http.newCall(Request.Builder().url(BuildConfig.NEWS_FEED_URL).build()).execute().use { r ->
                    if (!r.isSuccessful) null else r.body?.string()
                }
            }
        } catch (e: Exception) {
            null
        } ?: return Result.retry()
        val items = runCatching { JSONObject(text).getJSONArray("items") }.getOrNull() ?: return Result.success()
        val prefs = applicationContext.getSharedPreferences("pixistory_news", Context.MODE_PRIVATE)
        val seen = prefs.getStringSet("seen", emptySet())!!.toMutableSet()
        val first = !prefs.getBoolean("initialised", false)
        for (i in 0 until items.length()) {
            val o = items.getJSONObject(i)
            val id = o.optString("id")
            if (id.isBlank() || id in seen) continue
            seen.add(id)
            if (first) continue
            val wanted = when (o.optString("type")) {
                "drop" -> s.notifDrops
                "device" -> s.notifDevices
                "leak" -> s.notifLeaks
                "killed" -> s.notifKilled
                "availability" -> s.notifAvailability
                else -> false
            }
            if (wanted) NewsAlerts.notify(applicationContext, id.hashCode(), o.optString("title"), o.optString("body"), o.optString("url"))
        }
        prefs.edit().putStringSet("seen", seen).putBoolean("initialised", true).apply()
        return Result.success()
    }
}
