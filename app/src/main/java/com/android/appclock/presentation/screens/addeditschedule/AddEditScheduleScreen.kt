package com.android.appclock.presentation.screens.addeditschedule

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.appclock.core.common.ScheduleValidity
import com.android.appclock.data.model.RecurringType
import com.android.appclock.presentation.common.SchedulesDataUI
import com.android.appclock.presentation.components.AppBasicTextField
import com.android.appclock.presentation.components.AppIconImage
import com.android.appclock.presentation.components.CustomAppBarEditScreen
import com.android.appclock.presentation.components.DockedDatePicker
import com.android.appclock.presentation.components.SectionCard
import com.android.appclock.presentation.components.SectionHeader
import com.android.appclock.presentation.components.SelectableCard
import com.android.appclock.presentation.components.StatusChip
import com.android.appclock.presentation.components.TimePickerDialog
import com.android.appclock.ui.theme.ClockBlue
import com.android.appclock.ui.theme.ClockBlueDark
import com.android.appclock.ui.theme.ClockCyan
import com.android.appclock.utils.Constants.SCHEDULE_ID_DEFAULT
import java.time.LocalDate
import java.time.LocalTime

@SuppressLint("DefaultLocale")
@Composable
fun AddEditScheduleScreen(
    navController: NavController,
    viewModel: AddEditScheduleViewModel = hiltViewModel()
) {
    // Load installed apps lazily only when needed
    LaunchedEffect(Unit) {
        viewModel.loadInstalledAppsIfNeeded()
        // Start validation after screen is composable
        viewModel.startValidation()
    }

    val schedule = viewModel.editScheduleState.value
    val installedApps = viewModel.installedApps

    val expanded = viewModel.expanded.value
    val showTimePicker = viewModel.showTimePicker.value
    var appSearchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(appSearchQuery, installedApps) {
        if (appSearchQuery.isBlank()) {
            installedApps
        } else {
            installedApps.filter { app ->
                app.appName.contains(appSearchQuery, ignoreCase = true)
            }
        }
    }

    val validityState = viewModel.validityState.value
    val isNewSchedule = schedule.id == SCHEDULE_ID_DEFAULT

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            CustomAppBarEditScreen(
                onBackClick = { navController.popBackStack() },
                onDeleteClick = {
                    viewModel.onEvent(AddEditScheduleEvent.DeleteSchedule)
                    navController.popBackStack()
                },
                onChangeScheduleStatus = {
                    viewModel.onEvent(AddEditScheduleEvent.ChangeScheduleStatus(it))
                },
                onSaveClick = {
                    viewModel.onEvent(AddEditScheduleEvent.SaveSchedule)
                    navController.popBackStack()
                },
                validityState = validityState,
                scheduleStatus = schedule.status,
                isNewSchedule = isNewSchedule
            )

            Spacer(modifier = Modifier.height(18.dp))

            AddEditHeroCard(
                schedule = schedule,
                validityState = validityState,
                isNewSchedule = isNewSchedule,
                appIconLoader = viewModel.appIconLoader
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(
                title = "App & timing",
                subtitle = "Pick the app, date, and time for this launch."
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                SelectableCard(
                    title = "Selected app",
                    value = schedule.appName.ifEmpty { "Choose an installed app" },
                    supportingText = if (schedule.packageName.isBlank()) {
                        "Tap to browse installed apps"
                    } else {
                        schedule.packageName
                    },
                    packageName = schedule.packageName,
                    appIconLoader = viewModel.appIconLoader,
                    leadingIcon = if (schedule.packageName.isBlank()) Icons.Default.Apps else null,
                    onClick = { viewModel.toggleDropdown() }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        appSearchQuery = ""
                        viewModel.toggleDropdown()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(420.dp)
                ) {
                    OutlinedTextField(
                        value = appSearchQuery,
                        onValueChange = { appSearchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        singleLine = true,
                        label = { Text("Search installed apps") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (installedApps.isEmpty()) {
                        Text(
                            text = "Loading installed apps…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    } else if (filteredApps.isEmpty()) {
                        Text(
                            text = "No apps match \"$appSearchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .height(340.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            filteredApps.forEach { app ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(text = app.appName)
                                            Text(
                                                text = app.packageName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    leadingIcon = {
                                        AppIconImage(
                                            packageName = app.packageName,
                                            contentDescription = app.appName,
                                            appIconLoader = viewModel.appIconLoader,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    },
                                    onClick = {
                                        viewModel.onEvent(AddEditScheduleEvent.EnteredApp(app))
                                        appSearchQuery = ""
                                        viewModel.toggleDropdown()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            SectionCard(
                title = "Launch date",
                subtitle = "Choose the day this schedule should fire.",
                leadingIcon = Icons.Default.CalendarMonth
            ) {
                DockedDatePicker(LocalDate.parse(schedule.scheduledDate)) {
                    viewModel.onEvent(AddEditScheduleEvent.EnteredDate(it.toString()))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            SelectableCard(
                title = "Launch time",
                value = schedule.scheduledTime,
                supportingText = "Tap to open the time picker",
                leadingIcon = Icons.Default.AccessAlarm,
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                onClick = { viewModel.showTimePicker() }
            )

            if (showTimePicker) {
                TimePickerDialog(
                    initialHour = LocalTime.parse(schedule.scheduledTime).hour,
                    initialMinute = LocalTime.parse(schedule.scheduledTime).minute,
                    onTimeSelected = { hour, minute ->
                        viewModel.onEvent(AddEditScheduleEvent.EnteredTime(hour, minute))
                    },
                    onDismissRequest = { viewModel.hideTimePicker() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SectionCard(
                title = "Repeat",
                subtitle = "Set how often this schedule should repeat.",
                leadingIcon = Icons.Default.Repeat
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RecurringType.entries.forEach { type ->
                        FilterChip(
                            selected = schedule.recurringType == type,
                            onClick = {
                                viewModel.onEvent(AddEditScheduleEvent.EnteredRecurringType(type))
                            },
                            label = {
                                Text(
                                    text = type.displayName,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionCard(
                title = "Notes",
                subtitle = "Add an optional reminder for why this launch matters.",
                leadingIcon = Icons.AutoMirrored.Filled.Notes
            ) {
                AppBasicTextField(
                    description = schedule.description,
                    onValueChange = { viewModel.onEvent(AddEditScheduleEvent.EnteredDescription(it)) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AddEditHeroCard(
    schedule: SchedulesDataUI,
    validityState: ScheduleValidity,
    isNewSchedule: Boolean,
    appIconLoader: com.android.appclock.utils.AppIconLoader
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ClockBlueDark, ClockBlue, ClockCyan)
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.14f)
            ) {
                Text(
                    text = if (isNewSchedule) "Create schedule" else "Edit schedule",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = schedule.appName.ifBlank { "Choose the app you want to launch." },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${schedule.scheduledDate} • ${schedule.scheduledTime}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            StatusChip(schedule.status)

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedCard(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (schedule.packageName.isBlank()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    } else {
                        AppIconImage(
                            packageName = schedule.packageName,
                            contentDescription = schedule.appName,
                            appIconLoader = appIconLoader,
                            modifier = Modifier.size(52.dp),
                            iconSize = 52.dp
                        )
                    }

                    Spacer(modifier = Modifier.size(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Schedule summary",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (validityState) {
                                ScheduleValidity.VALID -> "Everything looks good. You can save this schedule."
                                else -> validityState.message
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}



