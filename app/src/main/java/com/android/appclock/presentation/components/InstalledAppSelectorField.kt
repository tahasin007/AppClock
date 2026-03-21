package com.android.appclock.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.android.appclock.core.utils.AppIconLoader
import com.android.appclock.presentation.common.InstalledAppUI

@Composable
fun InstalledAppSelectorField(
    selectedApp: InstalledAppUI?,
    installedApps: List<InstalledAppUI>,
    expanded: Boolean,
    appIconLoader: AppIconLoader,
    onExpandRequest: () -> Unit,
    onDismissRequest: () -> Unit,
    onAppSelected: (InstalledAppUI) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Selected app",
    emptyValue: String = "Choose an installed app",
    emptySupportingText: String = "Tap to browse installed apps",
    emptyLeadingIcon: ImageVector = Icons.Default.Apps,
    searchLabel: String = "Search installed apps",
    loadingText: String = "Loading installed apps..."
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(searchQuery, installedApps) {
        if (searchQuery.isBlank()) {
            installedApps
        } else {
            installedApps.filter { app ->
                app.appName.contains(searchQuery, ignoreCase = true) ||
                        app.packageName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    fun dismissDropdown() {
        searchQuery = ""
        onDismissRequest()
    }

    Box(modifier = modifier.fillMaxWidth()) {
        SelectableCard(
            title = title,
            value = selectedApp?.appName ?: emptyValue,
            supportingText = selectedApp?.packageName ?: emptySupportingText,
            packageName = selectedApp?.packageName,
            appIconLoader = appIconLoader,
            leadingIcon = if (selectedApp == null) emptyLeadingIcon else null,
            onClick = onExpandRequest
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { dismissDropdown() },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(420.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                singleLine = true,
                label = { Text(searchLabel) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (installedApps.isEmpty()) {
                Text(
                    text = loadingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            } else if (filteredApps.isEmpty()) {
                Text(
                    text = "No apps match \"$searchQuery\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .height(340.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    filteredApps.forEach { app ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(text = app.appName)
                                    Text(
                                        text = app.packageName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            leadingIcon = {
                                AppIconImage(
                                    packageName = app.packageName,
                                    contentDescription = app.appName,
                                    appIconLoader = appIconLoader,
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            onClick = {
                                searchQuery = ""
                                onAppSelected(app)
                            }
                        )
                    }
                }
            }
        }
    }
}

