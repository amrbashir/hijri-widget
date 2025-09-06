package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.R

@Composable
fun DayOffset() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.requiredWidth(38.dp))

        Slider(
            modifier = Modifier.weight(1F),
            value = Preferences.dayOffset.value.toFloat(),
            valueRange = -2F..2F,
            steps = 3,
            onValueChange = {
                Preferences.dayOffset.value = it.toInt()
            },
        )

        Text("${Preferences.dayOffset.value}")

        IconButton(
            onClick = {
                Preferences.dayOffset.value = Preferences.Defaults.dayOffset
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_settings_backup_restore_24),
                contentDescription = "Reset to default"
            )
        }
    }
}
