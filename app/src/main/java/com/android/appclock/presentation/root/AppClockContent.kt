package com.android.appclock.presentation.root

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.rememberNavController
import com.android.appclock.presentation.components.DrawerMenuContent
import com.android.appclock.presentation.components.PermissionDialog
import com.android.appclock.presentation.navigation.AppNavHost
import kotlinx.coroutines.launch

@Composable
fun AppClockContent(viewModel: PermissionViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentPermissionDialog = viewModel.currentPermissionDialog.value
    var notificationRequestInProgress by remember { mutableStateOf(false) }

    fun refreshPermissions() {
        viewModel.checkPermissions(
            canRequestNotificationSystemPrompt = shouldRequestNotificationSystemPrompt(
                context = context,
                hasNotificationPermission = viewModel.hasNotificationPermission.value,
                hasRequestedBefore = viewModel.hasRequestedNotificationPermission.value
            )
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        notificationRequestInProgress = false
        refreshPermissions()
    }

    LaunchedEffect(Unit) {
        refreshPermissions()
    }

    LaunchedEffect(viewModel.shouldRequestNotificationPermission.value) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            viewModel.shouldRequestNotificationPermission.value &&
            !notificationRequestInProgress
        ) {
            notificationRequestInProgress = true
            viewModel.markNotificationPermissionRequested()
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                DrawerMenuContent(
                    hasNotificationPermission = viewModel.hasNotificationPermission.value,
                    hasAlarmPermission = viewModel.hasAlarmPermission.value,
                    hasOverlayPermission = viewModel.hasOverlayPermission.value,
                    hasUsageStatsPermission = viewModel.hasUsageStatsPermission.value,
                    onFixPermissions = { refreshPermissions() },
                    onCloseClick = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }, content = {
            Scaffold { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    AppNavHost(
                        navController = navController,
                        onOpenDrawer = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )
                }
            }
        })

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentPermissionDialog == AppPermission.NOTIFICATIONS && !notificationRequestInProgress) {
            PermissionDialog(
                title = "Allow Notifications",
                message = "Enable notifications in settings to receive app launch and usage limit alerts.",
                onConfirm = { viewModel.openNotificationSettings(context) },
                onDismiss = { viewModel.dismissCurrentDialog() }
            )
        }

        if (currentPermissionDialog == AppPermission.EXACT_ALARM) {
            PermissionDialog(
                title = "Allow Exact Alarm",
                message = "To launch apps at proper time, please allow \"Alarms and reminders\" permission.",
                onConfirm = { viewModel.openExactAlarmSettings(context) },
                onDismiss = { viewModel.dismissCurrentDialog() }
            )
        }

        if (currentPermissionDialog == AppPermission.OVERLAY) {
            PermissionDialog(
                title = "Allow Overlay Permission",
                message = "To launch apps when AppClock is in background, please allow overlay permission.",
                onConfirm = { viewModel.openOverlaySettings(context) },
                onDismiss = { viewModel.dismissCurrentDialog() }
            )
        }

        if (currentPermissionDialog == AppPermission.USAGE_STATS) {
            PermissionDialog(
                title = "Allow Usage Stats Permission",
                message = "To track app launches accurately, please allow usage stats permission.",
                onConfirm = { viewModel.openUsageStatsSettings(context) },
                onDismiss = { viewModel.dismissCurrentDialog() }
            )
        }
    }
}

private fun shouldRequestNotificationSystemPrompt(
    context: Context,
    hasNotificationPermission: Boolean,
    hasRequestedBefore: Boolean
): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || hasNotificationPermission) {
        return false
    }

    val activity = context.findActivity() ?: return !hasRequestedBefore
    val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.POST_NOTIFICATIONS
    )
    return shouldShowRationale || !hasRequestedBefore
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
