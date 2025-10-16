package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import me.amrbashir.hijriwidget.formatHijriDate
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager
import me.amrbashir.hijriwidget.preference_activity.composableWithAnimatedContentScopeProvider
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceTemplate
import me.amrbashir.hijriwidget.preference_activity.composables.ui.RadioIcon

const val DATE_FORMAT_DESTINATION = "/preferences/date-format"

fun NavGraphBuilder.dateFormatDestination() {
    composableWithAnimatedContentScopeProvider(route = DATE_FORMAT_DESTINATION) { DateFormatScreen() }
}

fun NavController.navigateToDateFormat() {
    navigate(route = DATE_FORMAT_DESTINATION)
}

@Composable
internal fun DateFormatScreen() {
    val prefsManager = LocalPreferencesManager.current

    val savedFormat = prefsManager.dateFormat.value

    PreferenceScreenLayout {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            PreferenceGroup(label = stringResource(R.string.date_format_group_title)) {
                for (format in DATE_FORMAT_PRESETES) {
                    PreferenceTemplate(
                        label = format.formatHijriDate(
                            HijriDate.today(prefsManager),
                            prefsManager.calendarCalculationMethod.value
                        ),
                        description = format,
                        icon = { RadioIcon(selected = !prefsManager.dateIsCustomFormat.value && savedFormat == format) },
                        onClick = {
                            prefsManager.dateIsCustomFormat.value = false
                            prefsManager.dateFormat.value = format
                        }
                    )
                }

                PreferenceTemplate(
                    label = stringResource(R.string.date_format_custom),
                    description = stringResource(R.string.date_format_custom_description),
                    icon = { RadioIcon(selected = prefsManager.dateIsCustomFormat.value) },
                    onClick = {
                        prefsManager.dateIsCustomFormat.value = true
                    }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(start = 48.dp)
                            .fillMaxWidth(),
                        enabled = prefsManager.dateIsCustomFormat.value,
                        value = prefsManager.dateCustomFormat.value,
                        onValueChange = {
                            prefsManager.dateCustomFormat.value = it
                        }
                    )
                }
            }

            if (prefsManager.dateIsCustomFormat.value) {
                DateFormatGuideText()
            }
        }
    }
}

@Composable
private fun DateFormatGuideText() {
    val languageIDsURL = "http://www.i18nguy.com/unicode/language-identifiers.html"

    val linkStyles = TextLinkStyles(
        style = SpanStyle(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7F))
    )

    Text(
        AnnotatedString.fromHtml(
            stringResource(id = R.string.date_format_guide, languageIDsURL),
            linkStyles,
        ),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium.merge(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = 0.7F
            ),
            textDirection = TextDirection.ContentOrLtr,
        ),
    )
}
