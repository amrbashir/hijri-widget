package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.ColorMode
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preference_activity.components.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composableWithAnimatedContentScopeProvider
import me.amrbashir.hijriwidget.preference_activity.composables.ColorPicker
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceGroup
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceTemplate
import me.amrbashir.hijriwidget.preference_activity.composables.RadioIcon

const val COLOR_DESTINATION = "/preferences/color"

fun NavGraphBuilder.colorDestination() {
    composableWithAnimatedContentScopeProvider(route = COLOR_DESTINATION) { ColorScreen() }
}

fun NavController.navigateToColor() {
    navigate(route = COLOR_DESTINATION)
}


@Composable
internal fun ColorScreen() {
    val savedTextColorMode = Preferences.textColorMode.value
    val textColorModes = ColorMode.all()

    val savedBgColorMode = Preferences.bgColorMode.value
    val bgColorModes = ColorMode.allForBg()

    PreferenceScreenLayout {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PreferenceGroup(label = "Text Color") {
                for (mode in textColorModes) {
                    PreferenceTemplate(
                        label = mode.prettyName,
                        description = mode.description,
                        icon = { RadioIcon(selected = savedTextColorMode == mode) },
                        onClick = {
                            Preferences.textColorMode.value = mode
                        }
                    )
                }

            }

            if (Preferences.textColorMode.value == ColorMode.Custom) {
                Spacer(Modifier.requiredHeight(16.dp))

                ColorPicker(
                    Preferences.textCustomColor.value,
                    onColorChanged = {
                        Preferences.textCustomColor.value = it.toArgb()
                    }
                )
            }

            PreferenceGroup(label = "Background Color") {
                for (mode in bgColorModes) {
                    PreferenceTemplate(
                        label = mode.prettyName,
                        description = mode.description,
                        icon = { RadioIcon(selected = savedBgColorMode == mode) },
                        onClick = {
                            Preferences.bgColorMode.value = mode
                        }
                    )
                }

            }

            if (Preferences.bgColorMode.value == ColorMode.Custom) {
                Spacer(Modifier.requiredHeight(16.dp))

                ColorPicker(
                    Preferences.bgCustomColor.value,
                    onColorChanged = {
                        Preferences.bgCustomColor.value = it.toArgb()
                    }
                )
            }
        }
    }
}
