package com.android.appclock.presentation.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.appclock.presentation.components.ScheduleListItem
import com.android.appclock.presentation.screens.ScheduleViewModel

@Composable
fun HistoryScreen(viewModel: ScheduleViewModel) {
    val schedules = remember { viewModel.schedulesState }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp)
    ) {
        LazyColumn {
            items(schedules.size) {
                ScheduleListItem(schedule = schedules[it]) {}
            }
        }
    }
}