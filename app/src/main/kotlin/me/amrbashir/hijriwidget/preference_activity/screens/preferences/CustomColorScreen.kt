package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget
import me.amrbashir.hijriwidget.preference_activity.animatedContentComposable
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.ScrollableScreenColumn
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ColorPicker

private const val CUSTOM_TEXT_COLOR_DESTINATION = "/preferences/color/text"
private const val CUSTOM_BACKGROUND_COLOR_DESTINATION = "/preferences/color/background"

fun NavGraphBuilder.customColorDestination() {
	animatedContentComposable(route = CUSTOM_TEXT_COLOR_DESTINATION) {
		val prefsManager = LocalSelectedWidget.current.prefsManager
		CustomColorScreen(
			label = stringResource(R.string.text_color),
			color = prefsManager.textCustomColor.value,
			onColorChanged = { prefsManager.textCustomColor.value = it.toArgb() },
		)
	}
	animatedContentComposable(route = CUSTOM_BACKGROUND_COLOR_DESTINATION) {
		val prefsManager = LocalSelectedWidget.current.prefsManager
		CustomColorScreen(
			label = stringResource(R.string.background_color),
			color = prefsManager.bgCustomColor.value,
			onColorChanged = { prefsManager.bgCustomColor.value = it.toArgb() },
		)
	}
}

fun NavController.navigateToCustomTextColor() {
	navigate(route = CUSTOM_TEXT_COLOR_DESTINATION)
}

fun NavController.navigateToCustomBackgroundColor() {
	navigate(route = CUSTOM_BACKGROUND_COLOR_DESTINATION)
}

@Composable
private fun CustomColorScreen(
	label: String,
	color: Int,
	onColorChanged: (Color) -> Unit,
) {
	PreferenceScreenLayout {
		ScrollableScreenColumn(
			modifier = Modifier.fillMaxSize().imePadding(),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Text(
				label,
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.primary,
			)
			ColorPicker(
				initialColor = color,
				onColorChanged = onColorChanged,
			)
		}
	}
}
