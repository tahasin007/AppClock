package com.android.appclock.presentation.root

import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

enum class AppPermission {
    EXACT_ALARM,
    OVERLAY,
    USAGE_STATS
}

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {

    private var missingPermissionsQueue: List<AppPermission> = emptyList()

    private val _currentPermissionDialog = mutableStateOf<AppPermission?>(null)
    val currentPermissionDialog: State<AppPermission?> = _currentPermissionDialog

    private val _hasAlarmPermission = mutableStateOf(true)
    val hasAlarmPermission: State<Boolean> = _hasAlarmPermission

    private val _hasOverlayPermission = mutableStateOf(true)
    val hasOverlayPermission: State<Boolean> = _hasOverlayPermission

    private val _hasUsageStatsPermission = mutableStateOf(true)
    val hasUsageStatsPermission: State<Boolean> = _hasUsageStatsPermission

    fun checkPermissions() {
        val missingPermissions = mutableListOf<AppPermission>()

        val alarmOk = !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmPermissionGranted())
        val overlayOk = overlayPermissionGranted()
        val usageOk = usageStatsPermissionGranted()

        _hasAlarmPermission.value = alarmOk
        _hasOverlayPermission.value = overlayOk
        _hasUsageStatsPermission.value = usageOk

        if (!alarmOk) missingPermissions.add(AppPermission.EXACT_ALARM)
        if (!overlayOk) missingPermissions.add(AppPermission.OVERLAY)
        if (!usageOk) missingPermissions.add(AppPermission.USAGE_STATS)

        missingPermissionsQueue = missingPermissions
        _currentPermissionDialog.value = missingPermissionsQueue.firstOrNull()
    }

    fun dismissCurrentDialog() {
        val currentPermission = _currentPermissionDialog.value ?: return
        val currentIndex = missingPermissionsQueue.indexOf(currentPermission)
        _currentPermissionDialog.value =
            missingPermissionsQueue.drop(currentIndex + 1).firstOrNull()
    }

    private fun hideCurrentDialog() {
        _currentPermissionDialog.value = null
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
                "package:${context.packageName}".toUri()
            )
            context.startActivity(intent)
        }
        hideCurrentDialog()
    }

    fun openOverlaySettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${context.packageName}".toUri()
        )
        context.startActivity(intent)
        hideCurrentDialog()
    }

    fun openUsageStatsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        context.startActivity(intent)
        hideCurrentDialog()
    }
}