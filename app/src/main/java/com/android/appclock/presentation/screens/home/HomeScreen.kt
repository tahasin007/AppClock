package com.android.appclock.presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.appclock.presentation.components.ScheduleListItem
import com.android.appclock.presentation.navigation.Screen
import com.android.appclock.presentation.screens.ScheduleViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: ScheduleViewModel) {
    val schedules by remember { mutableStateOf(viewModel.schedules) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(schedules.size) { index ->
                ScheduleListItem(
                    schedule = schedules[index],
                    onClick = {
                        navController.navigate(Screen.AddEditSchedule.route)
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.AddEditSchedule.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(75.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Schedule")
        }
    }
}