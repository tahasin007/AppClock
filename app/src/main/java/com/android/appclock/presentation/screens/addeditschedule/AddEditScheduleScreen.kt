package com.android.appclock.presentation.screens.addeditschedule

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.appclock.presentation.components.AppIconImage
import com.android.appclock.presentation.components.AppBasicTextField
import com.android.appclock.presentation.components.CustomAppBarEditScreen
import com.android.appclock.presentation.components.DockedDatePicker
import com.android.appclock.presentation.components.SelectableCard
import com.android.appclock.presentation.components.TimePickerDialog
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
        Spacer(modifier = Modifier.height(10.dp))

        // App Selection View
        Box(modifier = Modifier.fillMaxWidth()) {
            SelectableCard(
                title = "Select App",
                value = schedule.appName.ifEmpty { "Select App" },
                packageName = schedule.packageName,
                appIconLoader = viewModel.appIconLoader,
                onClick = { viewModel.toggleDropdown() }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    appSearchQuery = ""
                    viewModel.toggleDropdown()
                },
                modifier = Modifier
                    .fillMaxWidth(0.91f)
                    .height(400.dp)
            ) {
                OutlinedTextField(
                    value = appSearchQuery,
                    onValueChange = { appSearchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    singleLine = true,
                    label = { Text("Search apps") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredApps.isEmpty()) {
                    Text(
                        text = "No apps found",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .height(330.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        filteredApps.forEach { app ->
                            DropdownMenuItem(
                                text = { Text(app.appName) },
                                leadingIcon = {
                                    AppIconImage(
                                        packageName = app.packageName,
                                        contentDescription = app.appName,
                                        appIconLoader = viewModel.appIconLoader,
                                        modifier = Modifier.size(24.dp)
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

        Spacer(modifier = Modifier.height(16.dp))
        DockedDatePicker(LocalDate.parse(schedule.scheduledDate)) {
            viewModel.onEvent(AddEditScheduleEvent.EnteredDate(it.toString()))
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Time Selection
        SelectableCard(
            title = "Time",
            value = schedule.scheduledTime,
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

        Spacer(modifier = Modifier.height(16.dp))

        AppBasicTextField(
            description = schedule.description,
            onValueChange = { viewModel.onEvent(AddEditScheduleEvent.EnteredDescription(it)) }
        )
    }
}
