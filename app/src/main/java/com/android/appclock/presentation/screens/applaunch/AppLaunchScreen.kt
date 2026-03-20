package com.android.appclock.presentation.screens.applaunch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.appclock.core.common.ScheduleValidity
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.presentation.common.SchedulesDataUI
import com.android.appclock.presentation.components.CustomAppBarEditScreen
import com.android.appclock.presentation.components.EmptyStateCard
import com.android.appclock.presentation.components.GradientHeroCard
import com.android.appclock.presentation.components.HeroMetricCard
import com.android.appclock.presentation.components.LoadingCard
import com.android.appclock.presentation.components.PackageInfoCard
import com.android.appclock.presentation.components.ScheduleListItem
import com.android.appclock.presentation.navigation.Screen
import com.android.appclock.ui.theme.ClockBlue
import com.android.appclock.ui.theme.ClockBlueDark
import com.android.appclock.ui.theme.ClockCyan
import com.android.appclock.ui.theme.ClockMint
import com.android.appclock.utils.AppIconLoader
import com.android.appclock.utils.Constants.NAV_ARG_SCHEDULE_ID

@Composable
fun AppLaunchScreen(
    navController: NavController,
    viewModel: AppLaunchViewModel = hiltViewModel()
) {
    val schedules = viewModel.schedulesState
    val isLoading = viewModel.isLoading.value
    val upcomingCount = schedules.count { it.status == ScheduleStatus.UPCOMING }
    val canceledCount = schedules.count { it.status == ScheduleStatus.CANCELED }
    val nextSchedule = schedules.firstOrNull { it.status == ScheduleStatus.UPCOMING }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        CustomAppBarEditScreen(
            onBackClick = { navController.popBackStack() },
            onDeleteClick = {},
            onChangeScheduleStatus = {},
            onSaveClick = {},
            validityState = ScheduleValidity.VALID,
            scheduleStatus = ScheduleStatus.UPCOMING,
            isNewSchedule = true,
            title = "App launch",
            subtitle = "Manage scheduled app launches and history.",
            showActions = false
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                AppLaunchHeroCard(
                    nextSchedule = nextSchedule,
                    upcomingCount = upcomingCount,
                    canceledCount = canceledCount,
                    appIconLoader = viewModel.appIconLoader,
                    onAddSchedule = { navController.navigate(Screen.AddEditSchedule.route) },
                    onOpenHistory = { navController.navigate(Screen.History.route) }
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Your launch queue",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (schedules.isEmpty()) {
                            "Create your first scheduled launch to turn AppClock into a focused routine tool."
                        } else {
                            "Upcoming schedules stay on the home screen, while history remains one tap away."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when {
                isLoading -> {
                    item {
                        LoadingCard(
                            title = "Loading your schedules",
                            subtitle = "Refreshing upcoming launches and recent edits."
                        )
                    }
                }

                schedules.isEmpty() -> {
                    item {
                        EmptyStateCard(
                            title = "No schedules yet",
                            subtitle = "Tap the button below to schedule your first app launch.",
                            buttonText = "Create my first schedule",
                            onButtonClick = { navController.navigate(Screen.AddEditSchedule.route) }
                        )
                    }
                }

                else -> {
                    items(items = schedules, key = { schedule -> schedule.id }) { schedule ->
                        ScheduleListItem(
                            schedule = schedule,
                            appIconLoader = viewModel.appIconLoader,
                            onClick = {
                                navController.navigate(
                                    Screen.AddEditSchedule.route + "?$NAV_ARG_SCHEDULE_ID=${schedule.id}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppLaunchHeroCard(
    nextSchedule: SchedulesDataUI?,
    upcomingCount: Int,
    canceledCount: Int,
    appIconLoader: AppIconLoader,
    onAddSchedule: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val heroTitleColor = Color.White
    val heroBodyColor = Color.White.copy(alpha = 0.9f)

    GradientHeroCard(
        gradientColors = listOf(ClockBlueDark, ClockBlue, ClockCyan),
        verticalSpacing = 18.dp
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = ClockMint.copy(alpha = 0.18f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = heroTitleColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Reliable app launch automation",
                    color = heroTitleColor,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HeroMetricCard(
                title = "Upcoming",
                value = upcomingCount.toString(),
                modifier = Modifier.weight(1f)
            )
            HeroMetricCard(
                title = "Canceled",
                value = canceledCount.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Next up",
                style = MaterialTheme.typography.labelLarge,
                color = heroBodyColor
            )
            PackageInfoCard(
                title = nextSchedule?.let { "${it.scheduledDate} • ${it.scheduledTime}" }
                    ?: "No launch queued yet",
                appName = nextSchedule?.appName
                    ?: "Add a schedule to see the next app launch here.",
                packageName = nextSchedule?.packageName.orEmpty(),
                appIconLoader = appIconLoader,
                containerColor = Color.Black.copy(alpha = 0.22f),
                titleColor = heroTitleColor,
                bodyColor = heroBodyColor
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onAddSchedule) {
                Text(text = "New schedule")
            }
            TextButton(onClick = onOpenHistory) {
                Text(
                    text = "View history",
                    color = heroTitleColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = heroTitleColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
