package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.hasRouteInHierarchy
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget
import me.amrbashir.hijriwidget.preference_activity.darkLightContainerColor
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.PREFERENCES_DESTINATION
import me.amrbashir.hijriwidget.widget.HijriWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
	scrollBehavior: TopAppBarScrollBehavior,
	onSave: suspend () -> Unit,
) {
	val navController = LocalNavController.current
	val currentBackStackEntry = navController.currentBackStackEntryAsState()
	val currentDestination = currentBackStackEntry.value?.destination

	val containerColor = MaterialTheme.colorScheme.darkLightContainerColor

	LargeTopAppBar(
		title = { Text(LocalAppBarTitle.current.value) },
		colors =
			TopAppBarDefaults.topAppBarColors(
				containerColor = containerColor,
				scrolledContainerColor = containerColor,
			),
		scrollBehavior = scrollBehavior,
		navigationIcon = { GoBackButton() },
		actions = {
			if (
				currentDestination?.hasRouteInHierarchy(PREFERENCES_DESTINATION) ?: false
			) {
				SaveButton(
					onSave = onSave,
				)
			}
		},
	)
}

@Composable
private fun GoBackButton() {
	val navController = LocalNavController.current

	if (navController.previousBackStackEntry != null) {
		IconButton(onClick = { navController.navigateUp() }) {
			Icon(
				imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
				contentDescription = null,
			)
		}
	}
}

@Composable
private fun SaveButton(onSave: suspend () -> Unit) {
	val context = LocalContext.current
	val widget = LocalSelectedWidget.current
	val coroutineScope = rememberCoroutineScope()

	val saveAction: () -> Unit = {
		coroutineScope.launch {
			widget.prefsManager.save(context, widget.id)
			HijriWidget().update(context, widget.id)
			AlarmReceiver.scheduleNextUpdate(context)
			onSave()
		}
	}

	IconButton(onClick = saveAction) {
		Icon(
			imageVector = Icons.Outlined.Check,
			contentDescription = null,
		)
	}
}
