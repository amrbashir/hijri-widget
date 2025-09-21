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
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager
import me.amrbashir.hijriwidget.preference_activity.composableWithAnimatedContentScopeProvider
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ColorPicker
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceTemplate
import me.amrbashir.hijriwidget.preference_activity.composables.ui.RadioIcon

const val COLOR_DESTINATION = "/preferences/color"

fun NavGraphBuilder.colorDestination() {
    composableWithAnimatedContentScopeProvider(route = COLOR_DESTINATION) { ColorScreen() }
}

fun NavController.navigateToColor() {
    navigate(route = COLOR_DESTINATION)
}


@Composable
internal fun ColorScreen() {
    val prefsManager = LocalPreferencesManager.current

    val savedTextColorMode = prefsManager.textColorMode.value
    val textColorModes = ColorMode.all()

    val savedBgColorMode = prefsManager.bgColorMode.value
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
                            prefsManager.textColorMode.value = mode
                        }
                    )
                }

            }

            if (prefsManager.textColorMode.value == ColorMode.Custom) {
                Spacer(Modifier.requiredHeight(16.dp))

                ColorPicker(
                    prefsManager.textCustomColor.value,
                    onColorChanged = {
                        prefsManager.textCustomColor.value = it.toArgb()
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
                            prefsManager.bgColorMode.value = mode
                        }
                    )
                }

            }

            if (prefsManager.bgColorMode.value == ColorMode.Custom) {
                Spacer(Modifier.requiredHeight(16.dp))

                ColorPicker(
                    prefsManager.bgCustomColor.value,
                    onColorChanged = {
                        prefsManager.bgCustomColor.value = it.toArgb()
                    }
                )
            }
        }
    }
}
