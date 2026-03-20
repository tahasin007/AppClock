package com.android.appclock.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppBasicTextField(
    description: String?,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = description ?: "",
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth(),
        minLines = 4,
        maxLines = 6,
        shape = RoundedCornerShape(20.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        placeholder = {
            Text(
                "Add context like meeting prep, study session, or routine reminder.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
        },
        label = { Text("Notes") }
    )
}