package com.android.appclock.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.appclock.utils.AppIconLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppIconImage(
    packageName: String,
    contentDescription: String,
    appIconLoader: AppIconLoader,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    placeholderModifier: Modifier = Modifier
) {
    val sizePx = with(LocalDensity.current) {
        iconSize.roundToPx()
    }
    val bitmap by produceState<android.graphics.Bitmap?>(
        initialValue = null,
        packageName,
        sizePx
    ) {
        value = withContext(Dispatchers.IO) {
            appIconLoader.loadIcon(packageName, sizePx)
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .then(placeholderModifier),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize(0.6f)
            )
        }
    }
}

