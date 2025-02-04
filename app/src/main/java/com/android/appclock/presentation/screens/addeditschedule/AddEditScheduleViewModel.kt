package com.android.appclock.presentation.screens.addeditschedule

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.android.appclock.data.model.ScheduleData
import com.android.appclock.data.model.ScheduleStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AddEditScheduleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var schedule = mutableStateOf(
        ScheduleData(
            id = savedStateHandle.get<Int>("scheduleId"),
            appName = "",
            packageName = "",
            scheduledTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
            scheduledDate = LocalDate.now().format(dateFormatter),
            description = "",
            status = ScheduleStatus.UPCOMING
        )
    )
        private set

    fun updateDate(newDate: String) {
        try {
            val parsedDate = LocalDate.parse(newDate, dateFormatter)
            schedule.value = schedule.value.copy(scheduledDate = parsedDate.format(dateFormatter))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("DefaultLocale")
    fun updateTime(hour: Int, minute: Int) {
        val formattedTime = String.format("%02d:%02d", hour, minute)
        schedule.value = schedule.value.copy(scheduledTime = formattedTime)
    }

    fun updateDescription(newDescription: String) {
        schedule.value = schedule.value.copy(description = newDescription)
    }

    fun updateApp(appName: String, packageName: String) {
        schedule.value = schedule.value.copy(appName = appName, packageName = packageName)
    }
}
