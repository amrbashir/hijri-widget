package me.amrbashir.hijriwidget.preference_activity.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalSelectWidget
import me.amrbashir.hijriwidget.preference_activity.LocalWidgetState
import me.amrbashir.hijriwidget.preference_activity.WidgetState
import me.amrbashir.hijriwidget.preference_activity.animatedContentComposable
import me.amrbashir.hijriwidget.preference_activity.composables.SharedWidgetPreview
import me.amrbashir.hijriwidget.preference_activity.screenPadding
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.navigateToPreferences
import me.amrbashir.hijriwidget.widget.HijriWidgetReceiver

const val HOME_DESTINATION = "/"

fun NavGraphBuilder.homeDestination() {
	animatedContentComposable(route = HOME_DESTINATION) { HomeScreen() }
}

@Composable
private fun HomeScreen() {
	LocalAppBarTitle.current.value = stringResource(R.string.app_name)

	val navController = LocalNavController.current
	val selectWidget = LocalSelectWidget.current
	val context = LocalContext.current
	val appWidgetManager = remember { AppWidgetManager.getInstance(context) }

	val widgetsStates = remember { mutableStateListOf<WidgetState>() }
	var loaded by remember { mutableStateOf(false) }

	// Reload every time this screen is shown, which also discards unsaved edits,
	// and when coming back to the app, e.g. after adding a widget
	val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
	LaunchedEffect(lifecycleState) {
		// Not waiting for `RESUMED`, which is only reached after the navigation transition ends
		if (!lifecycleState.isAtLeast(Lifecycle.State.STARTED)) return@LaunchedEffect
		val states = WidgetState.loadAll(context)
		widgetsStates.clear()
		widgetsStates.addAll(states)
		loaded = true
	}

	// Avoid flashing the empty state before widgets are loaded, but still fill
	// the screen, as navigation transitions slide relative to its size
	if (!loaded) {
		Box(Modifier.fillMaxSize())
		return
	}

	val canAddWidget = appWidgetManager.isRequestPinAppWidgetSupported
	val addWidget = {
		appWidgetManager.requestPinAppWidget(
			ComponentName(context, HijriWidgetReceiver::class.java),
			null,
			null,
		)
		Unit
	}
	val openAbout = { navController.navigateToAbout() }

	if (widgetsStates.isEmpty()) {
		EmptyHomeScreen(canAddWidget, addWidget, openAbout)
		return
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier =
			Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				// Applied after the scroll, so widgets shadows are not clipped
				.screenPadding(),
	) {
		for (state in widgetsStates) {
			key(state.appWidgetId) {
				CompositionLocalProvider(LocalWidgetState provides state) {
					SharedWidgetPreview(
						modifier = Modifier.shadow(6.dp, RoundedCornerShape(20.dp)),
						onSettingsClick = {
							selectWidget(state)
							navController.navigateToPreferences()
						},
					)
				}
			}
		}

		Row(
			horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
			modifier = Modifier.fillMaxWidth(),
		) {
			if (canAddWidget) {
				FlatButton(
					Icons.Outlined.Add,
					stringResource(R.string.home_screen_add_widget),
					addWidget,
				)
			}
			FlatButton(
				Icons.Outlined.Info,
				stringResource(R.string.preferences_about_title),
				openAbout,
			)
		}
	}
}

@Composable
private fun EmptyHomeScreen(
	canAddWidget: Boolean,
	addWidget: () -> Unit,
	openAbout: () -> Unit,
) {
	Box(
		modifier =
			Modifier
				.fillMaxSize()
				.screenPadding(),
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			modifier = Modifier.align(Alignment.Center),
		) {
			Icon(
				Icons.Outlined.Widgets,
				contentDescription = null,
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.size(48.dp),
			)
			Spacer(Modifier.height(16.dp))
			Text(
				stringResource(R.string.home_screen_no_widgets_title),
				style = MaterialTheme.typography.titleLarge,
			)
			Spacer(Modifier.height(8.dp))
			if (canAddWidget) {
				FlatButton(
					Icons.Outlined.Add,
					stringResource(R.string.home_screen_add_widget_long),
					addWidget,
				)
			} else {
				Text(
					stringResource(R.string.home_screen_no_widgets),
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					textAlign = TextAlign.Center,
					modifier = Modifier.padding(horizontal = 16.dp),
				)
			}
		}

		Box(Modifier.align(Alignment.BottomCenter)) {
			FlatButton(
				Icons.Outlined.Info,
				stringResource(R.string.preferences_about_title),
				openAbout,
			)
		}
	}
}

@Composable
private fun FlatButton(
	icon: ImageVector,
	label: String,
	onClick: () -> Unit,
) {
	TextButton(onClick = onClick) {
		Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
		Spacer(Modifier.width(8.dp))
		Text(label)
	}
}
