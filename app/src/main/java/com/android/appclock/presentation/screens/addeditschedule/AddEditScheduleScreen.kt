package com.android.appclock.presentation.screens.addeditschedule

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.appclock.core.common.ScheduleValidity
import com.android.appclock.data.model.RecurringType
import com.android.appclock.presentation.common.InstalledAppUI
import com.android.appclock.presentation.common.SchedulesDataUI
import com.android.appclock.presentation.components.AppBasicTextField
import com.android.appclock.presentation.components.CustomAppBarEditScreen
import com.android.appclock.presentation.components.DockedDatePicker
import com.android.appclock.presentation.components.GradientHeroCard
import com.android.appclock.presentation.components.HeroLabelSurface
import com.android.appclock.presentation.components.InstalledAppSelectorField
import com.android.appclock.presentation.components.PackageInfoCard
import com.android.appclock.presentation.components.SectionCard
import com.android.appclock.presentation.components.SectionHeader
import com.android.appclock.presentation.components.SelectableCard
import com.android.appclock.presentation.components.StatusChip
import com.android.appclock.presentation.components.TimePickerDialog
import com.android.appclock.ui.theme.ClockBlue
import com.android.appclock.ui.theme.ClockBlueDark
import com.android.appclock.ui.theme.ClockCyan
import com.android.appclock.utils.AppIconLoader
import com.android.appclock.utils.Constants.SCHEDULE_ID_DEFAULT
import java.time.LocalDate
import java.time.LocalTime

@SuppressLint("DefaultLocale")
@Composable
fun AddEditScheduleScreen(
    navController: NavController,
    viewModel: AddEditScheduleViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadInstalledAppsIfNeeded()
        viewModel.startValidation()
    }

    val schedule = viewModel.editScheduleState.value
    val installedApps = viewModel.installedApps
    val expanded = viewModel.expanded.value
    val showTimePicker = viewModel.showTimePicker.value
    val validityState = viewModel.validityState.value
    val isNewSchedule = schedule.id == SCHEDULE_ID_DEFAULT
    val selectedApp = installedApps.firstOrNull { it.packageName == schedule.packageName }
        ?: schedule.packageName.takeIf { it.isNotBlank() }?.let {
            InstalledAppUI(appName = schedule.appName, packageName = it)
        }

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

            InstalledAppSelectorField(
                selectedApp = selectedApp,
                installedApps = installedApps,
                expanded = expanded,
                appIconLoader = viewModel.appIconLoader,
                onExpandRequest = { viewModel.toggleDropdown() },
                onDismissRequest = { viewModel.toggleDropdown() },
                onAppSelected = { app ->
                    viewModel.onEvent(AddEditScheduleEvent.EnteredApp(app))
                    viewModel.toggleDropdown()
                },
                title = "Selected app",
                loadingText = "Loading installed apps…"
            )

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
    appIconLoader: AppIconLoader
) {
    val heroTitleColor = Color.White
    val heroBodyColor = Color.White.copy(alpha = 0.9f)

    GradientHeroCard(
        gradientColors = listOf(ClockBlueDark, ClockBlue, ClockCyan),
        verticalSpacing = 0.dp
    ) {
        Column {
            HeroLabelSurface(
                text = if (isNewSchedule) "Create schedule" else "Edit schedule"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = schedule.appName.ifBlank { "Choose the app you want to launch." },
                style = MaterialTheme.typography.headlineMedium,
                color = heroTitleColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${schedule.scheduledDate} • ${schedule.scheduledTime}",
                style = MaterialTheme.typography.bodyLarge,
                color = heroBodyColor
            )

            Spacer(modifier = Modifier.height(18.dp))

            StatusChip(schedule.status)

            Spacer(modifier = Modifier.height(16.dp))

            PackageInfoCard(
                title = "Schedule summary",
                appName = when (validityState) {
                    ScheduleValidity.VALID -> "Everything looks good. You can save this schedule."
                    else -> validityState.message
                },
                packageName = schedule.packageName,
                appIconLoader = appIconLoader,
                containerColor = Color.Black.copy(alpha = 0.22f),
                titleColor = heroTitleColor,
                bodyColor = heroBodyColor
            )
        }
    }
}
