package com.android.appclock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.android.appclock.presentation.components.DrawerMenuContent
import com.android.appclock.presentation.components.FluidBottomNavigationBar
import com.android.appclock.presentation.navigation.AppNavHost

@Composable
fun AppClockContent() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenuContent { _ ->
                // Handle menu item clicks if needed
            }
        }, content = {
            Scaffold(
                bottomBar = {
                    FluidBottomNavigationBar(
                        navController = navController,
                        drawerState = drawerState
                    )
                }) {
                Box(modifier = Modifier.padding(it)) {
                    AppNavHost(navController = navController)
                }
            }
        })
}