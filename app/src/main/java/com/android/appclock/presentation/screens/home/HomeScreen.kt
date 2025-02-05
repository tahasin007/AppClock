package com.android.appclock.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.appclock.presentation.components.PulsarIconButton
import com.android.appclock.presentation.components.ScheduleListItem
import com.android.appclock.presentation.navigation.Screen
import com.android.appclock.presentation.screens.ScheduleViewModel
import com.android.appclock.utils.Constants.NAV_ARG_SCHEDULE_ID

@Composable
fun HomeScreen(navController: NavController, viewModel: ScheduleViewModel) {
    val schedules = viewModel.schedulesState
    val isLoading = viewModel.isLoading.value // Observe loading state

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            PulsarIconButton(onClick = {
                navController.navigate(Screen.AddEditSchedule.route)
            })
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                schedules.isEmpty() -> {
                    Text(
                        text = "No schedules found, add schedule to launch app",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(schedules.size) { index ->
                            ScheduleListItem(
                                schedule = schedules[index],
                                onClick = {
                                    navController.navigate(
                                        Screen.AddEditSchedule.route + "?$NAV_ARG_SCHEDULE_ID=${schedules[index].id}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
