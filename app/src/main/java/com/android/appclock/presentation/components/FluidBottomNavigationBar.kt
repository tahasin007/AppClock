package com.android.appclock.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

@Composable
fun FluidBottomNavigationBar(
    navController: NavHostController,
    drawerState: DrawerState
) {
    val tabs = listOf(
        BottomBarTab.Settings,
        BottomBarTab.Home,
        BottomBarTab.History
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .animateContentSize(),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            val iconScale by animateFloatAsState(
                targetValue = if (selected) 1.5f else 1f,
                animationSpec = tween(durationMillis = 500), label = ""
            )

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) tab.activeIcon else tab.inactiveIcon,
                        contentDescription = tab.label,
                        modifier = Modifier
                            .scale(iconScale)
                            .padding(vertical = 8.dp)
                    )
                },
                selected = selected,
                onClick = {
                    if (tab is BottomBarTab.Settings) {
                        scope.launch {
                            drawerState.open()
                        }
                    } else if (currentRoute != tab.route) {
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                },
                alwaysShowLabel = true,
                modifier = Modifier
                    .padding(horizontal = 10.dp),
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    selectedIndicatorColor = MaterialTheme.colorScheme.secondary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            )
        }
    }
}