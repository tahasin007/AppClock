package com.android.appclock.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DockedDatePicker(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.toEpochDay() * 86400000
    )

    // Synchronize selectedDate with datePickerState
    LaunchedEffect(selectedDate) {
        datePickerState.selectedDateMillis = selectedDate.toEpochDay() * 86400000
    }

    // Listen for changes in datePickerState
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val selectedLocalDate = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            if (selectedLocalDate != selectedDate) {
                onDateSelected(selectedLocalDate)
            }
        }
    }

    DatePicker(
        title = null,
        state = datePickerState,
        showModeToggle = false,
        modifier = Modifier.fillMaxWidth(),
        colors = DatePickerDefaults.colors(
            selectedDayContainerColor = if (selectedDate.isBefore(today)) {
                Color.Red
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    )
}

