package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.SupportedLanguage
import me.amrbashir.hijriwidget.preferences.LocalNavController
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory

@Composable
fun Language() {
    val navController = LocalNavController.current;

    val savedLang = Preferences.language.value

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
        for (lang in arrayOf(SupportedLanguage.Arabic, SupportedLanguage.English)) {
            PreferenceCategory(
                label = "$lang",
                icon = if (savedLang == lang) Icons.Filled.Check else null,
                onClick = {
                    Preferences.language.value = lang
                    navController.navigateUp()
                }
            )
        }
    }
}