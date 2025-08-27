package me.amrbashir.hijriwidget.preferences.routes.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import me.amrbashir.hijriwidget.HijriDateCalculationMethod
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferencesGroup
import me.amrbashir.hijriwidget.preferences.composables.ui.RadioIcon

@Serializable
object CalendarCalculationRoute

fun NavGraphBuilder.calendarCalculationRoute() {
    composable<CalendarCalculationRoute> { Route() }
}

fun NavController.navigateToCalendarCalculation() {
    navigate(route = CalendarCalculationRoute)
}


@Composable
private fun Route() {
    val savedMethod = Preferences.calendarCalculationMethod.value

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PreferencesGroup(label = "Calendar Calculation Method") {
            for (method in HijriDateCalculationMethod.entries) {

                PreferenceCategory(
                    label = method.label,
                    description = method.description,
                    icon = { RadioIcon(selected = savedMethod == method.id) },
                    onClick = {
                        Preferences.calendarCalculationMethod.value = method.id
                    }
                )
            }
        }
    }
}
