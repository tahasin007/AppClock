package com.android.appclock.presentation.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.appclock.core.common.ScheduleValidity
import com.android.appclock.domain.model.ScheduleStatus
import com.android.appclock.presentation.common.uiColor
import com.android.appclock.presentation.common.uiIcon

@Composable
fun CustomAppBarEditScreen(
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onChangeScheduleStatus: (ScheduleStatus) -> Unit,
    onSaveClick: () -> Unit,
    validityState: ScheduleValidity,
    scheduleStatus: ScheduleStatus,
    isNewSchedule: Boolean,
    title: String? = null,
    subtitle: String? = null,
    showActions: Boolean = true,
    showStatusChips: Boolean = true
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text(
                        text = title ?: if (isNewSchedule) "Create schedule" else "Edit schedule",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = subtitle ?: if (isNewSchedule) {
                            "Choose an app and time to build a new launch routine."
                        } else {
                            "Update timing, app, or status before saving your changes."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (showActions) {
            val onSaveAction = {
                if (validityState != ScheduleValidity.VALID) {
                    Toast.makeText(context, validityState.message, Toast.LENGTH_SHORT).show()
                } else {
                    onSaveClick()
                }
            }

            if (isNewSchedule) {
                FilledTonalButton(
                    onClick = onSaveAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (validityState == ScheduleValidity.VALID) 1f else 0.65f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save",
                        modifier = Modifier.size(18.dp)
                    )
                    Text(text = "Save", modifier = Modifier.padding(start = 6.dp))
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = onSaveAction,
                        modifier = Modifier
                            .weight(1f)
                            .alpha(if (validityState == ScheduleValidity.VALID) 1f else 0.65f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Save", modifier = Modifier.padding(start = 6.dp))
                    }

                    FilledTonalButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Delete", modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }

            if (!isNewSchedule && showStatusChips) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = { onChangeScheduleStatus(ScheduleStatus.UPCOMING) },
                        label = { Text("Upcoming") },
                        leadingIcon = {
                            Icon(
                                imageVector = ScheduleStatus.UPCOMING.uiIcon,
                                contentDescription = null,
                                tint = ScheduleStatus.UPCOMING.uiColor,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (scheduleStatus == ScheduleStatus.UPCOMING) {
                                ScheduleStatus.UPCOMING.uiColor.copy(alpha = 0.14f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            }
                        )
                    )

                    AssistChip(
                        onClick = { onChangeScheduleStatus(ScheduleStatus.CANCELED) },
                        label = { Text("Canceled") },
                        leadingIcon = {
                            Icon(
                                imageVector = ScheduleStatus.CANCELED.uiIcon,
                                contentDescription = null,
                                tint = ScheduleStatus.CANCELED.uiColor,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (scheduleStatus == ScheduleStatus.CANCELED) {
                                ScheduleStatus.CANCELED.uiColor.copy(alpha = 0.14f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            }
                        )
                    )
                }
            }
        }
    }
}
