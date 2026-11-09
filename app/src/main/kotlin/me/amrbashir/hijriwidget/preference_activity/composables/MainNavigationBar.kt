package me.amrbashir.hijriwidget.preference_activity.composables

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.blur.BlurRadiusSpec
import androidx.compose.ui.graphics.blur.BlurStop
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalShowMainNavigation
import me.amrbashir.hijriwidget.preference_activity.screens.ABOUT_DESTINATION
import me.amrbashir.hijriwidget.preference_activity.screens.HOME_DESTINATION
import me.amrbashir.hijriwidget.widget.HijriWidgetReceiver

internal data class MainDestination(
	val route: String,
	@param:StringRes val label: Int,
	val icon: ImageVector,
)

internal val MAIN_DESTINATIONS =
	listOf(
		MainDestination(HOME_DESTINATION, R.string.navigation_home, Icons.Outlined.Home),
		MainDestination(ABOUT_DESTINATION, R.string.about_screen_title, Icons.Outlined.Info),
	)

/**
 * Inspired by https://x.com/pashapuma1/status/2117532568031576380
 * Pashapuma Design: https://github.com/pashapuma
 */
@Composable
internal fun MainNavigationBar(modifier: Modifier = Modifier) {
	val navController = LocalNavController.current
	val entry by navController.currentBackStackEntryAsState()
	val currentRoute = entry?.destination?.route

	Row(
		modifier =
			modifier
				.navigationBarsPadding()
				.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
	) {
		MainNavigationBackButton(
			enabled = currentRoute != HOME_DESTINATION,
			onClick = { navController.navigateUp() },
		)

		MainNavigationPill {
			MAIN_DESTINATIONS.forEach { destination ->
				MainNavigationDestinationButton(
					destination = destination,
					selected = currentRoute == destination.route,
					onClick = {
						if (currentRoute != destination.route) {
							navController.navigate(destination.route) {
								popUpTo(HOME_DESTINATION)
								launchSingleTop = true
							}
						}
					},
				)
			}
		}

		AddWidgetButton()
	}
}

@Composable
private fun RowScope.MainNavigationBackButton(
	enabled: Boolean,
	onClick: () -> Unit,
) {
	AnimatedVisibility(
		visible = enabled,
		enter = slideInHorizontally { it } + fadeIn() + expandHorizontally(expandFrom = Alignment.End),
		exit =
			slideOutHorizontally { it } + fadeOut() +
				shrinkHorizontally(
					shrinkTowards = Alignment.End,
				),
	) {
		OutlinedIconButton(
			onClick = onClick,
			enabled = enabled,
			modifier = Modifier.size(52.dp),
			shape = CircleShape,
			colors =
				IconButtonDefaults.outlinedIconButtonColors(
					containerColor = MaterialTheme.colorScheme.surfaceContainer,
					disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
				),
			border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
		) {
			Icon(
				imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
				contentDescription = stringResource(R.string.navigation_back),
			)
		}
	}
}

@Composable
private fun MainNavigationPill(content: @Composable RowScope.() -> Unit) {
	Row(
		modifier =
			Modifier
				.clip(CircleShape)
				.background(MaterialTheme.colorScheme.surfaceContainer)
				.border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
				.padding(6.dp),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		content = content,
	)
}

@Composable
private fun MainNavigationDestinationButton(
	destination: MainDestination,
	selected: Boolean,
	onClick: () -> Unit,
) {
	IconButton(
		onClick = onClick,
		modifier =
			Modifier.background(
				color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
				shape = CircleShape,
			),
	) {
		Icon(imageVector = destination.icon, contentDescription = stringResource(destination.label))
	}
}

@Composable
private fun AddWidgetButton() {
	val context = LocalContext.current
	val widgetManager = remember { AppWidgetManager.getInstance(context) }

	if (widgetManager.isRequestPinAppWidgetSupported) {
		FilledIconButton(
			onClick = {
				widgetManager.requestPinAppWidget(
					ComponentName(context, HijriWidgetReceiver::class.java),
					null,
					null,
				)
			},
			modifier = Modifier.size(60.dp),
		) {
			Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.home_screen_add_widget))
		}
	}
}

/** Keeps the last scrollable item above the floating navigation controls. */
internal fun Modifier.mainNavigationBarPadding(): Modifier =
	composed {
		if (LocalShowMainNavigation.current) padding(bottom = 112.dp) else this
	}

/** Blurs the content behind the lower part of the floating navigation. */
internal fun Modifier.mainNavigationBlur(enabled: Boolean): Modifier =
	composed {
		if (!enabled) return@composed this

		val density = LocalDensity.current
		val bottomInset = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
		val controlsHeight = 60.dp

		blur {
			// Start 25% down the navigation controls.
			val start = size.height - bottomInset - controlsHeight * 0.75F
			blurRadiusSpec =
				BlurRadiusSpec.linearGradient(
					start = DpOffset(0.dp, start),
					end = DpOffset(0.dp, size.height),
					stops = listOf(BlurStop(0F, 0.dp), BlurStop(1F, 24.dp)),
				)
		}
	}
