package com.pixistory.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pixistory.PixistoryApp
import java.util.concurrent.TimeUnit

object SyncScheduler {
    private val online = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    /** Queues a sync a few seconds from now; repeated edits collapse into one run. */
    fun requestSync(context: Context) {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInitialDelay(3, TimeUnit.SECONDS)
            .setConstraints(online)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork("pixistory-sync-now", ExistingWorkPolicy.REPLACE, request)
    }

    fun applyAutoSync(context: Context, enabled: Boolean) {
        val wm = WorkManager.getInstance(context)
        if (enabled) {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS).setConstraints(online).build()
            wm.enqueueUniquePeriodicWork("pixistory-sync-periodic", ExistingPeriodicWorkPolicy.KEEP, request)
        } else {
            wm.cancelUniqueWork("pixistory-sync-periodic")
            wm.cancelUniqueWork("pixistory-sync-now")
        }
    }
}

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val container = (applicationContext as PixistoryApp).container
        if (container.auth.uid == null) return Result.success()
        val state = container.sync.sync()
        return if (state is SyncState.Error && runAttemptCount < 3) Result.retry() else Result.success()
    }
}
