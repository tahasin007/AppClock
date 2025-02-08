package com.android.appclock.presentation.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val showExactAlarmDialog = viewModel.showExactAlarmDialog.value
    val showOverlayDialog = viewModel.showOverlayDialog.value

    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { DrawerMenuContent() }, content = {
            Scaffold(
                bottomBar = {
                    if (currentRoute == null || currentRoute.contains(Screen.Home.route)
                        || currentRoute.contains(Screen.History.route)
                    ) {
                        FluidBottomNavigationBar(
                            navController = navController,
                            drawerState = drawerState
                        )
                    }
                }) {
                Box(modifier = Modifier.padding(it)) {
                    AppNavHost(navController = navController)
                }
            }
        })

    Box(modifier = Modifier.fillMaxSize()) {
        // Show Exact Alarm Dialog
        if (showExactAlarmDialog) {
            PermissionDialog(
                title = "Allow Exact Alarm",
                message = "To launch apps at proper time, please allow \"Alarms and reminders\" permission.",
                onConfirm = { viewModel.openExactAlarmSettings(context) },
                onDismiss = { viewModel.dismissDialogs() }
            )
        }

        // Show Overlay Permission Dialog
        if (showOverlayDialog) {
            PermissionDialog(
                title = "Allow Overlay Permission",
                message = "To launch apps when AppClock is in background, please allow overlay permission.",
                onConfirm = { viewModel.openOverlaySettings(context) },
                onDismiss = { viewModel.dismissDialogs() }
            )
        }
    }
}