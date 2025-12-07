package me.amrbashir.hijriwidget.preference_activity

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.navigation.NavHostController
import me.amrbashir.hijriwidget.PreferencesManager

val LocalNavController = compositionLocalOf<NavHostController> {
    error("CompositionLocal LocalNavController not present")
}

val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> {
    error("CompositionLocal LocalSnackBarHostState not present")
}

@OptIn(ExperimentalMaterial3Api::class)
val LocalAppBarTitle = compositionLocalOf<MutableState<String>> {
    error("CompositionLocal LocalAppBarTitle not present")
}

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    error("CompositionLocal LocalSharedTransitionScope not present")
}

val LocalAnimatedContentScope = compositionLocalOf<AnimatedContentScope> {
    error("CompositionLocal LocalAnimatedContentScope not present")
}

val LocalPreferencesManager = compositionLocalOf<PreferencesManager> {
    error("CompositionLocal LocalPreferencesManager not present")
}

data class WidgetState(
    val id: GlanceId,
    val prefs: Preferences,
)

val LocalWidgetState = compositionLocalOf<MutableState<WidgetState>> {
    error("CompositionLocal LocalWidgetState not present")
}
