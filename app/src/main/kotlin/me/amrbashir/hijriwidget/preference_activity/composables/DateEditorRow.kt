package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.compose.dnd.DragAndDropState
import com.mohamedrejeb.compose.dnd.drag.DraggableItem
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget
import me.amrbashir.hijriwidget.preference_activity.composables.ui.underline

@Composable
internal fun DateEditorRow(
	row: DateFormatRow,
	rowIndex: Int,
	dragState: DragAndDropState<DateFormatComponent>,
	dropPosition: DateComponentPosition?,
	selectedComponentId: String?,
	onComponentMoved: (DateComponentPosition?) -> Unit,
	onComponentDropped: (String, DateComponentPosition) -> Unit,
	onComponentSelected: (String) -> Unit,
	onInsertionRequested: (DateComponentPosition) -> Unit,
	onRowDeleted: () -> Unit,
) {
	val draggedComponent = dragState.draggedItem?.data
	val showEmptyRowHint =
		row.components.isEmpty() && dropPosition?.rowId != row.id

	fun target(
		at: DateComponentPosition,
		priority: Float,
	): Modifier =
		Modifier.dropTarget(
			key = if (priority == 0F) row.id else at,
			state = dragState,
			zIndex = priority,
			dropAnimationEnabled = false,
			onDragEnter = { onComponentMoved(at) },
			onDragExit = { if (dropPosition == at) onComponentMoved(null) },
			onDrop = { drag ->
				onComponentDropped(drag.data.id, at)
				onComponentSelected(drag.data.id)
			},
		)

	Surface(
		modifier = Modifier.fillMaxWidth(),
		shape = RoundedCornerShape(10.dp),
		border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
		color = MaterialTheme.colorScheme.surfaceContainerLow,
	) {
		Column {
			DateEditorRowHeader(
				rowNumber = rowIndex + 1,
				deleteEnabled = draggedComponent == null,
				onRowDeleted = onRowDeleted,
			)

			HorizontalDivider()

			Row(
				modifier =
					Modifier
						.fillMaxWidth()
						.then(target(DateComponentPosition(row.id), 0F))
						.heightIn(
							min = 60.dp,
						).horizontalScroll(rememberScrollState())
						.padding(6.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				if (showEmptyRowHint) {
					Text(
						stringResource(R.string.date_editor_empty_row),
						style = MaterialTheme.typography.bodySmall,
					)
				}

				row.components.forEach { component ->
					key(component.id) {
						val at = DateComponentPosition(row.id, component.id)
						val moving = draggedComponent?.id == component.id

						Row(
							modifier = target(at, 1F),
							verticalAlignment = Alignment.CenterVertically,
						) {
							DateComponentInsertionPoint(
								showDropIndicator = draggedComponent != null && dropPosition == at,
								onInsertionRequested = { onInsertionRequested(at) },
							)

							DraggableDateComponent(
								component = component,
								selected = selectedComponentId == component.id,
								moving = moving,
								dragState = dragState,
								onClick = { onComponentSelected(component.id) },
							)
						}
					}
				}

				val endInsertion = DateComponentPosition(row.id)
				DateComponentInsertionPoint(
					modifier = target(endInsertion, 1F),
					showDropIndicator = draggedComponent != null && dropPosition == endInsertion,
					onInsertionRequested = { onInsertionRequested(endInsertion) },
				)
			}
		}
	}
}

@Composable
private fun DateComponentInsertionPoint(
	showDropIndicator: Boolean,
	onInsertionRequested: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier.width(32.dp).height(48.dp),
		contentAlignment = Alignment.Center,
	) {
		if (showDropIndicator) {
			VerticalDivider(
				modifier = Modifier.height(40.dp),
				thickness = 3.dp,
				color = MaterialTheme.colorScheme.primary,
			)
		} else {
			AddComponentButton(onClick = onInsertionRequested)
		}
	}
}

@Composable
private fun DateEditorRowHeader(
	rowNumber: Int,
	deleteEnabled: Boolean,
	onRowDeleted: () -> Unit,
) {
	Row(
		modifier =
			Modifier.fillMaxWidth().padding(
				start = 12.dp,
				end = 8.dp,
				top = 4.dp,
				bottom = 4.dp,
			),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			stringResource(R.string.date_editor_row, rowNumber),
			modifier = Modifier.weight(1F),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)

		DeleteRowButton(rowNumber = rowNumber, enabled = deleteEnabled, onClick = onRowDeleted)
	}
}

@Composable
private fun DeleteRowButton(
	rowNumber: Int,
	enabled: Boolean,
	onClick: () -> Unit,
) {
	CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
		IconButton(
			enabled = enabled,
			modifier = Modifier.size(24.dp),
			onClick = onClick,
		) {
			Icon(
				imageVector = Icons.Outlined.Delete,
				contentDescription = stringResource(R.string.date_editor_delete_row, rowNumber),
				modifier = Modifier.size(18.dp),
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

@Composable
private fun DraggableDateComponent(
	component: DateFormatComponent,
	selected: Boolean,
	moving: Boolean,
	dragState: DragAndDropState<DateFormatComponent>,
	onClick: () -> Unit,
) {
	DraggableItem(
		state = dragState,
		key = component.id,
		data = component,
		dropStrategy = DropStrategy.CenterDistance,
		draggableContent = {
			DateComponentValue(
				component = component,
				selected = true,
				onClick = {},
			)
		},
		modifier = Modifier.alpha(if (moving) 0.25F else 1F),
	) {
		DateComponentValue(
			component = component,
			selected = selected,
		) { onClick() }
	}
}

@Composable
private fun DateComponentValue(
	component: DateFormatComponent,
	selected: Boolean,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	val prefsManager = LocalSelectedWidget.current.prefsManager

	val preview =
		if (component.kind == DateComponentKind.Text) {
			when {
				component.value.all { it == ' ' } -> "␣".repeat(component.value.length)
				else -> component.value
			}
		} else {
			HijriDate.todayFormatted(prefsManager, component.toDatePattern()).ifEmpty {
				component.value
			}
		}

	val label =
		if (component.kind == DateComponentKind.Text &&
			component.value.isNotEmpty() &&
			component.value.all { it == ' ' }
		) {
			stringResource(R.string.date_editor_space)
		} else {
			"${stringResource(component.kind.label)}: $preview"
		}

	DateComponentChip(
		preview = preview,
		description = label,
		selected = selected,
		modifier = modifier,
		onClick = onClick,
	)
}

@Composable
private fun DateComponentChip(
	preview: String,
	description: String,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val accent = MaterialTheme.colorScheme.primary
	val backgroundColor = if (selected) accent.copy(alpha = 0.25F) else Color.Transparent
	val underline = if (selected) accent else MaterialTheme.colorScheme.outline

	Surface(
		onClick = onClick,
		modifier =
			modifier.heightIn(min = 48.dp).semantics {
				contentDescription = description
			},
		shape = RoundedCornerShape(4.dp),
		color = backgroundColor,
	) {
		Text(
			preview,
			style = MaterialTheme.typography.headlineSmall,
			maxLines = 1,
			modifier =
				Modifier
					.underline(
						color = underline,
						dashed = !selected,
					).padding(horizontal = 4.dp, vertical = 8.dp),
		)
	}
}

@Composable
private fun AddComponentButton(onClick: () -> Unit) {
	IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
		Icon(
			imageVector = Icons.Outlined.Add,
			contentDescription = stringResource(R.string.date_editor_add),
			tint = MaterialTheme.colorScheme.primary,
		)
	}
}
