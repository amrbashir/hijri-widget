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
import me.amrbashir.hijriwidget.preference_activity.LocalOpenWidgetPrefs
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget
import me.amrbashir.hijriwidget.preference_activity.WidgetInstance
import me.amrbashir.hijriwidget.preference_activity.animatedContentComposable
import me.amrbashir.hijriwidget.preference_activity.composables.SharedWidgetPreview
import me.amrbashir.hijriwidget.preference_activity.screenPadding
import me.amrbashir.hijriwidget.widget.HijriWidgetReceiver

const val HOME_DESTINATION = "/"

fun NavGraphBuilder.homeDestination() {
	animatedContentComposable(route = HOME_DESTINATION) { HomeScreen() }
}

@Composable
private fun HomeScreen() {
	LocalAppBarTitle.current.value = stringResource(R.string.app_name)

	val navController = LocalNavController.current
	val context = LocalContext.current
	val appWidgetManager = remember { AppWidgetManager.getInstance(context) }

	// `null` until loaded, to avoid flashing the empty state
	var widgets by remember { mutableStateOf<List<WidgetInstance>?>(null) }

	// Reload every time this screen is shown, which also discards unsaved edits,
	// and when coming back to the app, e.g. after adding a widget
	val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
	LaunchedEffect(lifecycleState) {
		// Not waiting for `RESUMED`, which is only reached after the navigation transition ends
		if (!lifecycleState.isAtLeast(Lifecycle.State.STARTED)) return@LaunchedEffect

		widgets = WidgetInstance.loadAll(context)
	}

	val canAddWidget = appWidgetManager.isRequestPinAppWidgetSupported
	val addWidget: () -> Unit = {
		appWidgetManager.requestPinAppWidget(
			ComponentName(context, HijriWidgetReceiver::class.java),
			null,
			null,
		)
	}
	val openAbout = { navController.navigateToAbout() }

	// Fills the screen even while loading, as navigation transitions slide relative to its size
	Box(Modifier.fillMaxSize()) {
		val widgets = widgets ?: return@Box

		if (widgets.isEmpty()) {
			EmptyHomeScreen(canAddWidget, addWidget, openAbout)
		} else {
			WidgetsHomeScreen(widgets, canAddWidget, addWidget, openAbout)
		}
	}
}

@Composable
private fun WidgetsHomeScreen(
	widgets: List<WidgetInstance>,
	canAddWidget: Boolean,
	addWidget: () -> Unit,
	openAbout: () -> Unit,
) {
	val openWidgetPrefs = LocalOpenWidgetPrefs.current

	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier =
			Modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				// Applied after the scroll, so widgets shadows are not clipped
				.screenPadding(),
	) {
		for (widget in widgets) {
			key(widget.appWidgetId) {
				CompositionLocalProvider(LocalSelectedWidget provides widget) {
					SharedWidgetPreview(
						modifier = Modifier.shadow(6.dp, RoundedCornerShape(20.dp)),
						onSettingsClick = { openWidgetPrefs(widget) },
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
					icon = Icons.Outlined.Add,
					label = stringResource(R.string.home_screen_add_widget),
					onClick = addWidget,
				)
			}

			FlatButton(
				icon = Icons.Outlined.Info,
				label = stringResource(R.string.preferences_about_title),
				onClick = openAbout,
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
					icon = Icons.Outlined.Add,
					label = stringResource(R.string.home_screen_add_widget_long),
					onClick = addWidget,
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
				icon = Icons.Outlined.Info,
				label = stringResource(R.string.preferences_about_title),
				onClick = openAbout,
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
