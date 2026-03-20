package com.android.appclock.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HeroMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Black.copy(alpha = 0.2f),
    titleColor: Color = Color.White.copy(alpha = 0.9f),
    valueColor: Color = Color.White
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = titleColor
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

