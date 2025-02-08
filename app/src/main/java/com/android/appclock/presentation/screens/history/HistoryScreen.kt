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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    viewModel: HistoryViewModel = hiltViewModel()
) {
    // Fetch all past schedules (for app that were launched of failed) from the ViewModel
    val allSchedules = viewModel.allSchedules

    val schedules = viewModel.schedulesState
    val selectedFilter = viewModel.selectedFilter.value

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
                CommonActionBar(
                    title = "Previous Schedules",
                    onBackClick = {
                        navController.popBackStack()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Filter Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Start
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

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(schedules.size) {
                        ScheduleListItem(schedule = schedules[it]) {}
                    }
                }
            }
        }
    }
}