package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.runtime.Composable
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ValueSlider

@Composable
fun TextSize() {
    val prefsManager = LocalPreferencesManager.current
    ValueSlider(
        value = prefsManager.textSize.value,
        onValueChange = { prefsManager.textSize.value = it },
        reset = prefsManager.textSize::reset,
        valueRange = 1F..50F,
        steps = 50,
    )
}
