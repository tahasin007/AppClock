package com.android.appclock.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.android.appclock.receiver.AppLaunchReceiver
import com.android.appclock.utils.Constants.ACTION_TRIGGER_ALARM
import com.android.appclock.utils.Constants.EXTRA_PACKAGE_NAME
import com.android.appclock.utils.Constants.EXTRA_SCHEDULE_ID
import javax.inject.Inject

class AlarmScheduler @Inject constructor(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAppLaunch(scheduleId: Int, packageName: String, scheduledTime: Long) {
        Log.i(TAG, "scheduleAppLaunch for [$scheduleId][$packageName][$scheduledTime]")

        val intent = Intent(context, AppLaunchReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
            putExtra(EXTRA_PACKAGE_NAME, packageName)
            putExtra(EXTRA_SCHEDULE_ID, scheduleId)
        }

        val currentTime = System.currentTimeMillis()
        val triggerAfter = maxOf(scheduledTime - currentTime, 0) / 1000

        val days = triggerAfter / (24 * 60 * 60)
        val hours = (triggerAfter % (24 * 60 * 60)) / 3600
        val minutes = (triggerAfter % 3600) / 60
        val seconds = triggerAfter % 60

        Log.i(TAG, "scheduleAppLaunch in: $days days, $hours hours, $minutes minutes, $seconds seconds")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent
                )
            } else {
                // May not trigger exactly at triggerTime
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent
            )
        }
    }

    fun cancelScheduledLaunch(scheduleId: Int) {
        Log.i(TAG, "cancelScheduledLaunch for id[$scheduleId]")
        val intent = Intent(context, AppLaunchReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            scheduleId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val TAG = "AlarmScheduler"
    }
}