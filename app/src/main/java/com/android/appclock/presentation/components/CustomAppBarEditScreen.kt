package com.android.appclock.presentation.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.appclock.core.common.ScheduleValidity
import com.android.appclock.data.model.ScheduleStatus
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
    isNewSchedule: Boolean
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Schedule Launch", style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }

        Row {
            if (!isNewSchedule) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (!isNewSchedule) {
                IconButton(
                    modifier = Modifier.then(
                        if (scheduleStatus == ScheduleStatus.CANCELED) {
                            Modifier.background(
                                color = ScheduleStatus.CANCELED.uiColor.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                        } else Modifier
                    ),
                    onClick = { onChangeScheduleStatus(ScheduleStatus.CANCELED) }) {
                    Icon(
                        imageVector = ScheduleStatus.CANCELED.uiIcon,
                        contentDescription = "Cancel",
                        tint = ScheduleStatus.CANCELED.uiColor,
                        modifier = Modifier.alpha(if (scheduleStatus == ScheduleStatus.CANCELED) 1f else 0.25f)
                    )
                }

                IconButton(
                    modifier = Modifier.then(
                        if (scheduleStatus == ScheduleStatus.UPCOMING) {
                            Modifier.background(
                                color = ScheduleStatus.UPCOMING.uiColor.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                        } else Modifier
                    ),
                    onClick = { onChangeScheduleStatus(ScheduleStatus.UPCOMING) }) {
                    Icon(
                        imageVector = ScheduleStatus.UPCOMING.uiIcon,
                        contentDescription = "Schedule",
                        tint = ScheduleStatus.UPCOMING.uiColor,
                        modifier = Modifier.alpha(if (scheduleStatus == ScheduleStatus.UPCOMING) 1f else 0.25f)
                    )
                }
            }
            IconButton(
                onClick = {
                    if (validityState != ScheduleValidity.VALID) {
                        Toast.makeText(context, validityState.message, Toast.LENGTH_SHORT).show()
                    } else onSaveClick()
                },
                modifier = Modifier.alpha(if (validityState == ScheduleValidity.VALID) 1f else 0.25f)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Save",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
