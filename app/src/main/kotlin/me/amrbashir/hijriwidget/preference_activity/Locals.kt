package me.amrbashir.hijriwidget.preference_activity

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.navigation.NavHostController
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.widget.HijriWidget

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

/**
 * A placed widget and a draft of its preferences.
 *
 * Edits to [prefsManager] are only persisted to the widget when saved.
 */
data class WidgetState(
    val id: GlanceId,
    val appWidgetId: Int,
    val prefsManager: PreferencesManager,
) {
    companion object {
        suspend fun load(context: Context, glanceId: GlanceId): WidgetState {
            val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
            return WidgetState(glanceId, appWidgetId, PreferencesManager.load(context, glanceId))
        }

        suspend fun load(context: Context, appWidgetId: Int): WidgetState {
            val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            return WidgetState(glanceId, appWidgetId, PreferencesManager.load(context, glanceId))
        }

        suspend fun loadAll(context: Context): List<WidgetState> {
            return GlanceAppWidgetManager(context)
                .getGlanceIds(HijriWidget::class.java)
                .map { load(context, it) }
        }
    }
}

/** The widget whose preferences are being displayed */
val LocalWidgetState = compositionLocalOf<WidgetState> {
    error("CompositionLocal LocalWidgetState not present")
}

/** Selects the widget whose preferences are edited by the preferences screens */
val LocalSelectWidget = compositionLocalOf<(WidgetState) -> Unit> {
    error("CompositionLocal LocalSelectWidget not present")
}
