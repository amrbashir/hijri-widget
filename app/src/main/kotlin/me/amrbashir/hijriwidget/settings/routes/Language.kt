package me.amrbashir.hijriwidget.settings.routes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import me.amrbashir.hijriwidget.Settings
import me.zhanghai.compose.preference.preference


@Composable
fun Language(navController: NavController) {
    val savedLang = Settings.language.value

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        for (lang in arrayOf("Arabic", "English")) {
            preference(
                key = lang,
                title = { Text(lang) },
                icon = {
                    if (savedLang == lang) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Language"
                        )
                    }
                },
                onClick = {
                    Settings.language.value = lang
                    navController.navigateUp()
                }
            )
        }
    }
}