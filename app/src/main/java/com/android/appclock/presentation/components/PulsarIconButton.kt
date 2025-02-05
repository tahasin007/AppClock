package com.android.appclock.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun PulsarIconButton(onClick: () -> Unit) {
    MultiplePulsarEffect { modifier ->
        IconButton(
            modifier = modifier,
            onClick = { onClick() }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Note",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MultiplePulsarEffect(
    nbPulsar: Int = 2,
    pulsarRadius: Float = 25f,
    pulsarColor: Color = MaterialTheme.colorScheme.primary,
    iconButton: @Composable (Modifier) -> Unit = {}
) {
    var iconSize by remember { mutableStateOf(IntSize(0, 0)) }

    val effects: List<Pair<Float, Float>> = List(nbPulsar) {
        pulsarBuilder(pulsarRadius = pulsarRadius, size = iconSize.width, delay = it * 500)
    }

    Box(
        Modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier, onDraw = {
            for (i in 0 until nbPulsar) {
                val (radius, alpha) = effects[i]
                drawCircle(
                    color = pulsarColor.copy(alpha = alpha),
                    radius = radius,
                    style = Stroke(width = 4f)
                )
            }
        })
        iconButton(
            Modifier
                .padding((pulsarRadius * 2).dp)
                .onGloballyPositioned {
                    if (it.isAttached) {
                        iconSize = it.size
                    }
                }
        )
    }
}