package me.amrbashir.hijriwidget.preference_activity.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.composables.AppInfo
import me.amrbashir.hijriwidget.preference_activity.composables.Changelog
import me.amrbashir.hijriwidget.preference_activity.composables.Contributors
import me.amrbashir.hijriwidget.preference_activity.composables.ScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.ScrollableScreenColumn
import me.amrbashir.hijriwidget.preference_activity.composables.ui.Item
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ItemGroup

const val SETTINGS_DESTINATION = "/Settings"

fun NavGraphBuilder.settingsDestination() {
	composable(route = SETTINGS_DESTINATION) { SettingsScreen() }
}

@Composable
private fun SettingsScreen() {
	LocalAppBarTitle.current.value = stringResource(R.string.settings_title)
	val navController = LocalNavController.current
	ScreenLayout {
		ScrollableScreenColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
			ItemGroup {
				Item(
					label = stringResource(R.string.logs),
					icon = Icons.Outlined.Description,
					description = stringResource(R.string.logs_description),
					onClick = { navController.navigate(LOGS_DESTINATION) },
				)
			}

			AppInfo()
			Contributors()
			Changelog()
		}
	}
}
