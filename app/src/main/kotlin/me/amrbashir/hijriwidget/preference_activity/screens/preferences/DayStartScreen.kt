package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager

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
    val prefsManager = LocalPreferencesManager.current

    val timePickerState = rememberTimePickerState(
        initialHour = prefsManager.dayStart.value / 60,
        initialMinute = prefsManager.dayStart.value % 60,
        is24Hour = false,
    )

    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TimePicker(state = timePickerState)

            Text(AnnotatedString.fromHtml("""
                    <p>- AM: Hijri Day starts <b>after</b> Gregorian Day</p>
                    <p>- PM: Hijri Day starts <b>before</b> Gregorian Day</p>
                """.trimIndent()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                TextButton(onClick = {
                    navController.navigateUp()
                }) {
                    Text("Cancel")
                }

                Button(onClick = {
                    prefsManager.dayStart.value = timePickerState.hour * 60 + timePickerState.minute
                    navController.navigateUp()
                }) {
                    Text("Confirm")
                }
            }
        }
    }
}
