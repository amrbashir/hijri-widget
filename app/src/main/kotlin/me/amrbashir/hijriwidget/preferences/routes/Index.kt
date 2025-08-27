package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object IndexRoute

fun NavGraphBuilder.indexRoute() {
    composable<IndexRoute> { Route() }
}

@Composable
private fun Route() {
    NotImplementedError("This is a stub for now, in perparation for multi-widget support")
}
