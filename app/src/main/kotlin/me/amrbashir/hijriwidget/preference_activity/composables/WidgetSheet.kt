package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.preference_activity.LocalShowWidgetPreview
import me.amrbashir.hijriwidget.preference_activity.darkLightContainerColor

// Matches the height of [WidgetPreview]
private val PREVIEW_HEIGHT = 150.dp
private val PREVIEW_MARGIN = 16.dp

// Default size of [FilledTonalIconButton]
private val SAVE_BUTTON_SIZE = 40.dp
private val SAVE_BUTTON_MARGIN = 8.dp

// Height of [BottomSheetDefaults.DragHandle] including its padding
private val DRAG_HANDLE_HEIGHT = 48.dp

/**
 * Shows [content] in a bottom sheet floating over the home screen,
 * with the widget preview and a save button calling [onSave] above it, then closing it.
 *
 * Dragging the sheet down or going back calls [onDismiss].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSheet(
	onDismiss: () -> Unit,
	onSave: suspend () -> Unit,
	content: @Composable () -> Unit,
) {
	// Starts hidden and has no partially expanded state, so dragging it down hides it
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	val scope = rememberCoroutineScope()

	val dismiss: () -> Unit = {
		scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
	}

	val saveAction =
		rememberSaveAction {
			onSave()
			// Slides out before closing, like dragging it down
			sheetState.hide()
			onDismiss()
		}

	LaunchedEffect(Unit) {
		// Slides in when first shown, separately so dragging it meanwhile doesn't cancel watching it below
		launch { sheetState.expand() }

		// BottomSheetScaffold has no dismiss callback, so dismiss once the sheet is hidden
		snapshotFlow { sheetState.isVisible || sheetState.targetValue != SheetValue.Hidden }
			// Skip the initial hidden state, before it starts showing
			.dropWhile { !it }
			// Wait until it's hidden again
			.first { !it }
		onDismiss()
	}

	BackHandler(onBack = dismiss)

	BoxWithConstraints {
		val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
		// Leaves room above the sheet for the save button and the preview
		val aboveSheetHeight = SAVE_BUTTON_SIZE + SAVE_BUTTON_MARGIN + PREVIEW_HEIGHT + PREVIEW_MARGIN
		val contentHeight =
			maxHeight - statusBarHeight - PREVIEW_MARGIN - aboveSheetHeight - DRAG_HANDLE_HEIGHT

		BottomSheetScaffold(
			scaffoldState = rememberBottomSheetScaffoldState(sheetState),
			sheetPeekHeight = 0.dp,
			sheetContainerColor = MaterialTheme.colorScheme.darkLightContainerColor,
			containerColor = Color.Transparent,
			content = {
				AboveSheetColumn(
					sheetState = sheetState,
					height = aboveSheetHeight,
					sheetHeight = contentHeight + DRAG_HANDLE_HEIGHT,
				) {
					FilledTonalIconButton(onClick = saveAction) {
						Icon(
							imageVector = Icons.Outlined.Check,
							contentDescription = null,
						)
					}

					WidgetPreview()
				}
			},
			sheetContent = {
				Box(
					modifier =
						Modifier
							.height(contentHeight)
							// Away from the status bar, so its insets don't apply
							.consumeWindowInsets(WindowInsets.statusBars),
				) {
					// Shown above the sheet instead
					CompositionLocalProvider(LocalShowWidgetPreview provides false, content = content)
				}
			},
		)
	}
}

/**
 * Lays out [content] right above the sheet, following it while it's dragged
 * and fading out as it goes down, instead of staying at the bottom of the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboveSheetColumn(
	sheetState: SheetState,
	height: Dp,
	sheetHeight: Dp,
	content: @Composable ColumnScope.() -> Unit,
) {
	BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
		val screenHeight = constraints.maxHeight.toFloat()
		// Before the sheet is laid out, it's at the bottom of the screen
		val sheetTop = { runCatching { sheetState.requireOffset() }.getOrDefault(screenHeight) }

		Column(
			horizontalAlignment = Alignment.End,
			verticalArrangement = Arrangement.spacedBy(SAVE_BUTTON_MARGIN),
			modifier =
				Modifier
					.padding(horizontal = PREVIEW_MARGIN)
					.offset { IntOffset(0, (sheetTop() - height.toPx()).toInt()) }
					.graphicsLayer { alpha = ((screenHeight - sheetTop()) / sheetHeight.toPx()).coerceIn(0f, 1f) },
			content = content,
		)
	}
}
