package com.android.appclock.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun AppBasicTextField(
    description: String?,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = description ?: "",
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp)),
        textStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (description.isNullOrEmpty()) {
                    Text(
                        "Click to Add Details",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )
                }
                innerTextField()
            }
        }
    )
}