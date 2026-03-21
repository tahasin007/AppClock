package com.android.appclock.data.monitoring

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.android.appclock.worker.UsageLimitAlertWorker
import java.util.concurrent.TimeUnit

object UsageLimitAlertScheduler {
    private const val UNIQUE_WORK_NAME = "usage_limit_alerts_periodic"

    fun ensureScheduled(context: Context) {
        val request = PeriodicWorkRequestBuilder<UsageLimitAlertWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context.applicationContext)
            .enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
}

