package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val INDEX_ROUTE = "/"

fun NavGraphBuilder.indexRoute() {
    composable(route = INDEX_ROUTE) { Route() }
}

@Composable
private fun Route() {
    NotImplementedError("This is a stub for now, in perparation for multi-widget support")
}
