package me.amrbashir.hijriwidget.preferences.routes

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import me.amrbashir.hijriwidget.DayStart
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preferences.LocalNavController
import me.amrbashir.hijriwidget.preferences.Route
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferencesGroup
import me.amrbashir.hijriwidget.preferences.composables.ui.TimePickerDialog
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    val navController = LocalNavController.current

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PreferencesGroup {
            var showDayStartPicker by remember { mutableStateOf(false) }

            PreferenceCategory(
                label = "Day Start (${Preferences.dayStart.value})",
                description = "Set when the Hijri day begins based on your local or religious preference",
                iconResId = R.drawable.baseline_access_time_24,
                onClick = {
                    showDayStartPicker = true
                }
            )

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

            PreferenceCategory(
                label = "Calendar Calculation Method",
                description = "Choose the method used to calculate Hijri dates",
                iconResId = R.drawable.baseline_calendar_month_24,
                onClick = {
                    navController.navigate(Route.CALENDAR_CALCULATION_METHOD)
                }
            )

            PreferenceCategory(
                label = "Day Offset",
                description = "Adjust Hijri date by ±1 day to match local moon sightings or personal observance",
                iconResId = R.drawable.baseline_more_time_24,
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Spacer(modifier = Modifier.requiredWidth(38.dp))

                    Slider(
                        modifier = Modifier.weight(1F),
                        value = Preferences.dayOffset.value.toFloat(),
                        valueRange = -1F..1F,
                        steps = 1,
                        onValueChange = {
                            Preferences.dayOffset.value = it.toInt()
                        },
                    )

                    Text("${Preferences.dayOffset.value}")

                    IconButton(
                        onClick = {
                            Preferences.dayOffset.value = Preferences.Defaults.dayOffset
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_settings_backup_restore_24),
                            contentDescription = "Reset to default"
                        )
                    }
                }

            }
        }

        PreferencesGroup {
            PreferenceCategory(
                label = "Format",
                description = "Customize how the Hijri date appears by choosing a format pattern",
                iconResId = R.drawable.baseline_translate_24,
                onClick = {
                    navController.navigate(Route.FORMAT)
                }
            )

            PreferenceCategory(
                label = "Color",
                description = "Choose the widget text and background color",
                iconResId = R.drawable.baseline_color_lens_24,
                onClick = {
                    navController.navigate(Route.COLOR)
                }
            )

            PreferenceCategory(
                label = "Text Size",
                description = "Change the widget text size",
                iconResId = R.drawable.baseline_text_increase_24,
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Spacer(modifier = Modifier.requiredWidth(38.dp))

                    Slider(
                        modifier = Modifier.weight(1F),
                        value = Preferences.textSize.value,
                        valueRange = 1F..100F,
                        steps = 100,
                        onValueChange = {
                            Preferences.textSize.value = it
                        },
                    )

                    Text("${Preferences.textSize.value.roundToInt()}")

                    IconButton(
                        onClick = {
                            Preferences.textSize.value = Preferences.Defaults.textSize
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_settings_backup_restore_24),
                            contentDescription = "Reset to default"
                        )
                    }
                }

            }

            PreferenceCategory(
                label = "Shadow",
                description = "Enable or disable the widget shadow",
                iconResId = R.drawable.baseline_brightness_6_24,
                endContent = {
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
        }

        PreferencesGroup {
            PreferenceCategory(
                label = "Restore defaults",
                description = "Restore the default preferences",
                iconResId = R.drawable.baseline_settings_backup_restore_24,
                onClick = {
                    Preferences.restoreDefaults()
                }
            )

            val privacyUrl = "https://hijri-widget.amrbashir.me/PRIVACY.md"
            PreferenceCategory(
                label = "Privacy Policy",
                description = "Click to read our privacy policy",
                iconResId = R.drawable.baseline_launch_24,
                onClick = {
                    navController.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            privacyUrl.toUri()
                        )
                    )
                }
            )
        }
    }
}
