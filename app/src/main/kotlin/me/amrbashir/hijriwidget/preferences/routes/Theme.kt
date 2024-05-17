package me.amrbashir.hijriwidget.preferences.routes

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.LocalNavController
import me.amrbashir.hijriwidget.SupportedTheme
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory

@Composable
fun Theme() {
    val navController = LocalNavController.current;

    val savedTheme = Preferences.theme.value

    val supportedThemes =
        mutableListOf(SupportedTheme.System, SupportedTheme.Dark, SupportedTheme.Light);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        supportedThemes.add(0, SupportedTheme.Dynamic)
    }

    val context = LocalContext.current;

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        for (theme in supportedThemes) {
            PreferenceCategory(
                label = "$theme",
                icon = if (savedTheme == theme) Icons.Filled.Check else null,
                onClick = {
                    Preferences.theme.value = theme
                    Preferences.updateColor(context)
                    navController.navigateUp()
                }
            )
        }
    }
}