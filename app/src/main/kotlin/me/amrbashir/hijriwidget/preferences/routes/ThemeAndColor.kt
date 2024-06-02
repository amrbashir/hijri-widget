package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.LocalNavController
import me.amrbashir.hijriwidget.SupportedTheme
import me.amrbashir.hijriwidget.preferences.composables.ColorPickerDialog
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory

@Composable
fun ThemeAndColor() {
    val navController = LocalNavController.current

    val savedTheme = Preferences.theme.value

    val supportedThemes = SupportedTheme.all()

    val context = LocalContext.current

    var colorPickerOpen by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
        for (theme in supportedThemes) {
            PreferenceCategory(
                label = theme.prettyName,
                description = theme.description,
                icon = if (savedTheme == theme) Icons.Filled.Check else null,
                onClick = {
                    if (theme == SupportedTheme.Custom) {
                        colorPickerOpen = true
                    } else {
                        Preferences.theme.value = theme
                        Preferences.updateColor(context)
                        navController.navigateUp()
                    }
                }
            )
        }

        if (colorPickerOpen) {
            ColorPickerDialog(
                onDismissRequest = {
                    colorPickerOpen = false
                },
                onConfirm = {
                    colorPickerOpen = false
                    Preferences.theme.value = SupportedTheme.Custom
                    Preferences.customColor.value = it.toArgb()
                    Preferences.updateColor(context)
                    navController.navigateUp()
                }
            )
        }
    }
}