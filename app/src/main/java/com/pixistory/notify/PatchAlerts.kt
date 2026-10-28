package com.pixistory.notify

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pixistory.MainActivity
import com.pixistory.PixistoryApp
import com.pixistory.R
import com.pixistory.data.model.DeviceStatus
import com.pixistory.data.model.statusEnum
import com.pixistory.util.patchAgeDays
import java.util.concurrent.TimeUnit

object PatchAlerts {
    private const val CHANNEL = "patch_alerts"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "Security patch alerts", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Tells you when a device in your collection has an old Android security patch"
                },
            )
        }
    }

    fun apply(context: Context, enabled: Boolean) {
        val wm = WorkManager.getInstance(context)
        if (enabled) {
            val request = PeriodicWorkRequestBuilder<PatchWorker>(1, TimeUnit.DAYS).build()
            wm.enqueueUniquePeriodicWork("pixistory-patch-daily", ExistingPeriodicWorkPolicy.KEEP, request)
        } else {
            wm.cancelUniqueWork("pixistory-patch-daily")
        }
    }

    fun notifyOld(context: Context, names: List<String>, days: Int) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        ensureChannel(context)
        val open = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val title = if (names.size == 1) "${names[0]} needs a newer patch" else "${names.size} devices have old patches"
        val text = names.take(4).joinToString(", ") + if (names.size > 4) " and ${names.size - 4} more" else ""
        val notification = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(title)
            .setContentText("$text: security patch older than $days days")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$text: security patch older than $days days"))
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(4401, notification)
    }
}

class PatchWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val container = (applicationContext as PixistoryApp).container
        val settings = container.settings.current
        if (!settings.patchAlerts) return Result.success()
        val old = container.repository.active()
            .filter { it.statusEnum == DeviceStatus.OWNED }
            .filter { (patchAgeDays(it.securityPatch) ?: 0L) > settings.patchDays }
        if (old.isNotEmpty()) PatchAlerts.notifyOld(applicationContext, old.map { it.name }, settings.patchDays)
        return Result.success()
    }
}
