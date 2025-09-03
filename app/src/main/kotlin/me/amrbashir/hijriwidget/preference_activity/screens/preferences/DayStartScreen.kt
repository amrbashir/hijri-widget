package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import me.amrbashir.hijriwidget.DayStart
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preference_activity.LocalNavController

const val DAY_START_DESTINATION = "/preferences/day-start"

fun NavGraphBuilder.dayStartDestination() {
    dialog(route = DAY_START_DESTINATION) { DayStartScreen() }
}

fun NavController.navigateToDayStart() {
    navigate(route = DAY_START_DESTINATION)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DayStartScreen() {
    val navController = LocalNavController.current

    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val timePickerState = rememberTimePickerState(
                initialHour = Preferences.dayStart.value.hour,
                initialMinute = Preferences.dayStart.value.minute,
                is24Hour = false,
            )

            TimePicker(
                state = timePickerState,
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {
                    navController.navigateUp()
                }) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.requiredWidth(2.dp))

                Button(onClick = {
                    val newDayStart = DayStart(timePickerState.hour, timePickerState.minute)
                    Preferences.dayStart.value = newDayStart
                    navController.navigateUp()
                }) {
                    Text("Confirm")
                }
            }
        }
    }
}
