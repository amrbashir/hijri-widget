package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.Preferences
import kotlin.math.roundToInt

@Composable
fun TextSize() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Text Size",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodySmall,
        )

        Slider(
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


