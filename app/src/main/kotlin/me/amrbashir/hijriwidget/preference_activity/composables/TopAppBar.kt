package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.amrbashir.hijriwidget.R
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
fun TopAppBar(scrollBehavior: TopAppBarScrollBehavior) {
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
		actions = {
			if (
				currentDestination?.hasRouteInHierarchy(PREFERENCES_DESTINATION) ?: false
			) {
				SaveButton()
			}
		},
	)
}

/** Saves the selected widget's preferences and updates it, then calls [onSave] */
@Composable
fun rememberSaveAction(onSave: suspend () -> Unit): () -> Unit {
	val context = LocalContext.current
	val widget = LocalSelectedWidget.current
	val coroutineScope = rememberCoroutineScope()

	return {
		coroutineScope.launch {
			// Leaving the screen right after saving must not cancel it midway
			withContext(NonCancellable) {
				widget.prefsManager.save(context, widget.id)
				HijriWidget().update(context, widget.id)
				AlarmReceiver.scheduleNextUpdate(context)
			}
			onSave()
		}
	}
}

@Composable
private fun SaveButton() {
	var saved by remember { mutableStateOf(false) }
	val coroutineScope = rememberCoroutineScope()
	var resetJob by remember { mutableStateOf<Job?>(null) }
	val saveLabel = stringResource(R.string.save)
	val saveAction =
		rememberSaveAction {
			resetJob?.cancel()
			saved = true
			resetJob =
				coroutineScope.launch {
					delay(3000)
					saved = false
				}
		}

	TextButton(
		onClick = saveAction,
		modifier = Modifier.semantics { contentDescription = saveLabel },
		colors =
			ButtonDefaults.textButtonColors(
				containerColor = if (saved) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
				contentColor =
					if (saved) {
						MaterialTheme.colorScheme.onSecondaryContainer
					} else {
						MaterialTheme.colorScheme.primary
					},
			),
	) {
		Row(modifier = Modifier.animateContentSize(), verticalAlignment = Alignment.CenterVertically) {
			Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
			AnimatedVisibility(visible = saved) {
				Row {
					Spacer(modifier = Modifier.width(6.dp))
					Text(stringResource(R.string.saved))
				}
			}
		}
	}
}
