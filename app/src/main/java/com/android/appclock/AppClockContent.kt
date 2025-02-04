package com.android.appclock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.appclock.presentation.components.DrawerMenuContent
import com.android.appclock.presentation.components.FluidBottomNavigationBar
import com.android.appclock.presentation.navigation.AppNavHost
import com.android.appclock.presentation.navigation.Screen
import com.android.appclock.presentation.screens.ScheduleViewModel

@Composable
fun AppClockContent() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Get current route
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val viewModel: ScheduleViewModel = hiltViewModel() // Shared ViewModel

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
                    AppNavHost(navController = navController, viewModel = viewModel)
                }
            }
        })
}