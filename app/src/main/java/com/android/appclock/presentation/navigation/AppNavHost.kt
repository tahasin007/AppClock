package com.android.appclock.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.appclock.presentation.screens.addeditschedule.AddEditScheduleScreen
import com.android.appclock.presentation.screens.applaunch.AppLaunchScreen
import com.android.appclock.presentation.screens.history.HistoryScreen
import com.android.appclock.presentation.screens.home.HomeScreen
import com.android.appclock.presentation.screens.usagemonitoring.AddEditUsageMonitoringScreen
import com.android.appclock.presentation.screens.usagemonitoring.UsageMonitoringScreen
import com.android.appclock.utils.Constants.NAV_ARG_SCHEDULE_ID
import com.android.appclock.utils.Constants.NAV_ARG_USAGE_MONITORING_RULE_ID
import com.android.appclock.utils.Constants.SCHEDULE_ID_INVALID
import com.android.appclock.utils.Constants.USAGE_MONITORING_RULE_ID_INVALID

@Composable
fun AppNavHost(
    navController: NavHostController,
    onOpenDrawer: () -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(
            route = Screen.Home.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
        ) {
            HomeScreen(
                navController = navController,
                onOpenDrawer = onOpenDrawer
            )
        }
        composable(
            route = Screen.UsageMonitoring.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
        ) {
            UsageMonitoringScreen(navController = navController)
        }
        composable(
            route = Screen.AddEditUsageMonitoring.route +
                    "?$NAV_ARG_USAGE_MONITORING_RULE_ID={$NAV_ARG_USAGE_MONITORING_RULE_ID}",
            arguments = listOf(
                navArgument(NAV_ARG_USAGE_MONITORING_RULE_ID) {
                    type = NavType.IntType
                    defaultValue = USAGE_MONITORING_RULE_ID_INVALID
                }
            ),
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
        ) {
            AddEditUsageMonitoringScreen(navController = navController)
        }
        composable(
            route = Screen.AppLaunch.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
        ) {
            AppLaunchScreen(navController = navController)
        }
        composable(
            route = Screen.History.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
        ) {
            HistoryScreen(navController = navController)
        }
        composable(
            route = Screen.AddEditSchedule.route + "?$NAV_ARG_SCHEDULE_ID={$NAV_ARG_SCHEDULE_ID}",
            arguments = listOf(
                navArgument(NAV_ARG_SCHEDULE_ID) {
                    type = NavType.IntType
                    defaultValue = SCHEDULE_ID_INVALID
                }
            ),
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
        ) {
            AddEditScheduleScreen(navController = navController)
        }
    }
}

// Function to handle enter transition
private fun enterTransition(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(
            500,
            easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f) // Standard smooth ease
        )
    ) + fadeIn(
        animationSpec = tween(400, easing = LinearOutSlowInEasing)
    )
}

// Function to handle exit transition
private fun exitTransition(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(
            450,
            easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f) // Matching smooth ease
        )
    ) + fadeOut(
        animationSpec = tween(350, easing = FastOutLinearInEasing)
    )
}