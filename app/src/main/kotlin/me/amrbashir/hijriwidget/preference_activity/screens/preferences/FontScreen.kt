package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget
import me.amrbashir.hijriwidget.preference_activity.animatedContentComposable
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.ui.Item
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ItemGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.RadioIcon
import me.amrbashir.hijriwidget.systemFontFamilies
import android.graphics.Typeface as AndroidTypeface

const val FONT_DESTINATION = "/preferences/font"

fun NavGraphBuilder.fontDestination() {
	animatedContentComposable(route = FONT_DESTINATION) { FontScreen() }
}

fun NavController.navigateToFont() {
	navigate(route = FONT_DESTINATION)
}

@Composable
internal fun FontScreen() {
	val prefsManager = LocalSelectedWidget.current.prefsManager
	var query by remember { mutableStateOf("") }

	val defaultLabel = stringResource(R.string.font_system_default)
	val defaultChoice = FontChoice(label = defaultLabel)

	val families by produceState(listOf("sans-serif", "serif", "monospace")) {
		value = withContext(Dispatchers.IO) { systemFontFamilies() }
	}
	val familyChoices = families.map { family -> FontChoice(label = family, familyName = family) }

	val allChoices = listOf(defaultChoice) + familyChoices
	val choices = allChoices.filter { it.label.contains(query, ignoreCase = true) }

	PreferenceScreenLayout {
		Column(
			modifier = Modifier.fillMaxSize(),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			FontSearchField(
				value = query,
				onValueChange = { query = it },
				modifier = Modifier.fillMaxWidth(),
			)

			Column(
				modifier = Modifier.weight(1F).verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(16.dp),
			) {
				ItemGroup(label = stringResource(R.string.font_family)) {
					choices.forEach { choice ->
						FontChoiceItem(
							choice = choice,
							selected = choice.familyName == prefsManager.textFontFamily.value.ifEmpty { null },
							onClick = {
								prefsManager.textFontFamily.value = choice.familyName.orEmpty()
								prefsManager.textWeight.value = prefsManager.getFontWeight().toFloat()
							},
						)
					}
				}

				if (choices.isEmpty()) {
					Text(stringResource(R.string.font_no_results), style = MaterialTheme.typography.bodySmall)
				}
			}
		}
	}
}

@Composable
private fun FontSearchField(
	value: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	TextField(
		value = value,
		onValueChange = onValueChange,
		placeholder = { Text(stringResource(R.string.font_search)) },
		leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
		shape = CircleShape,
		colors =
			TextFieldDefaults.colors(
				focusedContainerColor = MaterialTheme.colorScheme.surfaceBright,
				unfocusedContainerColor = MaterialTheme.colorScheme.surfaceBright,
				focusedIndicatorColor = Color.Transparent,
				unfocusedIndicatorColor = Color.Transparent,
			),
		singleLine = true,
		modifier = modifier,
	)
}

@Composable
private fun FontChoiceItem(
	choice: FontChoice,
	selected: Boolean,
	onClick: () -> Unit,
) {
	val typeface =
		remember(choice.familyName) {
			AndroidTypeface.create(choice.familyName, AndroidTypeface.NORMAL)
		}

	Item(
		label = choice.label,
		labelFontFamily = FontFamily(Typeface(typeface)),
		icon = { RadioIcon(selected = selected) },
		onClick = onClick,
	)
}

private data class FontChoice(
	val label: String,
	val familyName: String? = null,
)
