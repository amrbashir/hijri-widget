package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.HijriDateCalculationMethod
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.PreferencesGroup
import me.amrbashir.hijriwidget.preferences.composables.RadioIcon

@Composable
fun CalendarCalculation() {
    val savedMethod = Preferences.calendarCalculationMethod.value

    Column(
        Modifier
            .padding(horizontal = 16.dp)
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
