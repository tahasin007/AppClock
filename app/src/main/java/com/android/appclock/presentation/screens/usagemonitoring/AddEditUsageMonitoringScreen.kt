package com.android.appclock.presentation.screens.usagemonitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.appclock.domain.model.ScheduleStatus
import com.android.appclock.presentation.components.CustomAppBarEditScreen
import com.android.appclock.presentation.components.InstalledAppSelectorField
import com.android.appclock.presentation.components.SectionCard

@Composable
fun AddEditUsageMonitoringScreen(
    navController: NavController,
    viewModel: AddEditUsageMonitoringViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadInstalledAppsIfNeeded()
    }

    val uiState = viewModel.uiState.value
    val installedApps = viewModel.installedApps
    val expanded = viewModel.expanded.value
    val validityState = viewModel.validityState.value
    val isEditingRule = !uiState.isNewRule

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        CustomAppBarEditScreen(
            onBackClick = { navController.popBackStack() },
            onDeleteClick = { viewModel.deleteRule { navController.popBackStack() } },
            onChangeScheduleStatus = {},
            onSaveClick = { viewModel.saveRule { navController.popBackStack() } },
            validityState = validityState,
            scheduleStatus = ScheduleStatus.UPCOMING,
            isNewSchedule = uiState.isNewRule,
            title = if (isEditingRule) "Edit monitored app" else "Add monitored app",
            subtitle = "Pick one app and set its daily usage limit.",
            showActions = true,
            showStatusChips = false
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SectionCard(
                    title = "Selected app",
                    subtitle = "Choose the app to monitor.",
                    leadingIcon = Icons.Default.Apps
                ) {
                    InstalledAppSelectorField(
                        selectedApp = uiState.selectedApp,
                        installedApps = installedApps,
                        expanded = expanded,
                        appIconLoader = viewModel.appIconLoader,
                        onExpandRequest = { viewModel.toggleDropdown() },
                        onDismissRequest = { viewModel.toggleDropdown() },
                        onAppSelected = { app ->
                            viewModel.onAppSelected(app)
                            viewModel.toggleDropdown()
                        },
                        title = "Track app"
                    )
                }
            }

            item {
                SectionCard(
                    title = "Daily limit",
                    subtitle = "Set maximum usage time per day.",
                    leadingIcon = Icons.Default.Schedule
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = uiState.hours,
                            onValueChange = viewModel::onHoursChanged,
                            label = { Text("Hours") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = uiState.minutes,
                            onValueChange = viewModel::onMinutesChanged,
                            label = { Text("Minutes") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                SectionCard(
                    title = "Notifications",
                    subtitle = "Alert when usage approaches the daily limit.",
                    leadingIcon = Icons.Default.Notifications
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = uiState.notifyAt80Percent,
                            onClick = viewModel::toggleNotifyAt80,
                            label = { Text("Notify at 80%") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = uiState.notifyAt100Percent,
                            onClick = viewModel::toggleNotifyAt100,
                            label = { Text("Notify at 100%") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                SectionCard(
                    title = "Monitoring status",
                    subtitle = "Pause tracking without deleting this rule.",
                    leadingIcon = Icons.Default.Notifications
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (uiState.isActive) "Active" else "Paused",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (uiState.isActive) {
                                        "Foreground usage is monitored for this app."
                                    } else {
                                        "Tracking is paused until you enable it again."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = uiState.isActive,
                                onCheckedChange = viewModel::onActiveChanged
                            )
                        }
                    }
                }
            }
        }
    }
}
