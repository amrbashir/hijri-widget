package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.SupportedLanguage
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.RadioIcon

@Composable
fun DayOffset() {
    val savedOffset = Preferences.dayOffset.value

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        for (offset in arrayOf(Pair(-1,"-1"), Pair(0, "0"), Pair(1, "+1"))) {
            PreferenceCategory(
                label = offset.second,
                alternateIcon = { RadioIcon(selected = savedOffset == offset.first) },
                onClick = {
                    Preferences.dayOffset.value = offset.first
                }
            )
        }
    }
}
