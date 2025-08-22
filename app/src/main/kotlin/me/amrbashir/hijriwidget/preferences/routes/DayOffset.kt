package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferencesGroup
import me.amrbashir.hijriwidget.preferences.composables.ui.RadioIcon

@Composable
fun DayOffset() {
    val savedOffset = Preferences.dayOffset.value

    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PreferencesGroup(label = "Day Offset") {


            for (offset in arrayOf(Pair(-1, "-1"), Pair(0, "0"), Pair(1, "+1"))) {
                PreferenceCategory(
                    label = offset.second,
                    icon = { RadioIcon(selected = savedOffset == offset.first) },
                    onClick = {
                        Preferences.dayOffset.value = offset.first
                    }
                )
            }
        }
    }
}
