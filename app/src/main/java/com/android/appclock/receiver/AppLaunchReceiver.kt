package com.android.appclock.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import com.android.appclock.R
import com.android.appclock.core.utils.Constants.ACTION_TRIGGER_ALARM
import com.android.appclock.core.utils.Constants.EXTRA_PACKAGE_NAME
import com.android.appclock.core.utils.Constants.EXTRA_SCHEDULE_ID
import com.android.appclock.di.AlarmSchedulerEntryPoint
import com.android.appclock.di.AppLaunchTrackerEntryPoint
import com.android.appclock.di.ScheduleRepositoryEntryPoint
import com.android.appclock.domain.model.RecurringType
import com.android.appclock.domain.model.ScheduleStatus
import com.android.appclock.domain.repository.ScheduleRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

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
                try {
                    val repository = getRepository(context)
                    updateScheduleStatus(repository, scheduledId, ScheduleStatus.FAILED)
                    rescheduleIfRecurring(context, scheduledId)
                } finally {
                    pendingResult.finish()
                }
            }
            return
        }

        try {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            Log.i(TAG, "App launched successfully: $packageName")

            // Verify launch using UsageStatsManager
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    verifyAndUpdateStatus(context, packageName, scheduledId)
                } finally {
                    pendingResult.finish()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch app: ${e.message}")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val repository = getRepository(context)
                    updateScheduleStatus(repository, scheduledId, ScheduleStatus.FAILED)
                    rescheduleIfRecurring(context, scheduledId)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private suspend fun verifyAndUpdateStatus(
        context: Context,
        packageName: String,
        scheduleId: Int
    ) {
        // Wait a few seconds for the app to launch.
        delay(3000)

        val appLaunchTracker = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AppLaunchTrackerEntryPoint::class.java
        ).appLaunchTracker()

        val repository = getRepository(context)

        val launchTime = System.currentTimeMillis()
        val isLaunched = appLaunchTracker.verifyAppLaunched(packageName, launchTime - 5000)

        // Get schedule to retrieve app name.
        val schedule = repository.getScheduleById(scheduleId)
        val appName = schedule?.appName ?: packageName

        if (isLaunched) {
            Log.i(TAG, "App launch verified for: $packageName")
            updateScheduleStatus(repository, scheduleId, ScheduleStatus.LAUNCHED)
            sendNotification(context, appName, true)
        } else {
            Log.e(TAG, "Could not verify app launch for: $packageName")
            updateScheduleStatus(repository, scheduleId, ScheduleStatus.FAILED)
            sendNotification(context, appName, false)
        }

        rescheduleIfRecurring(context, scheduleId)
    }

    private suspend fun rescheduleIfRecurring(context: Context, scheduleId: Int) {
        if (scheduleId < 0) return
        val repository = getRepository(context)
        val schedule = repository.getScheduleById(scheduleId) ?: return
        if (schedule.recurringType == RecurringType.NONE) return

        val nextTime = when (schedule.recurringType) {
            RecurringType.DAILY -> schedule.scheduledDateTime + 24 * 60 * 60 * 1000L
            RecurringType.WEEKLY -> schedule.scheduledDateTime + 7 * 24 * 60 * 60 * 1000L
            RecurringType.MONTHLY -> ZonedDateTime
                .ofInstant(Instant.ofEpochMilli(schedule.scheduledDateTime), ZoneId.systemDefault())
                .plusMonths(1)
                .toInstant()
                .toEpochMilli()

            RecurringType.NONE -> return
        }

        repository.updateSchedule(
            schedule.copy(scheduledDateTime = nextTime, status = ScheduleStatus.UPCOMING)
        )

        val alarmScheduler = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AlarmSchedulerEntryPoint::class.java
        ).alarmScheduler()

        alarmScheduler.scheduleAppLaunch(scheduleId, schedule.packageName, nextTime)
        Log.i(TAG, "Recurring schedule rescheduled: id=$scheduleId, nextTime=$nextTime")
    }

    private fun sendNotification(context: Context, appName: String, success: Boolean) {
        if (!canPostNotifications(context)) {
            Log.w(TAG, "Skipping app launch notification")
            return
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "app_launch_channel"

        val channel = NotificationChannel(
            channelId,
            "App Launch",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

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

    private fun canPostNotifications(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getRepository(context: Context): ScheduleRepository {
        return EntryPointAccessors.fromApplication(
            context.applicationContext,
            ScheduleRepositoryEntryPoint::class.java
        ).scheduleRepository()
    }

    private suspend fun updateScheduleStatus(
        repository: ScheduleRepository,
        scheduleId: Int,
        status: ScheduleStatus
    ) {
        if (scheduleId < 0) {
            Log.w(TAG, "Skipping status update for invalid schedule id: $scheduleId")
            return
        }

        val schedule = repository.getScheduleById(scheduleId)
        if (schedule != null) {
            repository.updateSchedule(schedule.copy(status = status))
        }
    }

    companion object {
        const val TAG = "AppLaunchReceiver"
    }
}
