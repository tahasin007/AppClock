package com.android.appclock.presentation.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.presentation.components.EmptyDataView
import com.android.appclock.presentation.components.FilterButton
import com.android.appclock.presentation.components.ScheduleListItem
import com.android.appclock.ui.theme.ClockBlue
import com.android.appclock.ui.theme.ClockBlueDark
import com.android.appclock.ui.theme.ClockCyan

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel(),
    onSelectionModeChange: ((Boolean) -> Unit)? = null
) {
    val allSchedules = viewModel.allSchedules
    val schedules = viewModel.schedulesState
    val selectedFilter = viewModel.selectedFilter.value
    val isSelectionMode = viewModel.isSelectionMode
    val selectedCount = viewModel.selectedScheduleIds.size
    val launchedCount = allSchedules.count { it.status == ScheduleStatus.LAUNCHED }
    val failedCount = allSchedules.count { it.status == ScheduleStatus.FAILED }

    LaunchedEffect(isSelectionMode) {
        onSelectionModeChange?.invoke(isSelectionMode)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            if (isSelectionMode) {
                SelectionActionBar(
                    selectedCount = selectedCount,
                    onDelete = { viewModel.onEvent(HistoryScreenEvent.DeleteSelected) },
                    onCancel = { viewModel.onEvent(HistoryScreenEvent.ClearSelection) }
                )
            } else {
                HistoryTopBar(onBackClick = { navController.popBackStack() })
                Spacer(modifier = Modifier.height(16.dp))
                HistoryHeroCard(
                    launchedCount = launchedCount,
                    failedCount = failedCount
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isSelectionMode && allSchedules.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterButton(
                        text = "Launched",
                        isSelected = selectedFilter.scheduleStatus == ScheduleStatus.LAUNCHED,
                        onClick = { viewModel.onEvent(HistoryScreenEvent.ShowLaunched) }
                    )
                    FilterButton(
                        text = "Failed",
                        isSelected = selectedFilter.scheduleStatus == ScheduleStatus.FAILED,
                        onClick = { viewModel.onEvent(HistoryScreenEvent.ShowFailed) }
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (allSchedules.isEmpty()) {
                EmptyDataView(
                    message = "No launch history yet. Completed and failed schedules will appear here."
                )
            } else if (schedules.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = "No items in this filter.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(items = schedules, key = { schedule -> schedule.id }) { schedule ->
                        val isSelected = viewModel.selectedScheduleIds.contains(schedule.id)
                        ScheduleListItem(
                            schedule = schedule,
                            appIconLoader = viewModel.appIconLoader,
                            onClick = {
                                viewModel.onEvent(
                                    HistoryScreenEvent.ToggleSelection(
                                        schedule.id
                                    )
                                )
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
private fun HistoryTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
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
                    text = "Launch history",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Review completed and failed launches.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HistoryHeroCard(
    launchedCount: Int,
    failedCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(30.dp))
            .background(
                Brush.linearGradient(colors = listOf(ClockBlueDark, ClockBlue, ClockCyan))
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Execution overview",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HistoryMetric(
                    icon = Icons.Default.Verified,
                    label = "Launched",
                    value = launchedCount
                )
                HistoryMetric(
                    icon = Icons.Default.Warning,
                    label = "Failed",
                    value = failedCount
                )
            }
        }
    }
}

@Composable
private fun RowScope.HistoryMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: Int
) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.14f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SelectionActionBar(
    selectedCount: Int,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$selectedCount selected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Delete", modifier = Modifier.padding(start = 6.dp))
                    }

                    FilledTonalButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Done", modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }
    }
}