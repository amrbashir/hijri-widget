package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferencesGroup
import kotlin.math.roundToInt

@Composable
fun TextSize() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        PreferencesGroup(label = "Text Size") {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceBright)
                    .padding(16.dp)
            ) {
                Text("Value: ${Preferences.textSize.value.roundToInt()}")

                Slider(
                    value = Preferences.textSize.value,
                    valueRange = 1F..100F,
                    steps = 100,
                    onValueChange = {
                        Preferences.textSize.value = it
                    },
                )

            }

            PreferenceCategory(
                label = "Reset to default",
                onClick = {
                    Preferences.textSize.value = Preferences.Defaults.textSize
                }
            )
        }
    }
}


