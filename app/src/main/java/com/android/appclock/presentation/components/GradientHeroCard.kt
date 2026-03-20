package com.android.appclock.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.appclock.ui.theme.ClockBlue
import com.android.appclock.ui.theme.ClockBlueDark
import com.android.appclock.ui.theme.ClockCyan

@Composable
fun GradientHeroCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 32.dp,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    verticalSpacing: Dp = 16.dp,
    gradientColors: List<Color> = listOf(ClockBlueDark, ClockBlue, ClockCyan),
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush = Brush.linearGradient(colors = gradientColors))
            .padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        content()
    }
}

@Composable
fun HeroLabelSurface(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Black.copy(alpha = 0.2f),
    contentColor: Color = Color.White
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

