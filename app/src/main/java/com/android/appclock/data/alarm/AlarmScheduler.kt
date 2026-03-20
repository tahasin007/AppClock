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

    private fun createAlarmIntent(scheduleId: Int, packageName: String? = null): Intent {
        return Intent(context, AppLaunchReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
            putExtra(EXTRA_SCHEDULE_ID, scheduleId)
            if (!packageName.isNullOrBlank()) {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
            }
        }
    }

    fun scheduleAppLaunch(scheduleId: Int, packageName: String, scheduledTime: Long) {
        Log.i(TAG, "Scheduling app launch - ID: $scheduleId, Package: $packageName")

        val intent = createAlarmIntent(scheduleId, packageName)

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
        val intent = createAlarmIntent(scheduleId)
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