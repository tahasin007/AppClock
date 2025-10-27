package com.android.appclock.presentation.root

import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private val _showExactAlarmDialog = mutableStateOf(false)
    val showExactAlarmDialog: State<Boolean> = _showExactAlarmDialog

    private val _showOverlayDialog = mutableStateOf(false)
    val showOverlayDialog: State<Boolean> = _showOverlayDialog

    private val _showUsageStatsDialog = mutableStateOf(false)
    val showUsageStatsDialog: State<Boolean> = _showUsageStatsDialog

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmPermissionGranted()) {
            _showExactAlarmDialog.value = true
        }

        if (!overlayPermissionGranted()) {
            _showOverlayDialog.value = true
        }

        if (!usageStatsPermissionGranted()) {
            _showUsageStatsDialog.value = true
        }
    }

    fun dismissDialogs() {
        _showExactAlarmDialog.value = false
        _showOverlayDialog.value = false
        _showUsageStatsDialog.value = false
    }

    private fun alarmPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            application.getSystemService(AlarmManager::class.java)?.canScheduleExactAlarms() == true
        } else true
    }

    private fun overlayPermissionGranted(): Boolean {
        return Settings.canDrawOverlays(application)
    }

    private fun usageStatsPermissionGranted(): Boolean {
        val appOpsManager = application.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            application.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }
        dismissDialogs()
    }

    fun openOverlaySettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        context.startActivity(intent)
        dismissDialogs()
    }

    fun openUsageStatsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        context.startActivity(intent)
        dismissDialogs()
    }
}