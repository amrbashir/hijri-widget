package me.amrbashir.hijriwidget.preferences

import android.content.SharedPreferences
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
import me.amrbashir.hijriwidget.Preference
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.composables.PreferenceCategory


val Preferences.dayOffset: Preference<Int>
    get() = Preference(
        key = "DAY_OFFSET",
        default = 0,
        loader = SharedPreferences::getInt,
        saver = SharedPreferences.Editor::putInt,
    )


@Composable
private fun Content() {
    PreferenceCategory(
        label = "Day Offset",
        description = "Adjust Hijri date by ±1 day to match local moon sightings or personal observance",
        iconResId = R.drawable.baseline_more_time_24,
    ) {
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
                    Preferences.dayOffset.restoreDefault()
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_settings_backup_restore_24),
                    contentDescription = "Reset to default"
                )
            }
        }
    }
}
