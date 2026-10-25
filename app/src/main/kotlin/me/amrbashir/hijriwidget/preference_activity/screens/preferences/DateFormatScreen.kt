package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
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
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ItemGroup

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
				for (formats in DATE_FORMAT_PRESETES.chunked(2)) {
					Row(
						modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
						horizontalArrangement = Arrangement.spacedBy(2.dp),
					) {
						for (format in formats) {
							FormatOption(
								label = HijriDate.todayFormatted(prefsManager, format),
								description = format,
								selected =
									!prefsManager.dateIsCustomFormat.value && savedFormat == format,
								modifier = Modifier.weight(1F).fillMaxHeight(),
								onClick = {
									prefsManager.dateIsCustomFormat.value = false
									prefsManager.dateFormat.value = format
								},
							)
						}
					}
				}
			}

			Column(
				modifier =
					Modifier
						.fillMaxWidth()
						.clip(RoundedCornerShape(20.dp))
						.background(MaterialTheme.colorScheme.surfaceBright),
			) {
				FormatOption(
					label = stringResource(R.string.date_format_custom),
					description = stringResource(R.string.date_format_custom_description),
					selected = prefsManager.dateIsCustomFormat.value,
					modifier = Modifier.fillMaxWidth(),
					onClick = { prefsManager.dateIsCustomFormat.value = true },
				)

				if (prefsManager.dateIsCustomFormat.value) {
					Column(
						verticalArrangement = Arrangement.spacedBy(16.dp),
						modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
					) {
						OutlinedTextField(
							modifier = Modifier.fillMaxWidth(),
							value = prefsManager.dateCustomFormat.value,
							onValueChange = { prefsManager.dateCustomFormat.value = it },
						)
						DateFormatGuideText()
					}
				}
			}
		}
	}
}

@Composable
private fun FormatOption(
	label: String,
	description: String,
	selected: Boolean,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		modifier =
			modifier
				.background(MaterialTheme.colorScheme.surfaceBright)
				.selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
				.padding(16.dp),
	) {
		RadioButton(
			selected = selected,
			onClick = null,
			modifier = Modifier.padding(top = 2.dp).size(20.dp),
		)
		Column(
			modifier = Modifier.weight(1F),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			Text(
				text = label,
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onBackground,
			)
			Text(
				text = description,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
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
