package com.android.appclock.presentation.screens.usagemonitoring

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.appclock.core.common.ScheduleValidity
import com.android.appclock.core.utils.Constants.NAV_ARG_USAGE_MONITORING_RULE_ID
import com.android.appclock.domain.model.ScheduleStatus
import com.android.appclock.presentation.components.CustomAppBarEditScreen
import com.android.appclock.presentation.components.EmptyStateCard
import com.android.appclock.presentation.components.LoadingCard
import com.android.appclock.presentation.components.SectionHeader
import com.android.appclock.presentation.components.UsageAppCard
import com.android.appclock.presentation.components.UsageSummaryCard
import com.android.appclock.presentation.navigation.Screen

@Composable
fun UsageMonitoringScreen(
    navController: NavController,
    viewModel: UsageMonitoringViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val trackedRules = viewModel.trackedRules
    val summary = viewModel.summaryState.value
    val isLoading = viewModel.isLoading.value

    LaunchedEffect(Unit) {
        viewModel.refreshUsageSnapshot()
    }

    DisposableEffect(Unit) {
        viewModel.startRefreshing()
        onDispose { viewModel.stopRefreshing() }
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
            showActions = false,
            showStatusChips = false
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

            if (trackedRules.isNotEmpty() || !summary.hasUsageAccess) {
                item {
                    UsageSummaryCard(
                        foregroundAppName = summary.foregroundAppName,
                        totalUsageTodayLabel = summary.totalUsageTodayLabel,
                        reachedLimitCount = summary.reachedLimitCount,
                        nearLimitCount = summary.nearLimitCount
                    )
                }
            }

            when {
                isLoading && trackedRules.isEmpty() -> {
                    item {
                        LoadingCard(
                            title = "Loading tracked apps",
                            subtitle = "Refreshing monitored apps and today’s foreground usage."
                        )
                    }
                }

                trackedRules.isEmpty() -> {
                    item {
                        EmptyStateCard(
                            title = "No tracked apps yet",
                            subtitle = "Add your first monitored app to set a daily limit and notifications.",
                            buttonText = "Add monitored app",
                            onButtonClick = { navController.navigate(Screen.AddEditUsageMonitoring.route) }
                        )
                    }
                }

                else -> {
                    items(trackedRules, key = { it.id }) { rule ->
                        UsageAppCard(
                            appName = rule.appName,
                            usageLabel = rule.usageLabel,
                            limitLabel = rule.limitLabel,
                            progress = rule.progress,
                            isForeground = rule.isForeground,
                            statusLabel = rule.statusLabel,
                            onSetLimitClick = {
                                navController.navigate(
                                    Screen.AddEditUsageMonitoring.route +
                                            "?$NAV_ARG_USAGE_MONITORING_RULE_ID=${rule.id}"
                                )
                            }
                        )
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (summary.hasUsageAccess) {
                                "Usage access is enabled. Daily counters reset each day and foreground usage is refreshed while this screen is open."
                            } else {
                                "Usage access is required to calculate foreground screen time for your monitored apps."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (!summary.hasUsageAccess) {
                            FilledTonalButton(
                                onClick = {
                                    context.startActivity(
                                        Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                    )
                                }
                            ) {
                                Text("Grant usage access")
                            }
                        }
                    }
                }
            }
        }
    }
}
