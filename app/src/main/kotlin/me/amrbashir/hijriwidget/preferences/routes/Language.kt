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
fun Language() {
    val savedLang = Preferences.language.value

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        for (lang in arrayOf(SupportedLanguage.Arabic, SupportedLanguage.English)) {
            PreferenceCategory(
                label = "$lang",
                alternateIcon = { RadioIcon(selected = savedLang == lang) },
                onClick = {
                    Preferences.language.value = lang
                }
            )
        }
    }
}