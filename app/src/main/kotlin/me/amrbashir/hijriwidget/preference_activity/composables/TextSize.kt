package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager
import kotlin.math.roundToInt

@Composable
fun TextSize() {
    val prefsManager = LocalPreferencesManager.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Slider(
            modifier = Modifier.weight(1F),
            value = prefsManager.textSize.value,
            valueRange = 1F..50F,
            steps = 50,
            onValueChange = {
                prefsManager.textSize.value = it
            },
        )

        Text("${prefsManager.textSize.value.roundToInt()}")

        IconButton(
            onClick = {
                prefsManager.textSize.reset()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_settings_backup_restore_24),
                contentDescription = "Reset to default"
            )
        }
    }
}
