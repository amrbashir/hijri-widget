package me.amrbashir.hijriwidget.preference_activity.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val HOME_DESTINATION = "/"

fun NavGraphBuilder.homeDestination() {
    composable(route = HOME_DESTINATION) { HomeScreen() }
}

@Composable
private fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "شاشة الإعدادات الرئيسية",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "هنا سيتم إضافة محتوى إعدادات الودجت لاحقاً.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
