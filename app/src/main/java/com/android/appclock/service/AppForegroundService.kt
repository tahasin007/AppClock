package com.android.appclock.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.appclock.R

class AppForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY  // Ensures service restarts if killed
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onCreate")
        stopForeground(true)
    }

    private fun createNotification(): Notification {
        val channelId = "AppClockServiceChannel"
        val channelName = "Foreground Service"

        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("AppClock is Running")
            .setContentText("Service is running in the background to launch apps at scheduled times")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val TAG = "AppForegroundService"
    }
}
