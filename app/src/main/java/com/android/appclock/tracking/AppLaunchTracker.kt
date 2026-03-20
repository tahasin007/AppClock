package com.android.appclock.tracking

import android.app.Application
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import javax.inject.Inject

class AppLaunchTracker @Inject constructor(private val application: Application) {

    /**
     * Verifies if an app has been launched after the specified timestamp
     * by checking UsageStatsManager for ACTIVITY_RESUMED events
     */
    fun verifyAppLaunched(packageName: String, afterTimeMillis: Long): Boolean {
        val usageStatsManager =
            application.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val endTime = System.currentTimeMillis()
        val beginTime = afterTimeMillis

        val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
        val eventInfo = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(eventInfo)

            if (eventInfo.eventType == UsageEvents.Event.ACTIVITY_RESUMED &&
                eventInfo.packageName == packageName
            ) {
                return true
            }
        }

        return false
    }
}

