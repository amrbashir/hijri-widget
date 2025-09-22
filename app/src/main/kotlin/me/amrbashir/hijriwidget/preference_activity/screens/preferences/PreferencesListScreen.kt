package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.formatTime
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager
import me.amrbashir.hijriwidget.preference_activity.composableWithAnimatedContentScopeProvider
import me.amrbashir.hijriwidget.preference_activity.composables.DayOffset
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.TextSize
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceTemplate
import me.amrbashir.hijriwidget.preference_activity.screens.navigateToAbout

const val PREFERENCES_DESTINATION = "/preferences"
const val PREFERENCES_LIST_DESTINATION = "/preferences/"

fun NavGraphBuilder.preferenceListDestination() {
    composableWithAnimatedContentScopeProvider(route = PREFERENCES_LIST_DESTINATION) { PreferenceListScreen() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PreferenceListScreen() {
    LocalAppBarTitle.current.value = "Hijri Widget"

    val prefsManager = LocalPreferencesManager.current
    val navController = LocalNavController.current

    PreferenceScreenLayout {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            PreferenceGroup(label = "Functionality") {
                PreferenceTemplate(
                    label = "Date Format",
                    description = "Customize how the Hijri date appears by choosing a format pattern",
                    iconResId = R.drawable.outline_translate_24,
                    onClick = {
                        navController.navigateToDateFormat()
                    }
                )

                PreferenceTemplate(
                    label = "Day Start (${prefsManager.dayStart.value.formatTime()})",
                    description = "Set when the Hijri day begins based on your local or religious preference",
                    iconResId = R.drawable.outline_access_time_24,
                    onClick = {
                        navController.navigateToDayStart()
                    }
                )

                PreferenceTemplate(
                    label = "Calendar Calculation Method",
                    description = "Choose the method used to calculate Hijri dates",
                    iconResId = R.drawable.outline_calendar_month_24,
                    onClick = {
                        navController.navigateToCalendarCalculationMethod()
                    }
                )

                PreferenceTemplate(
                    label = "Day Offset",
                    description = "Adjust Hijri date by ±1 day to match local moon sightings or personal observance",
                    iconResId = R.drawable.outline_more_time_24,
                ) {
                    DayOffset()
                }
            }

            PreferenceGroup(label = "Appearance") {
                PreferenceTemplate(
                    label = "Color",
                    description = "Choose the widget text and background color",
                    iconResId = R.drawable.outline_color_lens_24,
                    onClick = {
                        navController.navigateToColor()
                    }
                )

                PreferenceTemplate(
                    label = "Text Size",
                    description = "Change the widget text size",
                    iconResId = R.drawable.outline_text_increase_24,
                ) {
                    TextSize()
                }

                PreferenceTemplate(
                    label = "Text Shadow",
                    description = "Enable or disable the widget text shadow",
                    iconResId = R.drawable.outline_brightness_6_24,
                    endContent = {
                        Switch(
                            checked = prefsManager.textShadow.value,
                            onCheckedChange = {
                                prefsManager.textShadow.value = it
                            }
                        )
                    },
                    onClick = {
                        prefsManager.textShadow.value = !prefsManager.textShadow.value
                    }
                )
            }

            PreferenceGroup(label = "Misc.") {
                PreferenceTemplate(
                    label = "Restore defaults",
                    description = "Restore the default preferences",
                    iconResId = R.drawable.outline_settings_backup_restore_24,
                    onClick = {
                        prefsManager.reset()
                    }
                )

                PreferenceTemplate(
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
