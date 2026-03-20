package com.android.appclock.presentation.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.presentation.components.CommonActionBar
import com.android.appclock.presentation.components.EmptyDataView
import com.android.appclock.presentation.components.FilterButton
import com.android.appclock.presentation.components.ScheduleListItem

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel(),
    onSelectionModeChange: ((Boolean) -> Unit)? = null
) {
    // Fetch all past schedules (for app that were launched of failed) from the ViewModel
    val allSchedules = viewModel.allSchedules

    val schedules = viewModel.schedulesState
    val selectedFilter = viewModel.selectedFilter.value
    val isSelectionMode = viewModel.isSelectionMode
    val selectedCount = viewModel.selectedScheduleIds.size

    // Notify parent about selection mode change
    LaunchedEffect(isSelectionMode) {
        onSelectionModeChange?.invoke(isSelectionMode)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (allSchedules.isEmpty()) {
            EmptyDataView(message = "Past schedules will appear here, currently no schedules")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                // Conditional Action Bar
                if (isSelectionMode) {
                    // Selection Mode Action Bar
                    SelectionActionBar(
                        selectedCount = selectedCount,
                        onDelete = { viewModel.onEvent(HistoryScreenEvent.DeleteSelected) },
                        onCancel = { viewModel.onEvent(HistoryScreenEvent.ClearSelection) }
                    )
                } else {
                    // Normal Action Bar
                    CommonActionBar(
                        title = "Previous Schedules", onBackClick = {
                            navController.popBackStack()
                        })
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filter Buttons (hidden in selection mode)
                if (!isSelectionMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        FilterButton(
                            text = "Launched",
                            isSelected = selectedFilter.scheduleStatus == ScheduleStatus.LAUNCHED,
                            onClick = { viewModel.onEvent(HistoryScreenEvent.ShowLaunched) })

                        FilterButton(
                            text = "Failed",
                            isSelected = selectedFilter.scheduleStatus == ScheduleStatus.FAILED,
                            onClick = { viewModel.onEvent(HistoryScreenEvent.ShowFailed) })
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                LazyColumn {
                    items(
                        items = schedules, 
                        key = { schedule -> schedule.id }
                    ) { schedule ->
                        val isSelected = viewModel.selectedScheduleIds.contains(schedule.id)
                        
                        ScheduleListItem(
                            schedule = schedule,
                            appIconLoader = viewModel.appIconLoader,
                            onClick = {
                                viewModel.onEvent(HistoryScreenEvent.ToggleSelection(schedule.id))
                            },
                            isSelected = isSelected,
                            showCheckbox = isSelectionMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionActionBar(
    selectedCount: Int,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$selectedCount selected",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}