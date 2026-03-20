package com.android.appclock.presentation.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.android.appclock.presentation.components.DrawerMenuContent
import com.android.appclock.presentation.components.PermissionDialog
import com.android.appclock.presentation.navigation.AppNavHost
import com.android.appclock.presentation.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun AppClockContent(viewModel: PermissionViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentPermissionDialog = viewModel.currentPermissionDialog.value

    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkPermissions()
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
            ModalDrawerSheet {
                DrawerMenuContent(
                    onHistoryClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate(Screen.History.route)
                    },
                    onCloseClick = {
                        scope.launch {
                            drawerState.close()
                        }
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