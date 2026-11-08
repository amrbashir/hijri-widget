package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.LANGUAGE_TAG
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.composables.ui.BorderlessSegmentedButton

@Composable
internal fun DateComponentLanguagePicker(
	languageTag: String?,
	onChange: (String?) -> Unit,
) {
	var customLanguage by remember { mutableStateOf(languageTag != null) }
	var language by remember { mutableStateOf(languageTag.orEmpty()) }
	val validLanguage = LANGUAGE_TAG.matches(language)
	val deviceLocale = LocalConfiguration.current.locales[0]

	Column(
		modifier =
			Modifier
				.border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
				.clip(MaterialTheme.shapes.medium),
	) {
		SingleChoiceSegmentedButtonRow(
			modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
			space = 0.dp,
		) {
			listOf(
				R.string.date_editor_device_language,
				R.string.date_editor_custom_language,
			).forEachIndexed { index, label ->
				BorderlessSegmentedButton(
					selected = customLanguage == (index == 1),
					onClick = {
						customLanguage = index == 1
						if (!customLanguage) {
							onChange(null)
						} else if (validLanguage) {
							onChange(language)
						}
					},
					showLeadingDivider = index > 0,
				) {
					Text(stringResource(label))
				}
			}
		}

		HorizontalDivider()

		Column(Modifier.padding(16.dp)) {
			if (customLanguage) {
				CustomDateLanguageField(
					language = language,
					onLanguageChange = {
						language = it
						if (LANGUAGE_TAG.matches(it)) onChange(it)
					},
				)
			} else {
				Text(
					stringResource(R.string.date_editor_device_language_hint, deviceLocale.displayName),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
				)
			}
		}
	}
}

@Composable
private fun CustomDateLanguageField(
	language: String,
	onLanguageChange: (String) -> Unit,
) {
	val invalidLanguage = language.isNotEmpty() && !LANGUAGE_TAG.matches(language)
	val uriHandler = LocalUriHandler.current

	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		Text(
			stringResource(R.string.date_editor_custom_language_hint),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)

		Row(
			modifier = Modifier.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Text(
				stringResource(R.string.date_editor_language),
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
			TextButton(onClick = {
				uriHandler.openUri("http://www.i18nguy.com/unicode/language-identifiers.html")
			}) {
				Text(stringResource(R.string.date_editor_view_language_tags))
			}
		}

		OutlinedTextField(
			value = language,
			onValueChange = onLanguageChange,
			supportingText =
				if (invalidLanguage) {
					{ Text(stringResource(R.string.date_editor_language_error)) }
				} else {
					null
				},
			isError = invalidLanguage,
			singleLine = true,
			modifier = Modifier.fillMaxWidth(),
		)
	}
}
