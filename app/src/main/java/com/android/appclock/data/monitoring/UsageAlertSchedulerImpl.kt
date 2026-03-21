package com.android.appclock.data.monitoring

import android.content.Context
import com.android.appclock.domain.service.UsageAlertScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UsageAlertSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UsageAlertScheduler {
    override fun ensureScheduled() {
        UsageLimitAlertScheduler.ensureScheduled(context)
    }
}

