package me.amrbashir.hijriwidget.preferences

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import me.amrbashir.hijriwidget.preferences.routes.aboutRoute
import me.amrbashir.hijriwidget.preferences.routes.indexRoute
import me.amrbashir.hijriwidget.preferences.routes.preferences.PREFERENCES_INDEX_ROUTE
import me.amrbashir.hijriwidget.preferences.routes.preferences.PREFERENCES_ROUTE
import me.amrbashir.hijriwidget.preferences.routes.preferences.calendarCalculationRoute
import me.amrbashir.hijriwidget.preferences.routes.preferences.colorRoute
import me.amrbashir.hijriwidget.preferences.routes.preferences.dateFormatRoute
import me.amrbashir.hijriwidget.preferences.routes.preferences.preferencesIndexRoute


fun NavGraphBuilder.composableWithAnimatedContentScope(
    route: String,
    content: @Composable () -> Unit
) {
    composable(route) {
        CompositionLocalProvider(
            LocalAnimatedContentScope provides this
        ) {
            content()
        }
    }
}


@Composable
fun Router() {
    val navController = LocalNavController.current

    NavHost(
        navController = navController,
        startDestination = PREFERENCES_ROUTE,
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
        aboutRoute()
        navigation(route = PREFERENCES_ROUTE, startDestination = PREFERENCES_INDEX_ROUTE) {
            preferencesIndexRoute()
            dateFormatRoute()
            calendarCalculationRoute()
            colorRoute()
        }
    }
}
