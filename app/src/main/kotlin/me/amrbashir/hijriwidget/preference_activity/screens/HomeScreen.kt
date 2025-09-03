package me.amrbashir.hijriwidget.preference_activity.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val HOME_DESTINATION = "/"

fun NavGraphBuilder.homeDestination() {
    composable(route = HOME_DESTINATION) { HomeScreen() }
}

@Composable
private fun HomeScreen() {
    NotImplementedError("This is a stub for now, in preparation for multi-widget support")
}
