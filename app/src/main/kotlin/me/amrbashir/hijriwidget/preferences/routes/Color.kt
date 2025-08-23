package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.SupportedTheme
import me.amrbashir.hijriwidget.preferences.composables.ColorPicker
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.PreferencesGroup
import me.amrbashir.hijriwidget.preferences.composables.RadioIcon

@Composable
fun Color() {
    val savedTheme = Preferences.theme.value
    val supportedThemes = SupportedTheme.all()

    val savedBgTheme = Preferences.bgTheme.value
    val supportedBgThemes = SupportedTheme.allForBg()

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        PreferencesGroup(label = "Text Color") {
            for (theme in supportedThemes) {
                PreferenceCategory(
                    label = theme.prettyName,
                    description = theme.description,
                    icon = { RadioIcon(selected = savedTheme == theme) },
                    onClick = {
                        Preferences.theme.value = theme
                    }
                )
            }

            if (Preferences.theme.value == SupportedTheme.Custom) {
                ColorPicker(
                    Preferences.customColor.value,
                    scrollState,
                    onColorChanged = {
                        Preferences.customColor.value = it.toArgb()
                    }
                )
            }
        }

        PreferencesGroup(label = "Background Color") {
            for (bgTheme in supportedBgThemes) {
                PreferenceCategory(
                    label = bgTheme.prettyName,
                    description = bgTheme.description,
                    icon = { RadioIcon(selected = savedBgTheme == bgTheme) },
                    onClick = {
                        Preferences.bgTheme.value = bgTheme
                    }
                )
            }

            if (Preferences.bgTheme.value == SupportedTheme.Custom) {
                ColorPicker(
                    Preferences.bgCustomColor.value,
                    scrollState,
                    onColorChanged = {
                        Preferences.bgCustomColor.value = it.toArgb()
                    }
                )
            }
        }
    }
}
