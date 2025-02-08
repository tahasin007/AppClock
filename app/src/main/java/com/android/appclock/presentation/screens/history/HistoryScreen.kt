package com.android.appclock.presentation.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.android.appclock.presentation.components.CommonActionBar
import com.android.appclock.presentation.components.EmptyDataView
import com.android.appclock.presentation.components.ScheduleListItem

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val schedules = remember { viewModel.schedulesState }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (schedules.isEmpty()) {
            EmptyDataView(message = "Past schedules will appear here, currently no schedules")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                CommonActionBar(
                    title = "Previous Schedule",
                    onBackClick = {
                        navController.popBackStack()
                    }
                )

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