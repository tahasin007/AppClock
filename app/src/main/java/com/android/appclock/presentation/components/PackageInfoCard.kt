package com.android.appclock.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.appclock.core.utils.AppIconLoader

@Composable
fun PackageInfoCard(
    title: String,
    appName: String,
    packageName: String,
    appIconLoader: AppIconLoader,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    bodyColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (packageName.isBlank()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                AppIconImage(
                    packageName = packageName,
                    contentDescription = appName,
                    appIconLoader = appIconLoader,
                    modifier = Modifier.size(52.dp),
                    iconSize = 52.dp
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = titleColor
                )
                Text(
                    text = appName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = bodyColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (packageName.isNotBlank()) {
                    Text(
                        text = packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = bodyColor,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

