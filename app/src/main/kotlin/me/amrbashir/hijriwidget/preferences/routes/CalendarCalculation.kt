package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.amrbashir.hijriwidget.CalendarCalculationMethod
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.RadioIcon

@Composable
fun CalendarCalculation() {
    val savedCalcMethod = Preferences.calendarCalculationMethod.value

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        for (calcMethod in CalendarCalculationMethod.entries) {
            val methodStr = calcMethod.toString()

            PreferenceCategory(
                label = calcMethod.label(),
                description = calcMethod.desc(),
                alternateIcon = { RadioIcon(selected = savedCalcMethod == methodStr) },
                onClick = {
                    Preferences.calendarCalculationMethod.value = methodStr
                }
            )
        }
    }
}
