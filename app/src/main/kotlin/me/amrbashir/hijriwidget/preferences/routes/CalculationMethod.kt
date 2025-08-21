package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.HijriDateCalculationMethod
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.RadioIcon

@Composable
fun CalendarCalculation() {
    val savedMethod = Preferences.calendarCalculationMethod.value

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Calendar Calculation Method",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodySmall,
        )

        for (method in HijriDateCalculationMethod.entries) {

            PreferenceCategory(
                label = method.label,
                description = method.description,
                alternateIcon = { RadioIcon(selected = savedMethod == method.id) },
                onClick = {
                    Preferences.calendarCalculationMethod.value = method.id
                }
            )
        }
    }
}
