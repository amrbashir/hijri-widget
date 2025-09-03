package me.amrbashir.hijriwidget.preferences.routes.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.DayStart
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preferences.LocalAppBarTitle
import me.amrbashir.hijriwidget.preferences.LocalNavController
import me.amrbashir.hijriwidget.preferences.composableWithAnimatedContentScope
import me.amrbashir.hijriwidget.preferences.composables.DayOffset
import me.amrbashir.hijriwidget.preferences.composables.TextSize
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferencesGroup
import me.amrbashir.hijriwidget.preferences.composables.ui.TimePickerDialog
import me.amrbashir.hijriwidget.preferences.routes.navigateToAbout

const val PREFERENCES_ROUTE = "/preferences"
const val PREFERENCES_INDEX_ROUTE = "/preferences/"

fun NavGraphBuilder.preferencesIndexRoute() {
    composableWithAnimatedContentScope(route = PREFERENCES_INDEX_ROUTE) { Route() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Route() {
    LocalAppBarTitle.current.value = "Hijri Widget"

    val navController = LocalNavController.current
    var showDayStartPicker by remember { mutableStateOf(false) }

    PreferenceRouteLayout {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            PreferencesGroup(label = "Functionality") {
                PreferenceCategory(
                    label = "Date Format",
                    description = "Customize how the Hijri date appears by choosing a format pattern",
                    iconResId = R.drawable.outline_translate_24,
                    onClick = {
                        navController.navigateToDateFormat()
                    }
                )

                PreferenceCategory(
                    label = "Day Start (${Preferences.dayStart.value})",
                    description = "Set when the Hijri day begins based on your local or religious preference",
                    iconResId = R.drawable.outline_access_time_24,
                    onClick = {
                        showDayStartPicker = true
                    }
                ) {
                    if (showDayStartPicker) {
                        TimePickerDialog(
                            initialHour = Preferences.dayStart.value.hour,
                            initialMinute = Preferences.dayStart.value.minute,
                            onConfirm = { state ->
                                Preferences.dayStart.value = DayStart(state.hour, state.minute)
                                showDayStartPicker = false
                            },
                            onDismiss = { showDayStartPicker = false }
                        )
                    }
                }

                PreferenceCategory(
                    label = "Calendar Calculation Method",
                    description = "Choose the method used to calculate Hijri dates",
                    iconResId = R.drawable.outline_calendar_month_24,
                    onClick = {
                        navController.navigateToCalendarCalculation()
                    }
                )

                PreferenceCategory(
                    label = "Day Offset",
                    description = "Adjust Hijri date by ±1 day to match local moon sightings or personal observance",
                    iconResId = R.drawable.outline_more_time_24,
                ) {
                    DayOffset()
                }
            }

            PreferencesGroup(label = "Appearance") {
                PreferenceCategory(
                    label = "Color",
                    description = "Choose the widget text and background color",
                    iconResId = R.drawable.outline_color_lens_24,
                    onClick = {
                        navController.navigateToColor()
                    }
                )

                PreferenceCategory(
                    label = "Text Size",
                    description = "Change the widget text size",
                    iconResId = R.drawable.outline_text_increase_24,
                ) {
                    TextSize()
                }

                PreferenceCategory(
                    label = "Text Shadow",
                    description = "Enable or disable the widget text shadow",
                    iconResId = R.drawable.outline_brightness_6_24,
                    endContent = {
                        Switch(
                            checked = Preferences.textShadow.value,
                            onCheckedChange = {
                                Preferences.textShadow.value = it
                            }
                        )
                    },
                    onClick = {
                        Preferences.textShadow.value = !Preferences.textShadow.value
                    }
                )
            }

            PreferencesGroup(label = "Misc.") {
                PreferenceCategory(
                    label = "Restore defaults",
                    description = "Restore the default preferences",
                    iconResId = R.drawable.outline_settings_backup_restore_24,
                    onClick = {
                        Preferences.restoreDefaults()
                    }
                )

                PreferenceCategory(
                    label = "About",
                    description = "App info, version details, and changelog.",
                    iconResId = R.drawable.outline_info_24,
                    onClick = {
                        navController.navigateToAbout()
                    }
                )
            }
        }
    }
}
