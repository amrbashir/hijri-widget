package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.HijriDateCalculationMethod
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager
import me.amrbashir.hijriwidget.preference_activity.composableWithAnimatedContentScopeProvider
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceTemplate
import me.amrbashir.hijriwidget.preference_activity.composables.ui.RadioIcon

const val CALENDAR_CALCULATION_METHOD_DESTINATION = "/preferences/calendar-calculation-method"

fun NavGraphBuilder.calendarCalculationMethodDestination() {
    composableWithAnimatedContentScopeProvider(route = CALENDAR_CALCULATION_METHOD_DESTINATION) {
        CalendarCalculationMethodScreen()
    }
}

fun NavController.navigateToCalendarCalculationMethod() {
    navigate(route = CALENDAR_CALCULATION_METHOD_DESTINATION)
}


@Composable
internal fun CalendarCalculationMethodScreen() {
    val prefsManager = LocalPreferencesManager.current

    val savedMethod = prefsManager.calendarCalculationMethod.value

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
            PreferenceGroup(label = "Calendar Calculation Method") {
                for (method in HijriDateCalculationMethod.entries) {
                    PreferenceTemplate(
                        label = method.label,
                        description = method.description,
                        icon = { RadioIcon(selected = savedMethod.id == method.id) },
                        onClick = {
                            prefsManager.calendarCalculationMethod.value = method
                        }
                    )
                }
            }
        }
    }
}
