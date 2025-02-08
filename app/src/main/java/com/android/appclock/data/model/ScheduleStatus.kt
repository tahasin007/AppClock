package com.android.appclock.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ScheduleStatus(
    val statusIcon: ImageVector,
    val statusColor: Color
) {
    NONE(
        statusIcon = Icons.Default.TimerOff,
        statusColor = Color.Black
    ),
    UPCOMING(
        statusIcon = Icons.Default.Event,
        statusColor = Color(0xFF168B17)
    ),
    CANCELED(
        statusIcon = Icons.Default.Cancel,
        statusColor = Color.Red
    ),
    LAUNCHED(
        statusIcon = Icons.Outlined.Verified,
        statusColor = Color(0xFF1B711B)
    ),
    FAILED(
        statusIcon = Icons.Default.Report,
        statusColor = Color.Red
    )
}
