package com.android.appclock.presentation.root

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
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

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmPermissionGranted()) {
            _showExactAlarmDialog.value = true
        }

        if (!overlayPermissionGranted()) {
            _showOverlayDialog.value = true
        }
    }

    fun dismissDialogs() {
        _showExactAlarmDialog.value = false
        _showOverlayDialog.value = false
    }

    private fun alarmPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            application.getSystemService(AlarmManager::class.java)?.canScheduleExactAlarms() == true
        } else true
    }

    private fun overlayPermissionGranted(): Boolean {
        return Settings.canDrawOverlays(application)
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
}