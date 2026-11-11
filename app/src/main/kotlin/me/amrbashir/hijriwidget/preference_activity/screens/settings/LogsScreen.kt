package me.amrbashir.hijriwidget.preference_activity.screens.settings

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.Logs
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.composables.ScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.mainNavigationBarPadding
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ConnectedButtonGroup

const val LOGS_DESTINATION = "/Settings/Logs"

fun NavGraphBuilder.logsDestination() {
	composable(LOGS_DESTINATION) { LogsScreen() }
}

@Composable
private fun LogsScreen() {
	LocalAppBarTitle.current.value = stringResource(R.string.logs)

	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	val state by Logs.state.collectAsStateWithLifecycle()
	var refreshing by remember { mutableStateOf(false) }

	fun refreshLogs() {
		if (refreshing) return
		refreshing = true
		scope.launch {
			try {
				Logs.refresh()
			} finally {
				refreshing = false
			}
		}
	}

	ScreenLayout(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		LogsActions(
			canSend = state.status == Logs.Status.Ready,
			refreshing = refreshing,
			onSend = { Logs.send(context) },
			onRefresh = ::refreshLogs,
			onClear = { scope.launch { Logs.clear() } },
		)

		state.error?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }
		
		LogsContainer(state = state, modifier = Modifier.weight(1F).fillMaxWidth().mainNavigationBarPadding())
	}
}

@Composable
private fun LogsActions(
	canSend: Boolean,
	refreshing: Boolean,
	onSend: () -> Unit,
	onRefresh: () -> Unit,
	onClear: () -> Unit,
) {
	ConnectedButtonGroup(modifier = Modifier.fillMaxWidth()) {
		ConnectedButton(
			modifier = Modifier.weight(1F),
			enabled = canSend && !refreshing,
			onClick = onSend,
		) {
			Text(stringResource(R.string.send_logs))
		}
		ConnectedButton(
			modifier = Modifier.width(56.dp),
			enabled = !refreshing,
			onClick = onRefresh,
		) {
			RefreshIcon(loading = refreshing)
		}
		ConnectedButton(
			modifier = Modifier.width(56.dp),
			enabled = !refreshing,
			onClick = onClear,
		) {
			Icon(
				imageVector = Icons.Outlined.DeleteSweep,
				contentDescription = stringResource(R.string.clear_logs),
			)
		}
	}
}

@Composable
private fun LogsContainer(
	state: Logs.State,
	modifier: Modifier = Modifier,
) {
	val shape = RoundedCornerShape(16.dp)
	Column(
		modifier =
			modifier
				.clip(shape)
				.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
				.verticalScroll(rememberScrollState())
				.padding(12.dp),
	) {
		when (state.status) {
			Logs.Status.Loading -> Text(stringResource(R.string.loading_logs))
			Logs.Status.Failed -> Unit
			Logs.Status.Ready ->
				SelectionContainer {
					Text(
						state.logs.ifBlank { stringResource(R.string.no_logs) },
						style = MaterialTheme.typography.bodySmall,
						fontFamily = FontFamily.Monospace,
					)
				}
		}
	}
}

@Composable
private fun RefreshIcon(loading: Boolean) {
	val rotation =
		if (loading) {
			val transition = rememberInfiniteTransition(label = "refresh")
			val angle by transition.animateFloat(
				initialValue = 0F,
				targetValue = 360F,
				animationSpec =
					infiniteRepeatable(
						tween(800, easing = LinearEasing),
						RepeatMode.Restart,
					),
				label = "refresh rotation",
			)
			angle
		} else {
			0F
		}

	Icon(
		imageVector = Icons.Outlined.Refresh,
		modifier = Modifier.rotate(rotation),
		contentDescription = stringResource(R.string.refresh_logs),
	)
}
