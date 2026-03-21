package com.android.appclock

import android.app.Application
import com.android.appclock.data.monitoring.UsageLimitAlertScheduler
import com.android.appclock.notifications.UsageLimitNotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppClockApp : Application() {
    override fun onCreate() {
        super.onCreate()
        UsageLimitNotificationHelper.ensureChannel(this)
        UsageLimitAlertScheduler.ensureScheduled(this)
    }
}
