package com.android.appclock.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.appclock.presentation.screens.ScheduleViewModel
import com.android.appclock.presentation.screens.addeditschedule.AddEditScheduleScreen
import com.android.appclock.presentation.screens.history.HistoryScreen
import com.android.appclock.presentation.screens.home.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController, viewModel: ScheduleViewModel) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.History.route) {
            HistoryScreen(viewModel = viewModel)
        }
        composable(Screen.AddEditSchedule.route) {
            AddEditScheduleScreen(navController = navController)
        }
    }
}