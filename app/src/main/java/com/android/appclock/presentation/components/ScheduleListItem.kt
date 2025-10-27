package com.android.appclock.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import coil.compose.rememberAsyncImagePainter
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.presentation.common.SchedulesDataUI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
@SuppressLint("SimpleDateFormat")
fun ScheduleListItem(
    schedule: SchedulesDataUI,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    showCheckbox: Boolean = false
) {
    val (month, day) = remember(schedule.scheduledDate) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = try {
            dateFormat.parse(schedule.scheduledDate) ?: Date()
        } catch (e: Exception) {
            Date()
        }
        val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date).uppercase()
        val day = SimpleDateFormat("dd", Locale.getDefault()).format(date)
        month to day
    }

    val interactionSource = remember { MutableInteractionSource() }
    
    // Animate border width
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "borderWidth"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .clickable(
                interactionSource = interactionSource, indication = null, onClick = onClick
            )
            .then(
                Modifier.border(
                    width = borderWidth,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(25.dp)
                )
            ),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                schedule.status == ScheduleStatus.LAUNCHED -> ScheduleStatus.LAUNCHED.statusColor.copy(
                    alpha = 0.05f
                )
                schedule.status == ScheduleStatus.FAILED -> ScheduleStatus.FAILED.statusColor.copy(
                    alpha = 0.05f
                )
                else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox/Selection Indicator
            if (showCheckbox) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = if (isSelected) "Selected" else "Not Selected",
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 12.dp)
                )
            }

            // Left column (app icon)
            schedule.appIcon?.let { iconBitmap ->
                Image(
                    painter = rememberAsyncImagePainter(iconBitmap),
                    contentDescription = schedule.appName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(8.dp)
                )
            } ?: Image(
                painter = rememberAsyncImagePainter(model = null),
                contentDescription = schedule.appName,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Middle column (app name, description, time)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = schedule.appName, style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        ), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 5.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = schedule.status.statusIcon,
                        contentDescription = schedule.status.name,
                        modifier = Modifier.size(18.dp),
                        tint = schedule.status.statusColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = schedule.status.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = schedule.status.statusColor, fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                if (!schedule.description.isNullOrEmpty()) {
                    Text(
                        text = schedule.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Text(
                    text = schedule.scheduledTime, style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            // Right colum (date, month column)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = month,
                    style = MaterialTheme.typography.bodyLarge,
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

