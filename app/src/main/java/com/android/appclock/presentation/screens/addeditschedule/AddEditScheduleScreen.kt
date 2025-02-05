package com.android.appclock.presentation.screens.addeditschedule

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.android.appclock.presentation.components.AppBasicTextField
import com.android.appclock.presentation.components.CustomAppBarEditScreen
import com.android.appclock.presentation.components.DockedDatePicker
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

    var expanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { expanded = !expanded }
                    ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(0.5.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (schedule.packageName.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(schedule.appIcon),
                                contentDescription = schedule.appName,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = schedule.appName.ifEmpty { "Select App" },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
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
                            expanded = false
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { showTimePicker = true }
                ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(0.5.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessAlarm,
                        contentDescription = "Clock",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        "Time",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        schedule.scheduledTime,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        if (showTimePicker) {
            TimePickerDialog(
                initialHour = LocalTime.parse(schedule.scheduledTime).hour,
                initialMinute = LocalTime.parse(schedule.scheduledTime).minute,
                onTimeSelected = { hour, minute ->
                    viewModel.onEvent(AddEditScheduleEvent.EnteredTime(hour, minute))
                },
                onDismissRequest = { showTimePicker = false }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppBasicTextField(
            description = schedule.description,
            onValueChange = { viewModel.onEvent(AddEditScheduleEvent.EnteredDescription(it)) }
        )
    }
}
