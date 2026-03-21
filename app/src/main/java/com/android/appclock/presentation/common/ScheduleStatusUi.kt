package com.android.appclock.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.appclock.domain.model.ScheduleStatus

val ScheduleStatus.uiIcon: ImageVector
    get() = when (this) {
        ScheduleStatus.NONE -> Icons.Default.TimerOff
        ScheduleStatus.UPCOMING -> Icons.Default.Event
        ScheduleStatus.CANCELED -> Icons.Default.Cancel
        ScheduleStatus.LAUNCHED -> Icons.Outlined.Verified
        ScheduleStatus.FAILED -> Icons.Default.Report
    }

val ScheduleStatus.uiColor: Color
    get() = when (this) {
        ScheduleStatus.NONE -> Color.Black
        ScheduleStatus.UPCOMING -> Color(0xFF168B17)
        ScheduleStatus.CANCELED -> Color.Red
        ScheduleStatus.LAUNCHED -> Color(0xFF1B711B)
        ScheduleStatus.FAILED -> Color.Red
    }

