package com.android.appclock.utils

import android.content.Context
import android.content.Intent
import com.android.appclock.service.AppForegroundService

object ServiceUtil {
    fun startForegroundService(context: Context) {
        val intent = Intent(context, AppForegroundService::class.java)
        context.startForegroundService(intent)
    }
}