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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.DATE_FORMAT_PRESETES
import me.amrbashir.hijriwidget.HijriDate
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
            PreferenceGroup(label = "Format") {
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
                    label = "Custom",
                    description = "Specify custom date format pattern (e.g., 'dd/MM/yyyy', 'EEEE, MMMM d')",
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
            """
            Customize how the date appears on your widget
            <br>
            <br>
            You can use these codes:
            <ul>
                <li><b>d</b> = day number (1, 2, 3...)</li>
                <li><b>dd</b> = day with zero (01, 02, 03...)</li>
                <li><b>M</b> = month number (1, 2, 3...)</li>
                <li><b>MM</b> = month with zero (01, 02, 03...)</li>
                <li><b>MMM</b> = short month name (Muh, Saf, Rab...)</li>
                <li><b>MMMM</b> = full month name (Muharram, Safar...)</li>
                <li><b>yy</b> = short year (47, 48...)</li>
                <li><b>yyyy</b> = full year (1447, 1448...)</li>
                <li><b>EE</b> = short week day (Thu, Fri...)</li>
                <li><b>EEEE</b> = full week day (Thursday, Friday...)</li>
            </ul>
            <br>
            Examples:
            <ul>
                <li><b>dd/MM/yyyy</b> = 21/02/1447</li>
                <li><b>MMMM d, yyyy</b> = Safar 21, 1447</li>
                <li><b>EE, MMMM d, yyyy</b> = Fri, Safar 21, 1447</li>
                <li><b>d-M-yy</b> = 21-2-47</li>
            </ul>
            <br>
            Mix and match these codes with slashes, dashes, spaces, or commas to create your preferred date style.
            <br>
            <br>
            By default the language used for displaying these codes, is ar-SA but you can specify a different language by using this syntax:
            <br>
            <br>
                 ${"\u2000\u2000\u2000\u2000"}<big>&lt;language-code&gt;{date-code}</big>
            <br>
            <br>
            Examples:
            <ul>
                <li><b>en-GB{dd/MMMM/yyyy}</b>= 21/Safar/1447</li>
                <li><b>ar-SA{dd MMMM yyyy}</b>= ${"\u200F"}۲۱ صفر ۱٤٤۷</li>
                <li><b>hi-IN{dd MMMM yyyy}</b>= 06 सफर 1447</li>
                <li><b>tr-TR{dd MMMM yyyy}</b>= 06 Rebiülevvel 1447</li>
            </ul>
            <br>
            You can also mix and match multiple languages:
            <ul>
            <li><b>en-GB{dd}/ar-SA{MMMM}/en-GB{yyyy}</b> = 21/صفر${"\u200E"}/1447</li>
            <li><b>en-GB{dd} ar-SA{MMMM} en-GB{yyyy}</b> = 21 صفر${"\u200E"} 1447</li>
            </ul>
            <br>
            For the full list of language identifiers:
            <br>
            <a href="$languageIDsURL">$languageIDsURL</a>
            """.trimIndent(),
            linkStyles,
        ),
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyMedium.merge(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = 0.7F
            )
        ),
    )
}
