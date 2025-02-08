package com.android.appclock.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.appclock.presentation.screens.addeditschedule.AddEditScheduleScreen
import com.android.appclock.presentation.screens.history.HistoryScreen
import com.android.appclock.presentation.screens.home.HomeScreen
import com.android.appclock.utils.Constants.NAV_ARG_SCHEDULE_ID
import com.android.appclock.utils.Constants.SCHEDULE_ID_INVALID

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(
            route = Screen.Home.route,
            enterTransition = { enterTransition() },
            exitTransition = { exitTransition() },
        ) {
            HomeScreen(navController = navController)
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
    return fadeIn(
        animationSpec = tween(
            750, easing = LinearOutSlowInEasing // Smoother ease-in effect
        )
    ) + slideInHorizontally(
        animationSpec = tween(
            750,
            easing = CubicBezierEasing(0.5f, 1.4f, 0.5f, 1f)
        ), // Adding a bounce effect
        initialOffsetX = { fullWidth -> fullWidth }  // Slide in from right
    ) + scaleIn(
        initialScale = 0.95f, // Slight scaling for a subtle zoom effect
        animationSpec = tween(500, easing = LinearOutSlowInEasing)
    )
}


// Function to handle exit transition
private fun exitTransition(): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            600, easing = LinearEasing // Smooth fade out
        )
    ) + slideOutHorizontally(
        animationSpec = tween(
            600,
            easing = CubicBezierEasing(0.8f, 0.0f, 0.2f, 1f) // Smooth and controlled slide out
        ),
        targetOffsetX = { fullWidth -> -fullWidth }  // Slide out to the left
    ) + scaleOut(
        targetScale = 0.9f, // Slight shrink during the exit for a more dynamic feel
        animationSpec = tween(
            500, easing = FastOutSlowInEasing // Shrink slightly faster than the slide
        )
    ) + fadeOut(
        animationSpec = tween(
            300,
            easing = FastOutLinearInEasing // Fading out faster towards the end for a snappy exit
        )
    )
}