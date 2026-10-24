package me.amrbashir.hijriwidget.preference_activity

import android.content.Context
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.navigation.NavHostController
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.widget.HijriWidget

val LocalNavController =
	compositionLocalOf<NavHostController> {
		error("CompositionLocal LocalNavController not present")
	}

val LocalSnackBarHostState =
	compositionLocalOf<SnackbarHostState> {
		error("CompositionLocal LocalSnackBarHostState not present")
	}

@OptIn(ExperimentalMaterial3Api::class)
val LocalAppBarTitle =
	compositionLocalOf<MutableState<String>> {
		error("CompositionLocal LocalAppBarTitle not present")
	}

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope =
	compositionLocalOf<SharedTransitionScope> {
		error("CompositionLocal LocalSharedTransitionScope not present")
	}

val LocalAnimatedContentScope =
	compositionLocalOf<AnimatedContentScope> {
		error("CompositionLocal LocalAnimatedContentScope not present")
	}

/**
 * A widget instance on the home screen and a draft of its preferences.
 *
 * Edits to [prefsManager] are only persisted to the widget when saved.
 */
data class WidgetInstance(
	val id: GlanceId,
	val appWidgetId: Int,
	val prefsManager: PreferencesManager,
) {
	companion object {
		suspend fun load(
			context: Context,
			appWidgetId: Int,
		): WidgetInstance {
			val glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
			return WidgetInstance(glanceId, appWidgetId, PreferencesManager.load(context, glanceId))
		}

		suspend fun load(
			context: Context,
			glanceId: GlanceId,
		): WidgetInstance {
			val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
			return WidgetInstance(glanceId, appWidgetId, PreferencesManager.load(context, glanceId))
		}

		suspend fun loadAll(context: Context): List<WidgetInstance> =
			HijriWidget.glanceIds(context).map { load(context, it) }
	}
}

/**
 * The widget whose preferences are being displayed.
 *
 * Can be provided without a widget (e.g. on the home screen before one is selected),
 * but reading it then fails.
 */
object LocalSelectedWidget {
	private val local = compositionLocalOf<WidgetInstance?> { null }

	val current: WidgetInstance
		@Composable get() = checkNotNull(local.current) { "No widget selected" }

	infix fun provides(widget: WidgetInstance?) = local provides widget
}

/** Selects a widget and opens its preferences */
val LocalOpenWidgetPrefs =
	compositionLocalOf<(WidgetInstance) -> Unit> {
		error("CompositionLocal LocalOpenWidgetPrefs not present")
	}
