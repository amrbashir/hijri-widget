package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mohamedrejeb.compose.dnd.DragAndDropContainer
import com.mohamedrejeb.compose.dnd.DragAndDropState
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.rememberDragAndDropState
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.isAsciiLetter
import me.amrbashir.hijriwidget.parseDateFormat
import me.amrbashir.hijriwidget.validateDatePatternStructure
import java.util.UUID

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun VisualDateFormatEditor(
	initialRows: List<DateFormatRow>,
	onPatternChange: (String) -> Unit,
) {
	var rows by remember { mutableStateOf(initialRows) }

	var selectedComponentId by remember { mutableStateOf<String?>(null) }
	var insertionPosition by remember { mutableStateOf<DateComponentPosition?>(null) }

	val dragState = rememberDragAndDropState<DateFormatComponent>(dragAfterLongPress = true)
	val draggedComponent = dragState.draggedItem?.data
	var dropPosition by remember { mutableStateOf<DateComponentPosition?>(null) }

	// Track the insertion gap targeted during dragging.
	LaunchedEffect(draggedComponent?.id) {
		// Clear the previous drop indicator.
		dropPosition = null

		// Start at the dragged component’s original insertion position.
		if (draggedComponent != null) {
			val sourceRow = rows.firstOrNull { row -> row.components.any { it.id == draggedComponent.id } }
			sourceRow?.let { row ->
				val index = row.components.indexOfFirst { it.id == draggedComponent.id }
				val nextComponent = row.components.getOrNull(index + 1)
				dropPosition = DateComponentPosition(row.id, nextComponent?.id)
			}
		}
	}

	fun cleanupEmptyComponents() {
		val component = rows.flatMap { it.components }.firstOrNull { it.id == selectedComponentId }
		if (component?.kind == DateComponentKind.Text && component.value.isEmpty()) {
			rows = rows.deleteComponent(component.id)
			onPatternChange(rows.toDatePattern())
		}
	}

	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		Text(
			stringResource(R.string.date_editor_hint),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)

		SharedTransitionLayout {
			DragAndDropContainer(state = dragState) {
				Box {
					if (draggedComponent != null) {
						DateComponentDeleteZone(
							dragState = dragState,
							onComponentDropped = { component ->
								rows = rows.deleteComponent(component.id)
								onPatternChange(rows.toDatePattern())
								if (selectedComponentId == component.id) selectedComponentId = null
								dropPosition = null
							},
						)
					}

					Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
						rows.forEachIndexed { rowIndex, row ->
							key(row.id) {
								DateEditorRow(
									row = row,
									rowIndex = rowIndex,
									dragState = dragState,
									dropPosition = dropPosition,
									selectedComponentId = selectedComponentId,
									onComponentMoved = { dropPosition = it },
									onComponentDropped = { id, at ->
										rows = rows.moveComponent(id, at)
										onPatternChange(rows.toDatePattern())
									},
									onComponentSelected = { id ->
										// Clean up empty components only when switching to a different component.
										if (id != selectedComponentId) cleanupEmptyComponents()
										selectedComponentId = id
									},
									onInsertionRequested = { insertionPosition = it },
									onRowDeleted = {
										cleanupEmptyComponents()
										rows = rows.deleteRow(row.id)
										onPatternChange(rows.toDatePattern())
										selectedComponentId = null
									},
								)

								// Each row hosts its own options, animated when the selected component changes.
								AnimatedDateComponentOptions(
									component = row.components.firstOrNull { it.id == selectedComponentId },
									onChange = {
										rows = rows.replaceComponent(it)
										onPatternChange(rows.toDatePattern())
									},
									onDelete = { id ->
										rows = rows.deleteComponent(id)
										onPatternChange(rows.toDatePattern())
										selectedComponentId = null
									},
									onDismiss = {
										cleanupEmptyComponents()
										selectedComponentId = null
									},
								)
							}
						}
					}
				}
			}
		}
	}

	// Show the component picker sheet if inserting.
	insertionPosition?.let { at ->
		DateComponentSheet(
			onDismiss = { insertionPosition = null },
			onInsert = { component ->
				rows = rows.insertComponent(component, at)
				cleanupEmptyComponents()
				onPatternChange(rows.toDatePattern())
				selectedComponentId = component.id.takeUnless { component.isLineBreak }
				insertionPosition = null
			},
		)
	}
}

