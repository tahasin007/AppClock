package com.android.appclock.receiver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.domain.repository.ScheduleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppLaunchReceiver @Inject constructor(
    private val repository: ScheduleRepository
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val packageName = intent?.getStringExtra("PACKAGE_NAME") ?: return
        val scheduledId = intent.getIntExtra("SCHEDULE_ID", -1)

        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent == null) {
            Log.e("AppLaunchReceiver", "Launch intent is null. App might be uninstalled.")
            updateScheduleStatus(scheduledId, ScheduleStatus.FAILED)
            return
        }

        try {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)

            CoroutineScope(Dispatchers.IO).launch {
                delay(1000)
                val status = if (isAppRunning(context, packageName)) {
                    ScheduleStatus.LAUNCHED
                } else {
                    ScheduleStatus.FAILED
                }
                updateScheduleStatus(scheduledId, status)
            }
        } catch (e: Exception) {
            Log.e("AppLaunchReceiver", "Failed to launch app", e)
            updateScheduleStatus(scheduledId, ScheduleStatus.FAILED)
        }
    }

    private fun updateScheduleStatus(scheduleId: Int, status: ScheduleStatus) {
        CoroutineScope(Dispatchers.IO).launch {
            val schedule = repository.getScheduleById(scheduleId)
            if (schedule != null) {
                repository.updateSchedule(schedule.copy(status = status))
            }
        }
    }

    private fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = activityManager.runningAppProcesses ?: return false

        return runningProcesses.any { it.processName == packageName }
    }
}
