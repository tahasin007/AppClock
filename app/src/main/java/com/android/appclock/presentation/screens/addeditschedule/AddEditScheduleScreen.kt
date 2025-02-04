package com.android.appclock.presentation.screens.addeditschedule

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.android.appclock.presentation.components.CustomAppBar
import com.android.appclock.presentation.components.DockedDatePicker
import com.android.appclock.presentation.components.TimePickerDialog
import com.android.appclock.utils.CommonUtil.getInstalledApps
import java.time.LocalDate
import java.time.LocalTime

@SuppressLint("DefaultLocale")
@Composable
fun AddEditScheduleScreen(
    navController: NavController,
    viewModel: AddEditScheduleViewModel = hiltViewModel()
) {
    val schedule = viewModel.schedule.value
    val context = LocalContext.current
    val apps = remember { getInstalledApps(context) }
    var expanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CustomAppBar(navController)
        Spacer(modifier = Modifier.height(10.dp))

        // App Selection
        Box(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
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
                                painter = rememberAsyncImagePainter(schedule.packageName),
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
                apps.forEach { app ->
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
                            viewModel.updateApp(app.appName, app.packageName)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        DockedDatePicker(LocalDate.parse(schedule.scheduledDate)) { viewModel.updateDate(it.toString()) }
        Spacer(modifier = Modifier.height(10.dp))

        // Time Selection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePicker = true },
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
                onTimeSelected = { hour, minute -> viewModel.updateTime(hour, minute) },
                onDismissRequest = { showTimePicker = false }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = schedule.description ?: "",
            onValueChange = { viewModel.updateDescription(it) },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp)),
            textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    if (schedule.description.isNullOrEmpty()) {
                        Text(
                            "Add Details",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
