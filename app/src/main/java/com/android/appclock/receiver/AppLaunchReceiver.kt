package com.android.appclock.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.appclock.R
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.di.AppLaunchTrackerEntryPoint
import com.android.appclock.di.ScheduleRepositoryEntryPoint
import com.android.appclock.tracking.AppLaunchTracker
import com.android.appclock.utils.Constants.ACTION_TRIGGER_ALARM
import com.android.appclock.utils.Constants.EXTRA_PACKAGE_NAME
import com.android.appclock.utils.Constants.EXTRA_SCHEDULE_ID
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppLaunchReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: return
        val scheduledId = intent.getIntExtra(EXTRA_SCHEDULE_ID, -1)

        Log.i(TAG, "onReceive [${intent.action}] for [$packageName][$scheduledId]")
        if (intent.action != ACTION_TRIGGER_ALARM) return

        // Use goAsync() to extend receiver execution time for verification
        val pendingResult = goAsync()

        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent == null) {
            Log.e(TAG, "Launch intent is null. App might be uninstalled.")
            CoroutineScope(Dispatchers.IO).launch {
                updateScheduleStatus(context, scheduledId, ScheduleStatus.FAILED)
                pendingResult.finish()
            }
            return
        }

        try {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            Log.i(TAG, "App launched successfully: $packageName")

            // Verify launch using UsageStatsManager
            verifyAndUpdateStatus(context, packageName, scheduledId, pendingResult)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch app: ${e.message}")
            CoroutineScope(Dispatchers.IO).launch {
                updateScheduleStatus(context, scheduledId, ScheduleStatus.FAILED)
                pendingResult.finish()
            }
        }
    }

    private fun verifyAndUpdateStatus(context: Context, packageName: String, scheduleId: Int, pendingResult: PendingResult) {
        CoroutineScope(Dispatchers.IO).launch {
            // Wait a few seconds for the app to launch
            delay(3000)

            val appLaunchTracker = EntryPointAccessors.fromApplication(
                context.applicationContext,
                AppLaunchTrackerEntryPoint::class.java
            ).appLaunchTracker()

            val repository = EntryPointAccessors.fromApplication(
                context.applicationContext,
                ScheduleRepositoryEntryPoint::class.java
            ).scheduleRepository()
            
            val launchTime = System.currentTimeMillis()
            val isLaunched = appLaunchTracker.verifyAppLaunched(packageName, launchTime - 5000)

            // Get schedule to retrieve app name
            val schedule = repository.getScheduleById(scheduleId)
            val appName = schedule?.appName ?: packageName
            
            if (isLaunched) {
                Log.i(TAG, "App launch verified for: $packageName")
                updateScheduleStatus(context, scheduleId, ScheduleStatus.LAUNCHED)
                sendNotification(context, appName, true)
            } else {
                Log.e(TAG, "Could not verify app launch for: $packageName")
                updateScheduleStatus(context, scheduleId, ScheduleStatus.FAILED)
                sendNotification(context, appName, false)
            }

            pendingResult.finish()
        }
    }

    private fun sendNotification(context: Context, appName: String, success: Boolean) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "app_launch_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "App Launch",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val title = if (success) "App Launched" else "App Launch Failed"
        val text = if (success) {
            "Successfully launched $appName"
        } else {
            "Failed to verify launch of $appName"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
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
