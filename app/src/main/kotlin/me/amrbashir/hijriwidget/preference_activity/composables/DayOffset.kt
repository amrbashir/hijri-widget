package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.runtime.Composable
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ValueSlider

@Composable
fun DayOffset() {
    val prefsManager = LocalPreferencesManager.current
    ValueSlider(
        value = prefsManager.dayOffset.value.toFloat(),
        onValueChange = { prefsManager.dayOffset.value = it.toInt() },
        reset = prefsManager.dayOffset::reset,
        valueRange = -2F..2F,
        steps = 3,
    )
}
