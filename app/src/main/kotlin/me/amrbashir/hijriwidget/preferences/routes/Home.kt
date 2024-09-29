package me.amrbashir.hijriwidget.preferences.routes

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preferences.LocalNavController
import me.amrbashir.hijriwidget.preferences.Route
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory

@Composable
fun Home() {
    val navController = LocalNavController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = WindowInsets.safeContent
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            PreferenceCategory(
                label = "Language",
                description = "Choose the widget language",
                icon = ImageVector.vectorResource(R.drawable.baseline_translate_24),
                onClick = {
                    navController.navigate(Route.LANGUAGE)
                }
            )

            PreferenceCategory(
                label = "Text Color",
                description = "Choose the widget text color",
                icon = ImageVector.vectorResource(R.drawable.baseline_color_lens_24),
                onClick = {
                    navController.navigate(Route.TEXT_COLOR)
                }
            )

            PreferenceCategory(
                label = "Text Size",
                description = "Change the widget text size",
                icon = ImageVector.vectorResource(R.drawable.baseline_text_increase_24),
                onClick = {
                    navController.navigate(Route.TEXT_SIZE)
                }

            )

            PreferenceCategory(
                label = "Shadow",
                description = "Enable or disable the widget shadow",
                icon = ImageVector.vectorResource(R.drawable.baseline_brightness_6_24),
                rightContent = {
                    Switch(
                        checked = Preferences.shadow.value,
                        onCheckedChange = {
                            Preferences.shadow.value = it
                        }
                    )
                },
                onClick = {
                    Preferences.shadow.value = !Preferences.shadow.value
                }
            )

            PreferenceCategory(
                label = "Restore defaults",
                description = "Restore the default preferences",
                icon = ImageVector.vectorResource(R.drawable.baseline_settings_backup_restore_24),
                onClick = {
                    Preferences.restoreDefaults(navController.context)
                }
            )

            val privacyUrl = "https://hijri-widget.amrbashir.me/PRIVACY.md"
            PreferenceCategory(
                label = "Privacy Policy",
                description = "Click to read our privacy policy",
                icon = ImageVector.vectorResource(R.drawable.baseline_launch_24),
                onClick = {
                  navController.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)))
                }
            )
        }
    }
}