package com.android.appclock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.di.ScheduleRepositoryEntryPoint
import com.android.appclock.utils.Constants.ACTION_TRIGGER_ALARM
import com.android.appclock.utils.Constants.EXTRA_PACKAGE_NAME
import com.android.appclock.utils.Constants.EXTRA_SCHEDULE_ID
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppLaunchReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: return
        val scheduledId = intent.getIntExtra(EXTRA_SCHEDULE_ID, -1)

        Log.i(TAG, "onReceive [${intent.action}] for [$packageName][$scheduledId]")
        if (intent.action != ACTION_TRIGGER_ALARM) return

        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent == null) {
            Log.e(TAG, "Launch intent is null. App might be uninstalled.")
            updateScheduleStatus(context, scheduledId, ScheduleStatus.FAILED)
            return
        }

        try {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)

            CoroutineScope(Dispatchers.IO).launch {
                // TODO Need to use UsageStatsManager for more accurate result
                updateScheduleStatus(context, scheduledId, ScheduleStatus.LAUNCHED)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch app for ${e.message}")
            updateScheduleStatus(context, scheduledId, ScheduleStatus.FAILED)
        }
    }

    private fun updateScheduleStatus(context: Context, scheduleId: Int, status: ScheduleStatus) {
        val repository = EntryPointAccessors.fromApplication(
            context.applicationContext,
            ScheduleRepositoryEntryPoint::class.java
        ).scheduleRepository()

        CoroutineScope(Dispatchers.IO).launch {
            val schedule = repository.getScheduleById(scheduleId)
            if (schedule != null) {
                repository.updateSchedule(schedule.copy(status = status))
            }
        }
    }

    companion object {
        const val TAG = "AppLaunchReceiver"
    }
}
