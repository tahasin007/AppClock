package com.android.appclock.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.presentation.common.SchedulesDataUI
import com.android.appclock.presentation.common.uiColor
import com.android.appclock.presentation.common.uiIcon
import com.android.appclock.utils.AppIconLoader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
@SuppressLint("SimpleDateFormat")
fun ScheduleListItem(
    schedule: SchedulesDataUI,
    appIconLoader: AppIconLoader,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    showCheckbox: Boolean = false
) {
    val (month, day) = remember(schedule.scheduledDate) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = try {
            dateFormat.parse(schedule.scheduledDate) ?: Date()
        } catch (_: Exception) {
            Date()
        }
        val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase()
        val day = SimpleDateFormat("dd", Locale.getDefault()).format(date)
        month to day
    }

    // Animate border width
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "borderWidth"
    )

    val cardElevation by animateDpAsState(
        targetValue = if (isSelected) 5.dp else 2.dp,
        animationSpec = tween(durationMillis = 200),
        label = "cardElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .clickable(onClick = onClick)
            .then(
                Modifier.border(
                    width = borderWidth,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(28.dp)
                )
            ),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                schedule.status == ScheduleStatus.LAUNCHED -> ScheduleStatus.LAUNCHED.uiColor.copy(
                    alpha = 0.08f
                )

                schedule.status == ScheduleStatus.FAILED -> ScheduleStatus.FAILED.uiColor.copy(alpha = 0.08f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showCheckbox) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = if (isSelected) "Selected" else "Not Selected",
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.38f
                    ),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 12.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
            ) {
                AppIconImage(
                    packageName = schedule.packageName,
                    contentDescription = schedule.appName,
                    appIconLoader = appIconLoader,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .padding(10.dp),
                    iconSize = 48.dp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = schedule.appName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    StatusPill(status = schedule.status)
                }

                Text(
                    text = schedule.description?.takeIf { it.isNotBlank() } ?: schedule.packageName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        letterSpacing = 0.5.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = schedule.scheduledTime,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = month,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = day,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(status: ScheduleStatus) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = status.uiColor.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = status.uiIcon,
                contentDescription = status.name,
                modifier = Modifier.size(14.dp),
                tint = status.uiColor
            )
            Text(
                text = status.displayName(),
                style = MaterialTheme.typography.labelMedium,
                color = status.uiColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun ScheduleStatus.displayName(): String {
    val lowercase = name.lowercase(Locale.getDefault())
    return lowercase.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

