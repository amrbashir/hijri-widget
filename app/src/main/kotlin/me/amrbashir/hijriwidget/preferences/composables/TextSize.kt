package me.amrbashir.hijriwidget.preferences.composables

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
import kotlin.math.roundToInt

@Composable
fun TextSize() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Spacer(modifier = Modifier.requiredWidth(38.dp))

        Slider(
            modifier = Modifier.weight(1F),
            value = Preferences.textSize.value,
            valueRange = 1F..50F,
            steps = 50,
            onValueChange = {
                Preferences.textSize.value = it
            },
        )

        Text("${Preferences.textSize.value.roundToInt()}")

        IconButton(
            onClick = {
                Preferences.textSize.value = Preferences.Defaults.textSize
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_settings_backup_restore_24),
                contentDescription = "Reset to default"
            )
        }
    }
}
