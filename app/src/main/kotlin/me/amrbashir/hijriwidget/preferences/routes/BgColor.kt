package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.SupportedTheme
import me.amrbashir.hijriwidget.preferences.composables.ColorPicker
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.RadioIcon

@Composable
fun BgColor() {
    val savedTheme = Preferences.bgTheme.value

    val supportedThemes = SupportedTheme.allForBg()

    val context = LocalContext.current

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            "Background Color",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodySmall,
        )

        for (theme in supportedThemes) {
            PreferenceCategory(
                label = theme.prettyName,
                description = theme.description,
                alternateIcon = { RadioIcon(selected = savedTheme == theme) },
                onClick = {
                    Preferences.bgTheme.value = theme
                    Preferences.updateBgColor(context)
                }
            )
        }

        if (Preferences.bgTheme.value == SupportedTheme.Custom) {
            ColorPicker(
                Preferences.bgCustomColor.value,
                scrollState,
                onColorChanged = {
                    Preferences.bgCustomColor.value = it.toArgb()
                    Preferences.updateBgColor(context)
                }
            )
        }
    }
}
