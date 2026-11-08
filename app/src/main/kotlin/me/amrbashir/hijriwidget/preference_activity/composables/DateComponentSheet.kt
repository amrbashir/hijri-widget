package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardReturn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateComponentSheet(
	onDismiss: () -> Unit,
	onInsert: (DateFormatComponent) -> Unit,
) {
	ModalBottomSheet(
		onDismissRequest = onDismiss,
		sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
	) {
		Column(
			modifier =
				Modifier
					.fillMaxWidth()
					.verticalScroll(
						rememberScrollState(),
					).padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			Column(
				modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
				verticalArrangement = Arrangement.spacedBy(6.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Text(
					text = stringResource(R.string.date_editor_add),
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.headlineSmall,
				)
				Text(
					text = stringResource(R.string.date_editor_add_description),
					textAlign = TextAlign.Center,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}

			DateComponentGrid(
				onInsert = onInsert,
				modifier = Modifier.fillMaxWidth(),
			)

			FlowRow(
				modifier = Modifier.fillMaxWidth(),
				maxItemsInEachRow = 4,
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				DateSeparator.entries.filter { it != DateSeparator.NewLine }.forEach { separator ->
					DateSeparatorTile(
						symbol = separator.label,
						label = separator.description,
						onClick = { onInsert(DateFormatComponent(DateComponentKind.Text, separator.value)) },
						modifier = Modifier.widthIn(min = 64.dp).weight(1F),
					)
				}
			}

			DateNewLineButton(
				onClick = {
					onInsert(
						DateFormatComponent(DateComponentKind.Text, DateSeparator.NewLine.value),
					)
				},
				modifier = Modifier.fillMaxWidth(),
			)

			Text(
				text = stringResource(R.string.date_editor_or),
				modifier = Modifier.fillMaxWidth(),
				textAlign = TextAlign.Center,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)

			DateTextInput(
				modifier = Modifier.fillMaxWidth(),
				onInsert = { onInsert(DateFormatComponent(DateComponentKind.Text, it)) },
			)
		}
	}
}

@Composable
private fun DateComponentGrid(
	onInsert: (DateFormatComponent) -> Unit,
	modifier: Modifier = Modifier,
) {
	val prefsManager = LocalSelectedWidget.current.prefsManager

	// Two 14.dp paddings, a 14.dp gap, and the label and preview line heights.
	val tileHeight =
		with(LocalDensity.current) {
			42.dp +
				MaterialTheme.typography.bodySmall.lineHeight
					.toDp() +
				MaterialTheme.typography.titleLarge.lineHeight
					.toDp()
		}

	// Bound both rows so the surrounding sheet owns vertical scrolling.
	LazyVerticalGrid(
		columns = GridCells.Fixed(2),
		modifier = modifier.height(tileHeight * 2 + 10.dp),
		userScrollEnabled = false,
		horizontalArrangement = Arrangement.spacedBy(10.dp),
		verticalArrangement = Arrangement.spacedBy(10.dp),
	) {
		items(
			listOf(
				DateComponentKind.Day,
				DateComponentKind.Month,
				DateComponentKind.Year,
				DateComponentKind.Weekday,
			),
		) { kind ->
			val component = DateFormatComponent(kind, kind.defaultPattern)
			DateComponentTile(
				label = stringResource(kind.label),
				preview =
					HijriDate
						.todayFormatted(
							prefsManager,
							component.toDatePattern(),
						),
				onClick = { onInsert(component) },
				modifier = Modifier.height(tileHeight),
			)
		}
	}
}

@Composable
private fun DateComponentTile(
	label: String,
	preview: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		onClick = onClick,
		modifier = modifier,
		shape = MaterialTheme.shapes.medium,
		color = MaterialTheme.colorScheme.surfaceContainerHigh,
	) {
		Column(
			modifier = Modifier.padding(14.dp),
			verticalArrangement = Arrangement.spacedBy(14.dp),
		) {
			Text(
				text = label,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)

			Text(
				text = preview,
				style = MaterialTheme.typography.titleLarge,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}
	}
}

@Composable
private fun DateSeparatorTile(
	symbol: String,
	label: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		onClick = onClick,
		modifier = modifier,
		shape = MaterialTheme.shapes.small,
		color = MaterialTheme.colorScheme.surfaceContainerHigh,
	) {
		Column(
			modifier = Modifier.padding(horizontal = 4.dp, vertical = 10.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(6.dp),
		) {
			Text(
				symbol,
				style = MaterialTheme.typography.titleLarge,
				color = MaterialTheme.colorScheme.primary,
			)
			Text(
				label,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}

@Composable
private fun DateNewLineButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		onClick = onClick,
		modifier = modifier,
		shape = MaterialTheme.shapes.small,
		color = MaterialTheme.colorScheme.secondaryContainer,
		contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
	) {
		Row(
			modifier = Modifier.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
		) {
			Icon(Icons.Outlined.KeyboardReturn, null)
			Text(DateSeparator.NewLine.description, style = MaterialTheme.typography.labelLarge)
		}
	}
}

@Composable
private fun DateTextInput(
	onInsert: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	var text by remember { mutableStateOf("") }

	Surface(
		modifier = modifier,
		shape = MaterialTheme.shapes.medium,
		color = MaterialTheme.colorScheme.surfaceContainerHigh,
	) {
		Column(
			modifier = Modifier.padding(12.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			val label = stringResource(R.string.date_editor_text)
			Text(
				text = label,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)

			BasicTextField(
				value = text,
				onValueChange = { text = it },
				singleLine = true,
				textStyle =
					MaterialTheme.typography.bodyLarge.copy(
						color = MaterialTheme.colorScheme.onSurface,
					),
				cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
				modifier =
					Modifier
						.fillMaxWidth()
						.semantics { contentDescription = label },
				decorationBox = { input ->
					Box {
						if (text.isEmpty()) {
							Text(
								text = stringResource(R.string.date_editor_text_placeholder),
								style = MaterialTheme.typography.bodyLarge,
								color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6F),
							)
						}
						input()
					}
				},
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(12.dp),
			) {
				Text(
					text = stringResource(R.string.date_editor_text_hint),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.weight(1F),
				)
				Button(
					enabled = text.isNotEmpty(),
					onClick = { onInsert(text) },
					shape = MaterialTheme.shapes.small,
					contentPadding = PaddingValues(horizontal = 12.dp),
				) {
					Icon(Icons.Outlined.Add, null, Modifier.size(18.dp))
					Text(stringResource(R.string.date_editor_add_text), Modifier.padding(start = 6.dp))
				}
			}
		}
	}
}
