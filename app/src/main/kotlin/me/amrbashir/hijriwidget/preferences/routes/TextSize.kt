package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory
import kotlin.math.roundToInt

@Composable
fun TextSize() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PreferenceCategory(
            label = "Custom text size",
            rightContent = {
                Switch(
                    checked = Preferences.isCustomTextSize.value,
                    onCheckedChange = {
                        Preferences.isCustomTextSize.value = it
                    }
                )
            },
            onClick = {
                Preferences.isCustomTextSize.value = !Preferences.isCustomTextSize.value
            }
        )

        Slider(
            enabled = Preferences.isCustomTextSize.value,
            value = Preferences.customTextSize.value,
            valueRange = 1F..100F,
            steps = 100,
            onValueChange = {
                Preferences.customTextSize.value = it
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(Preferences.customTextSize.value.roundToInt().toString())

        Spacer(modifier = Modifier.requiredHeight(16.dp))

        TextButton(
            onClick = {
                Preferences.customTextSize.value = 22F
            }
        ) {
            Text("Reset to default")
        }
    }
}


