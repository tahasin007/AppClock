package com.android.appclock.presentation.root

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.appclock.presentation.components.DrawerMenuContent
import com.android.appclock.presentation.components.FluidBottomNavigationBar
import com.android.appclock.presentation.components.PermissionDialog
import com.android.appclock.presentation.navigation.AppNavHost
import com.android.appclock.presentation.navigation.Screen

@Composable
fun AppClockContent(viewModel: PermissionViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Get current route
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentPermissionDialog = viewModel.currentPermissionDialog.value
    
    // Track selection mode in history screen
    var isHistorySelectionMode by remember { mutableStateOf(false) }

    fun onHistorySelectionModeChange(isSelectionMode: Boolean) {
        isHistorySelectionMode = isSelectionMode
    }

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
        drawerContent = { DrawerMenuContent() }, content = {
            Scaffold(
                bottomBar = {
                    // Determine nav bar visibility based on current route
                    val showBottomNav = currentRoute?.let { 
                        it.contains(Screen.Home.route) || it.contains(Screen.History.route)
                    } ?: true
                    
                    // Animate alpha to 0 when on AddEditSchedule, to 1 on Home/History
                    val navBarAlpha by animateFloatAsState(
                        targetValue = if (showBottomNav && !isHistorySelectionMode) 1f else 0f,
                        animationSpec = tween(200),
                        label = "navBarAlpha"
                    )
                    
                    // Always render nav bar but with alpha animation to keep layout stable
                    if (navBarAlpha > 0f) {
                        FluidBottomNavigationBar(
                            navController = navController,
                            drawerState = drawerState,
                            modifier = Modifier.alpha(navBarAlpha)
                        )
                    }
                }) {
                Box(modifier = Modifier.padding(it)) {
                    AppNavHost(
                        navController = navController,
                        onHistorySelectionModeChange = { isSelectionMode ->
                            onHistorySelectionModeChange(isSelectionMode)
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