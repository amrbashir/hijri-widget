package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.HorizontalAlignment
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.VerticalAlignment
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget
import me.amrbashir.hijriwidget.preference_activity.animatedContentComposable
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.ScrollableScreenColumn
import me.amrbashir.hijriwidget.preference_activity.composables.ui.Item
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ItemGroup

private const val LAYOUT_DESTINATION = "/preferences/layout"

fun NavGraphBuilder.layoutDestination() {
	animatedContentComposable(route = LAYOUT_DESTINATION) { LayoutScreen() }
}

fun NavController.navigateToLayout() {
	navigate(route = LAYOUT_DESTINATION)
}

@Composable
private fun LayoutScreen() {
	LocalAppBarTitle.current.value = stringResource(R.string.preferences_layout_title)
	val prefsManager = LocalSelectedWidget.current.prefsManager

	PreferenceScreenLayout {
		ScrollableScreenColumn(
			modifier = Modifier,
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			AlignmentPicker(prefsManager = prefsManager)

			ItemGroup(label = stringResource(R.string.layout_padding)) {
				PaddingPicker(prefsManager = prefsManager)
			}
		}
	}
}

@Composable
private fun AlignmentPicker(prefsManager: PreferencesManager) {
	ItemGroup(label = stringResource(R.string.layout_alignment)) {
		Item(label = stringResource(R.string.layout_horizontal)) {
			AlignmentOptions(
				labels = listOf(R.string.layout_left, R.string.layout_center, R.string.layout_right),
				selectedIndex = prefsManager.horizontalAlignment.value.ordinal,
				onSelected = {
					prefsManager.horizontalAlignment.value = HorizontalAlignment.entries[it]
				},
			)
		}
		Item(label = stringResource(R.string.layout_vertical)) {
			AlignmentOptions(
				labels = listOf(R.string.layout_top, R.string.layout_center, R.string.layout_bottom),
				selectedIndex = prefsManager.verticalAlignment.value.ordinal,
				onSelected = { prefsManager.verticalAlignment.value = VerticalAlignment.entries[it] },
			)
		}
	}
}

@Composable
private fun AlignmentOptions(
	labels: List<Int>,
	selectedIndex: Int,
	onSelected: (Int) -> Unit,
) {
	SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
		labels.forEachIndexed { index, label ->
			SegmentedButton(
				selected = index == selectedIndex,
				onClick = { onSelected(index) },
				shape = SegmentedButtonDefaults.itemShape(index = index, count = labels.size),
			) {
				Text(stringResource(label))
			}
		}
	}
}

@Composable
private fun PaddingPicker(prefsManager: PreferencesManager) {
	val focusManager = LocalFocusManager.current
	var top by remember(prefsManager) { mutableStateOf(prefsManager.paddingTop.value.toString()) }
	var start by remember(prefsManager) { mutableStateOf(prefsManager.paddingStart.value.toString()) }
	var end by remember(prefsManager) { mutableStateOf(prefsManager.paddingEnd.value.toString()) }
	var bottom by remember(
		prefsManager,
	) { mutableStateOf(prefsManager.paddingBottom.value.toString()) }

	Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)) {
		Column(
			modifier = Modifier.fillMaxWidth().padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				Spacer(modifier = Modifier.weight(1F))
				PaddingInput(
					label = stringResource(R.string.layout_top),
					value = top,
					onValueChange = {
						top = it
						it.toIntOrNull()?.let { value -> prefsManager.paddingTop.value = value }
					},
					modifier = Modifier.weight(1F),
				)
				Spacer(modifier = Modifier.weight(1F))
			}

			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(10.dp),
			) {
				PaddingInput(
					label = stringResource(R.string.layout_start),
					value = start,
					onValueChange = {
						start = it
						it.toIntOrNull()?.let { value -> prefsManager.paddingStart.value = value }
					},
					modifier = Modifier.weight(1F),
				)
				Spacer(modifier = Modifier.weight(1F))
				PaddingInput(
					label = stringResource(R.string.layout_end),
					value = end,
					onValueChange = {
						end = it
						it.toIntOrNull()?.let { value -> prefsManager.paddingEnd.value = value }
					},
					modifier = Modifier.weight(1F),
				)
			}

			Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
				Spacer(modifier = Modifier.weight(1F))
				PaddingInput(
					label = stringResource(R.string.layout_bottom),
					value = bottom,
					onValueChange = {
						bottom = it
						it.toIntOrNull()?.let { value -> prefsManager.paddingBottom.value = value }
					},
					modifier = Modifier.weight(1F),
				)
				Spacer(modifier = Modifier.weight(1F))
			}

			PaddingPickerFooter(
				onReset = {
					focusManager.clearFocus()
					prefsManager.paddingStart.reset()
					prefsManager.paddingTop.reset()
					prefsManager.paddingEnd.reset()
					prefsManager.paddingBottom.reset()
					top = prefsManager.paddingTop.value.toString()
					start = prefsManager.paddingStart.value.toString()
					end = prefsManager.paddingEnd.value.toString()
					bottom = prefsManager.paddingBottom.value.toString()
				},
			)
		}
	}
}

@Composable
private fun PaddingPickerFooter(onReset: () -> Unit) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(10.dp),
	) {
		Text(
			stringResource(R.string.layout_padding_hint),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		TextButton(modifier = Modifier.align(Alignment.End), onClick = onReset) {
			Text(stringResource(R.string.reset_to_default))
		}
	}
}

@Composable
private fun PaddingInput(
	label: String,
	value: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	val invalid = value.isNotEmpty() && value.toIntOrNull() == null
	val focusManager = LocalFocusManager.current

	OutlinedTextField(
		value = value,
		onValueChange = {
			if (it.all(Char::isDigit)) onValueChange(it)
		},
		label = { Text(label) },
		singleLine = true,
		isError = invalid,
		textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
		keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
		keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
		modifier = modifier,
	)
}
