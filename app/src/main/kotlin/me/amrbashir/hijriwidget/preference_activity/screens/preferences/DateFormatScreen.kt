package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.DATE_FORMAT_PRESETES
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget
import me.amrbashir.hijriwidget.preference_activity.animatedContentComposable
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.VisualDateFormatEditor
import me.amrbashir.hijriwidget.preference_activity.composables.parseDateFormatRows
import me.amrbashir.hijriwidget.preference_activity.composables.ui.Item
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ItemGroup
import me.amrbashir.hijriwidget.validateDatePatternStructure

const val DATE_FORMAT_DESTINATION = "/preferences/date-format"

fun NavGraphBuilder.dateFormatDestination() {
	animatedContentComposable(route = DATE_FORMAT_DESTINATION) { DateFormatScreen() }
}

fun NavController.navigateToDateFormat() {
	navigate(route = DATE_FORMAT_DESTINATION)
}

@Composable
internal fun DateFormatScreen() {
	val prefsManager = LocalSelectedWidget.current.prefsManager

	val savedFormat = prefsManager.dateFormat.value
	var editingPattern by rememberSaveable { mutableStateOf(false) }

	val customPattern = prefsManager.dateCustomFormat.value
	val patternError = remember(customPattern) { validateDatePatternStructure(customPattern) }
	val parsedRows = remember(customPattern) { parseDateFormatRows(customPattern) }
	val invalidPattern = patternError != null
	val showPattern = editingPattern

	PreferenceScreenLayout {
		Column(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier =
				Modifier
					.fillMaxSize()
					.imePadding()
					.verticalScroll(rememberScrollState()),
		) {
			ItemGroup(label = stringResource(R.string.date_format_group_title)) {
				for (format in DATE_FORMAT_PRESETES) {
					val selected = !prefsManager.dateIsCustomFormat.value && savedFormat == format
					FormatOption(
						label = HijriDate.todayFormatted(prefsManager, format),
						description = null,
						selected = selected,
						modifier = Modifier.fillMaxWidth(),
						onClick = {
							prefsManager.dateIsCustomFormat.value = false
							prefsManager.dateFormat.value = format
						},
					)
				}
			}

			Card(
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(20.dp),
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright),
			) {
				FormatOption(
					label = stringResource(R.string.date_format_custom),
					description = stringResource(R.string.date_format_custom_description),
					selected = prefsManager.dateIsCustomFormat.value,
					modifier = Modifier.fillMaxWidth(),
					onClick = {
						if (!prefsManager.dateIsCustomFormat.value &&
							prefsManager.dateCustomFormat.value.isEmpty()
						) {
							prefsManager.dateCustomFormat.value = savedFormat
						}
						prefsManager.dateIsCustomFormat.value = true
					},
				)

				if (prefsManager.dateIsCustomFormat.value) {
					Column(
						verticalArrangement = Arrangement.spacedBy(16.dp),
						modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
					) {
						SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
							listOf(R.string.date_format_visual, R.string.date_format_pattern).forEachIndexed {
								index,
								label,
								->
								SegmentedButton(
									selected = showPattern == (index == 1),
									onClick = { editingPattern = index == 1 },
									shape = SegmentedButtonDefaults.itemShape(index = index, count = 2),
								) {
									Text(stringResource(label))
								}
							}
						}

						if (showPattern) {
							OutlinedTextField(
								modifier = Modifier.fillMaxWidth(),
								value = customPattern,
								isError = invalidPattern,
								supportingText = {
									if (patternError != null) Text(stringResource(patternError.messageRes))
								},
								onValueChange = { prefsManager.dateCustomFormat.value = it },
							)
							DateFormatGuideText()
						} else if (parsedRows != null) {
							VisualDateFormatEditor(
								initialRows = parsedRows,
								onPatternChange = { prefsManager.dateCustomFormat.value = it },
							)
						} else {
							InvalidDatePatternState(
								onEditPattern = { editingPattern = true },
								onStartFresh = { prefsManager.dateCustomFormat.value = "" },
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun InvalidDatePatternState(
	onEditPattern: () -> Unit,
	onStartFresh: () -> Unit,
) {
	Column(
		modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Icon(
			Icons.Outlined.ErrorOutline,
			contentDescription = null,
			modifier = Modifier.size(32.dp),
			tint = MaterialTheme.colorScheme.error,
		)
		Text(
			stringResource(R.string.date_editor_invalid_pattern_title),
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.error,
		)
		Text(
			text =
				AnnotatedString.fromHtml(
					htmlString = stringResource(R.string.date_editor_pattern_needs_editing),
					linkStyles = TextLinkStyles(SpanStyle(color = MaterialTheme.colorScheme.primary)),
					linkInteractionListener = { link ->
						when ((link as? LinkAnnotation.Url)?.url) {
							"pattern" -> onEditPattern()
							"fresh" -> onStartFresh()
						}
					},
				),
			textAlign = TextAlign.Center,
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
private fun FormatOption(
	label: String,
	description: String?,
	selected: Boolean,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	Item(
		verticalAlignment = Alignment.CenterVertically,
		label = label,
		description = description,
		modifier = modifier.selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
		icon = {
			RadioButton(
				selected = selected,
				onClick = null,
				modifier = Modifier.size(20.dp),
			)
		},
	)
}

@Composable
private fun DateFormatGuideText() {
	val languageIDsURL = "http://www.i18nguy.com/unicode/language-identifiers.html"

	val linkStyles =
		TextLinkStyles(
			style = SpanStyle(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7F)),
		)

	Text(
		AnnotatedString.fromHtml(
			stringResource(id = R.string.date_format_guide, languageIDsURL),
			linkStyles,
		),
		modifier = Modifier.fillMaxWidth(),
		style =
			MaterialTheme.typography.bodyMedium.merge(
				color =
					MaterialTheme.colorScheme.onSurfaceVariant.copy(
						alpha = 0.7F,
					),
				textDirection = TextDirection.ContentOrLtr,
			),
	)
}
