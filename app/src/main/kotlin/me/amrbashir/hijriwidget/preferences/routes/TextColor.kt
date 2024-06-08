package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.SupportedTheme
import me.amrbashir.hijriwidget.preferences.LocalNavController
import me.amrbashir.hijriwidget.preferences.composables.ColorPicker
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.RadioIcon

@Composable
fun TextColor() {
    val savedTheme = Preferences.theme.value

    val supportedThemes = SupportedTheme.all()

    val context = LocalContext.current

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .imePadding()
    ) {
        for (theme in supportedThemes) {
            PreferenceCategory(
                label = theme.prettyName,
                description = theme.description,
                alternateIcon = { RadioIcon(selected = savedTheme == theme) },
                onClick = {
                    Preferences.theme.value = theme
                    Preferences.updateColor(context)
                }
            )
        }

        if (Preferences.theme.value == SupportedTheme.Custom) {
            ColorPicker(
                scrollState,
                onColorChanged = {
                    Preferences.customColor.value = it.toArgb()
                    Preferences.updateColor(context)
                }
            )
        }
    }
}