package me.amrbashir.hijriwidget.settings.routes

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import me.amrbashir.hijriwidget.Settings
import me.amrbashir.hijriwidget.settings.SupportedTheme
import me.amrbashir.hijriwidget.settings.composables.settingsItem


@Composable
fun Theme(navController: NavController) {
    val savedTheme = Settings.theme.value

    val supportedThemes =
        mutableListOf(SupportedTheme.System, SupportedTheme.Dark, SupportedTheme.Light);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        supportedThemes.add(0, SupportedTheme.Dynamic)
    }

    val context = LocalContext.current;

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        for (theme in supportedThemes) {
            settingsItem(
                title = { Text("$theme") },
                icon = {
                    if (savedTheme == theme) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Theme"
                        )
                    }
                },
                onClick = {
                    Settings.theme.value = theme
                    Settings.updateColor(context)
                    navController.navigateUp()
                }
            )
        }
    }
}