package me.amrbashir.hijriwidget.preference_activity.composables.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.R
import kotlin.math.roundToInt

@Composable
fun ValueSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    default: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Slider(
            modifier = Modifier.weight(1F),
            value = value,
            valueRange = valueRange,
            steps = steps,
            onValueChange = onValueChange,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(value.roundToInt().toString())

        val isDefault = value == default
        AnimatedVisibility(!isDefault) {
            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { onValueChange(default) }) {
                Icon(
                    imageVector = Icons.Outlined.SettingsBackupRestore,
                    contentDescription = stringResource(R.string.reset_to_default)
                )
            }
        }
    }
}
