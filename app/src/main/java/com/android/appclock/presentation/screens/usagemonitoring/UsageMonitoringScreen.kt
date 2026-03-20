package com.android.appclock.presentation.screens.usagemonitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.appclock.core.common.ScheduleValidity
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.presentation.components.CustomAppBarEditScreen
import com.android.appclock.presentation.components.EmptyStateCard
import com.android.appclock.presentation.components.SectionHeader
import com.android.appclock.presentation.components.TrackedUsageRuleCard
import com.android.appclock.presentation.navigation.Screen

@Composable
fun UsageMonitoringScreen(
    navController: NavController,
    viewModel: UsageMonitoringViewModel = hiltViewModel()
) {
    val trackedApps = remember {
        listOf(
            TrackedUsageAppUi("Instagram", "com.instagram.android", "2h 00m", "Near limit"),
            TrackedUsageAppUi("YouTube", "com.google.android.youtube", "1h 30m", "In range")
        )
    }

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
            title = "Usage monitoring",
            subtitle = "Choose apps to monitor when they are in the foreground.",
            showActions = false
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader(
                    title = "Tracked apps",
                    subtitle = "One app = one rule. Add apps to monitor foreground usage and set daily limits."
                )
            }

            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate(Screen.AddEditUsageMonitoring.route) }
                ) {
                    Text("Add monitored app")
                }
            }

            if (trackedApps.isEmpty()) {
                item {
                    EmptyStateCard(
                        title = "No tracked apps yet",
                        subtitle = "Add your first monitored app to set a daily limit and notifications.",
                        buttonText = "Add monitored app",
                        onButtonClick = { navController.navigate(Screen.AddEditUsageMonitoring.route) }
                    )
                }
            } else {
                items(trackedApps, key = { it.packageName }) { app ->
                    TrackedUsageRuleCard(
                        appName = app.appName,
                        packageName = app.packageName,
                        dailyLimitLabel = app.dailyLimitLabel,
                        statusLabel = app.statusLabel,
                        appIconLoader = viewModel.appIconLoader,
                        onEditClick = { navController.navigate(Screen.AddEditUsageMonitoring.route) }
                    )
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Foreground monitoring uses usage access. Daily counters reset each day and history stays available.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private data class TrackedUsageAppUi(
    val appName: String,
    val packageName: String,
    val dailyLimitLabel: String,
    val statusLabel: String
)

