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
import me.amrbashir.hijriwidget.HijriDateCalculationMethod
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.composableWithAnimatedContentScope
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferenceTemplate
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferenceGroup
import me.amrbashir.hijriwidget.preferences.composables.ui.RadioIcon

const val CALENDAR_CALCULATION_METHOD_ROUTE = "/preferences/calendar-calculation-method"

fun NavGraphBuilder.calendarCalculationRoute() {
    composableWithAnimatedContentScope(route = CALENDAR_CALCULATION_METHOD_ROUTE) {
        Route()
    }
}

fun NavController.navigateToCalendarCalculation() {
    navigate(route = CALENDAR_CALCULATION_METHOD_ROUTE)
}


@Composable
private fun Route() {
    val savedMethod = Preferences.calendarCalculationMethod.value

    PreferenceRouteLayout {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PreferenceGroup(label = "Calendar Calculation Method") {
                for (method in HijriDateCalculationMethod.entries) {

                    PreferenceTemplate(
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
}
