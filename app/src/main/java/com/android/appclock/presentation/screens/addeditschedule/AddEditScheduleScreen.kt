package com.android.appclock.presentation.screens.addeditschedule

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
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
    val schedule = viewModel.editScheduleState.value
    val installedApps = viewModel.installedApps

    val expanded = viewModel.expanded.value
    val showTimePicker = viewModel.showTimePicker.value

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
                icon = schedule.appIcon,
                onClick = { viewModel.toggleDropdown() }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { viewModel.toggleDropdown() },
                modifier = Modifier
                    .fillMaxWidth(0.91f)
                    .height(400.dp)
            ) {
                installedApps.forEach { app ->
                    DropdownMenuItem(
                        text = { Text(app.appName) },
                        leadingIcon = {
                            Image(
                                painter = rememberAsyncImagePainter(app.icon),
                                contentDescription = app.appName,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = {
                            viewModel.onEvent(AddEditScheduleEvent.EnteredApp(app))
                            viewModel.toggleDropdown()
                        }
                    )
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
