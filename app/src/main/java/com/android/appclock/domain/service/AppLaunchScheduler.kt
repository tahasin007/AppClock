package com.android.appclock.domain.service

interface AppLaunchScheduler {
    fun scheduleAppLaunch(scheduleId: Int, packageName: String, scheduledTime: Long)
    fun cancelScheduledLaunch(scheduleId: Int)
}

