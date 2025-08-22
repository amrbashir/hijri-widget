package me.amrbashir.hijriwidget.preferences.composables.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun PreferencesGroup(
    label: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    label?.let {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }

    Column(
        modifier = Modifier.clip(shape = RoundedCornerShape(20.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        content()
    }
}