@Composable
private fun BoxScope.DateComponentDeleteZone(
	dragState: DragAndDropState<DateFormatComponent>,
	onComponentDropped: (DateFormatComponent) -> Unit,
) {
	val height = 40.dp
	var hovered by remember { mutableStateOf(false) }

	Surface(
		modifier =
			Modifier
				.align(
					Alignment.TopCenter,
				).offset(y = -height)
				.zIndex(1F)
				.width(220.dp)
				.height(height)
				.dropTarget(
					key = "delete-component",
					state = dragState,
					dropAnimationEnabled = false,
					onDragEnter = { hovered = true },
					onDragExit = { hovered = false },
					onDrop = {
						hovered = false
						onComponentDropped(it.data)
					},
				),
		shape =
			GenericShape { size, _ ->
				moveTo(0F, size.height)
				cubicTo(size.width * 0.2F, size.height, size.width * 0.2F, 0F, size.width / 2F, 0F)
				cubicTo(size.width * 0.8F, 0F, size.width * 0.8F, size.height, size.width, size.height)
				close()
			},
		color = MaterialTheme.colorScheme.error.copy(alpha = if (hovered) 0.9F else 0.7F),
		contentColor = MaterialTheme.colorScheme.onError,
	) {
		Icon(
			imageVector = Icons.Outlined.Delete,
			contentDescription = stringResource(R.string.date_editor_delete_component),
			modifier = Modifier.wrapContentSize(Alignment.Center).size(20.dp),
		)
	}
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.AnimatedDateComponentOptions(
	component: DateFormatComponent?,
	onChange: (DateFormatComponent) -> Unit,
	onDelete: (String) -> Unit,
	onDismiss: () -> Unit,
) {
	AnimatedContent(
		targetState = component,
		contentKey = { it?.id },
		label = "Component options",
	) { visibleComponent ->
		if (visibleComponent != null) {
			DateComponentOptions(
				component = visibleComponent,
				modifier =
					Modifier.sharedElement(
						sharedContentState = rememberSharedContentState("component-options"),
						animatedVisibilityScope = this@AnimatedContent,
					),
				onChange = onChange,
				onDelete = { onDelete(visibleComponent.id) },
				onDismiss = onDismiss,
			)
		}
	}
}

internal enum class DateComponentKind(
	val patterns: List<String> = emptyList(),
	val defaultPattern: String = "",
) {
	Day(listOf("d", "dd"), "d"),
	Month(listOf("M", "MM", "MMM", "MMMM", "MMMMM"), "MMMM"),
	Year(listOf("y", "yy", "yyy", "yyyy"), "yyyy"),
	Weekday(listOf("E", "EE", "EEE", "EEEE", "EEEEE", "EEEEEE"), "EEEE"),
	Text,

	/**
	 * Preserves codes entered through Edit Pattern, e.g. G renders the calendar era.
	 * Not offered in the picker, but selecting an existing component lets users edit its code.
	 */
	Pattern,
}

internal val DateComponentKind.label: Int
	get() =
		when (this) {
			DateComponentKind.Day -> R.string.date_editor_day
			DateComponentKind.Month -> R.string.date_editor_month
			DateComponentKind.Year -> R.string.date_editor_year
			DateComponentKind.Weekday -> R.string.date_editor_weekday
			DateComponentKind.Text -> R.string.date_editor_text
			DateComponentKind.Pattern -> R.string.date_editor_pattern
		}

/** Editor tokens only; date rendering continues to use the existing ICU formatter. */
internal data class DateFormatComponent(
	val kind: DateComponentKind,
	val value: String,
	val languageTag: String? = null,
	val id: String = UUID.randomUUID().toString(),
)

/** Newline text splits a row during insertion rather than becoming a visible component. */
internal val DateFormatComponent.isLineBreak: Boolean
	get() = kind == DateComponentKind.Text && value == "\n"

internal fun DateFormatComponent.toDatePattern(): String {
	if (value.isEmpty()) return ""

	val body =
		when {
			kind != DateComponentKind.Text -> value
			value.all { it == '\'' } -> value.replace("'", "''")
			else -> "'${value.replace("'", "''")}'"
		}

	// Anonymous groups preserve boundaries between adjacent fields such as {d}{d}.
	return "${languageTag.orEmpty()}{$body}"
}

internal enum class DateSeparator(
	val value: String,
) {
	Space(" "),
	Slash("/"),
	Dash("-"),
	Comma(","),
	NewLine("\n"),
	;

	val label: String
		@Composable get() =
			when (this) {
				Space -> "␣"
				NewLine -> stringResource(R.string.date_editor_new_line)
				else -> value
			}

	val description: String
		@Composable get() =
			stringResource(
				when (this) {
					Space -> R.string.date_editor_space
					Slash -> R.string.date_editor_slash
					Dash -> R.string.date_editor_dash
					Comma -> R.string.date_editor_comma
					NewLine -> R.string.date_editor_start_new_line
				},
			)
}

internal data class DateFormatRow(
	val components: List<DateFormatComponent> = emptyList(),
	val id: String = UUID.randomUUID().toString(),
)

internal data class DateComponentPosition(
	val rowId: String,
	val beforeId: String? = null,
)

/** Returns null for malformed quotes or groups, preserving the original raw pattern. */
internal fun parseDateFormatRows(pattern: String): List<DateFormatRow>? {
	if (validateDatePatternStructure(pattern) != null) return null

	val rows = mutableListOf(mutableListOf<DateFormatComponent>())

	fun text(
		value: String,
		language: String?,
	) {
		value.split('\n').forEachIndexed { index, line ->
			if (index > 0) rows.add(mutableListOf())
			if (line.isNotEmpty()) {
				rows.last().add(
					DateFormatComponent(DateComponentKind.Text, line, language),
				)
			}
		}
	}

	fun readQuotedText(
		input: String,
		start: Int,
	): Pair<String, Int>? {
		var index = start + 1 // Skip the opening quote.
		if (index < input.length && input[index] == '\'') {
			index++
			return "'" to index
		}

		val literal = StringBuilder()
		while (index < input.length) {
			val char = input[index++]
			if (char != '\'') {
				literal.append(char)
				continue
			}

			// A doubled quote is literal; a single quote ends the text.
			if (index < input.length && input[index] == '\'') {
				index++
				literal.append('\'')
			} else {
				return literal.toString() to index
			}
		}
		return null
	}

	for (segment in parseDateFormat(pattern)) {
		val input = segment.format
		var index = 0

		while (index < input.length) {
			val start = index
			val char = input[index]

			when {
				char == '\'' -> {
					val (literal, nextIndex) = readQuotedText(input, index) ?: return null
					index = nextIndex
					text(literal, segment.langCode)
				}

				char.isAsciiLetter() -> {
					// Repeated letters form one date field, e.g. MMMM.
					while (index < input.length && input[index] == char) index++
					val value = input.substring(start, index)
					val kind = DateComponentKind.entries.firstOrNull { value in it.patterns }
					val component = DateFormatComponent(kind ?: DateComponentKind.Pattern, value, segment.langCode)
					rows.last().add(component)
				}

				else -> {
					// Collect spaces and punctuation until the next quote or date field.
					while (index < input.length && input[index] != '\'' && !input[index].isAsciiLetter()) index++
					val literal = input.substring(start, index)
					text(literal, segment.langCode)
				}
			}
		}
	}
	return rows.map { DateFormatRow(it.toList()) }
}

internal fun List<DateFormatRow>.toDatePattern(): String =
	joinToString("\n") { row ->
		row.components.joinToString("") { it.toDatePattern() }
	}

internal fun List<DateFormatRow>.insertComponent(
	component: DateFormatComponent,
	at: DateComponentPosition,
): List<DateFormatRow> =
	flatMap { row ->
		if (row.id != at.rowId) return@flatMap listOf(row)

		val beforeIndex = row.components.indexOfFirst { it.id == at.beforeId }
		val insertionIndex = if (beforeIndex >= 0) beforeIndex else row.components.size

		if (component.isLineBreak) {
			val before = row.copy(components = row.components.take(insertionIndex))
			val after = DateFormatRow(components = row.components.drop(insertionIndex))
			return@flatMap listOf(before, after)
		}

		val components = row.components.toMutableList()
		components.add(insertionIndex, component)
		listOf(row.copy(components = components))
	}

internal fun List<DateFormatRow>.moveComponent(
	id: String,
	at: DateComponentPosition,
): List<DateFormatRow> {
	if (at.beforeId == id) return this

	val component = flatMap { it.components }.firstOrNull { it.id == id } ?: return this
	val remainingRows =
		map { row ->
			row.copy(components = row.components.filterNot { it.id == id })
		}
	return remainingRows.insertComponent(component, at)
}

internal fun List<DateFormatRow>.replaceComponent(
	replacement: DateFormatComponent,
): List<DateFormatRow> =
	map { row ->
		val components = row.components.map { if (it.id == replacement.id) replacement else it }
		row.copy(components = components)
	}

internal fun List<DateFormatRow>.deleteComponent(id: String): List<DateFormatRow> =
	map { row -> row.copy(components = row.components.filterNot { it.id == id }) }

internal fun List<DateFormatRow>.deleteRow(id: String): List<DateFormatRow> =
	filterNot { it.id == id }.ifEmpty { listOf(DateFormatRow()) }
