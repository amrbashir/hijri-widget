package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget

@Composable
internal fun DateComponentOptions(
	component: DateFormatComponent,
	onChange: (DateFormatComponent) -> Unit,
	onDelete: () -> Unit,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
) {
	val prefsManager = LocalSelectedWidget.current.prefsManager

	Card(
		modifier = modifier,
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
	) {
		Column(modifier = Modifier.fillMaxWidth()) {
			DateComponentOptionsHeader(
				label = stringResource(component.kind.label),
				onDelete = onDelete,
				onDismiss = onDismiss,
			)

			HorizontalDivider()

			Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
				if (component.kind.patterns.isNotEmpty()) {
					FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
						component.kind.patterns.forEach { value ->
							val pattern = component.copy(value = value).toDatePattern()
							val preview = HijriDate.todayFormatted(prefsManager, pattern)

							FilterChip(
								selected = component.value == value,
								onClick = { onChange(component.copy(value = value)) },
								label = {
									Text(preview)
								},
							)
						}
					}
				} else {
					OutlinedTextField(
						value = component.value,
						onValueChange = { onChange(component.copy(value = it)) },
						label = {
							Text(stringResource(component.kind.label))
						},
						singleLine = true,
						modifier = Modifier.fillMaxWidth(),
					)
				}

				key(component.id) {
					DateComponentLanguagePicker(languageTag = component.languageTag) {
						onChange(component.copy(languageTag = it))
					}
				}
			}
		}
	}
}

@Composable
private fun DateComponentOptionsHeader(
	label: String,
	onDelete: () -> Unit,
	onDismiss: () -> Unit,
) {
	Row(
		modifier = Modifier.fillMaxWidth().padding(12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		CloseComponentOptionsButton(onClick = onDismiss)
		Text(
			label,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.weight(1F),
			textAlign = TextAlign.Center,
		)
		DeleteComponentButton(onClick = onDelete)
	}
}

@Composable
private fun DeleteComponentButton(onClick: () -> Unit) {
	CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
		IconButton(onClick = onClick, modifier = Modifier.size(24.dp)) {
			Icon(
				imageVector = Icons.Outlined.Delete,
				contentDescription = stringResource(R.string.date_editor_delete_component),
				modifier = Modifier.size(18.dp),
			)
		}
	}
}

@Composable
private fun CloseComponentOptionsButton(onClick: () -> Unit) {
	CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
		IconButton(onClick = onClick, modifier = Modifier.size(24.dp)) {
			Icon(
				imageVector = Icons.Outlined.Close,
				contentDescription = stringResource(R.string.date_editor_close_options),
				modifier = Modifier.size(18.dp),
			)
		}
	}
}
