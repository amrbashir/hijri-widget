package me.amrbashir.hijriwidget.preferences

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import me.amrbashir.hijriwidget.preferences.routes.aboutRoute
import me.amrbashir.hijriwidget.preferences.routes.indexRoute
import me.amrbashir.hijriwidget.preferences.routes.preferences.PreferencesIndexRoute
import me.amrbashir.hijriwidget.preferences.routes.preferences.PreferencesRoute
import me.amrbashir.hijriwidget.preferences.routes.preferences.calendarCalculationRoute
import me.amrbashir.hijriwidget.preferences.routes.preferences.colorRoute
import me.amrbashir.hijriwidget.preferences.routes.preferences.dateFormatRoute
import me.amrbashir.hijriwidget.preferences.routes.preferences.preferencesIndexRoute


@Composable
fun Router() {
    val navController = LocalNavController.current

    NavHost(
        navController = navController,
        startDestination = PreferencesRoute,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        }
    ) {
        indexRoute()
        preferencesIndexRoute()
        aboutRoute()
        navigation<PreferencesRoute>(startDestination = PreferencesIndexRoute) {
            preferencesIndexRoute()
            dateFormatRoute()
            calendarCalculationRoute()
            colorRoute()
        }
    }
}
