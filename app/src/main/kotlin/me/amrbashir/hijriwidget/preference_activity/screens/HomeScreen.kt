package me.amrbashir.hijriwidget.preference_activity.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.amrbashir.hijriwidget.R

const val HOME_DESTINATION = "/"

fun NavGraphBuilder.homeDestination() {
    composable(route = HOME_DESTINATION) { HomeScreen() }
}

@Composable
private fun HomeScreen() {
    NotImplementedError(R.string.home_screen_stub_text.toString())
}
