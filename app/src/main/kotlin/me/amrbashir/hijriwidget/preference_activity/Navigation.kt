package me.amrbashir.hijriwidget.preference_activity

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import me.amrbashir.hijriwidget.preference_activity.screens.HOME_DESTINATION
import me.amrbashir.hijriwidget.preference_activity.screens.aboutDestination
import me.amrbashir.hijriwidget.preference_activity.screens.homeDestination
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.PREFERENCES_DESTINATION
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.PREFERENCES_LIST_DESTINATION
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.calendarCalculationMethodDestination
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.colorDestination
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.dateFormatDestination
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.dayStartDestination
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.preferenceListDestination


@Composable
fun Navigation() {
    val navController = LocalNavController.current

    NavHost(
        navController = navController,
        startDestination = HOME_DESTINATION,
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
        homeDestination()
        aboutDestination()
        navigation(
            route = PREFERENCES_DESTINATION,
            startDestination = PREFERENCES_LIST_DESTINATION,
        ) {
            preferenceListDestination()
            dateFormatDestination()
            dayStartDestination()
            calendarCalculationMethodDestination()
            colorDestination()
        }
    }
}


fun NavGraphBuilder.animatedContentComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(route, arguments) {
        CompositionLocalProvider(
            LocalAnimatedContentScope provides this
        ) {
            content(it)
        }
    }
}
