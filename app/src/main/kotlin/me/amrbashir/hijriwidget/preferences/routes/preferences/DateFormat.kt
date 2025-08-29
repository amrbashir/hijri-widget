package me.amrbashir.hijriwidget.preferences.routes.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import me.amrbashir.hijriwidget.DATE_FORMAT_PRESETES
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.formatDate
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferenceCategory
import me.amrbashir.hijriwidget.preferences.composables.ui.PreferencesGroup
import me.amrbashir.hijriwidget.preferences.composables.ui.RadioIcon


@Serializable
object DateFormatRoute

fun NavGraphBuilder.dateFormatRoute() {
    composable<DateFormatRoute> { Route() }
}

fun NavController.navigateToDateFormat() {
    navigate(route = DateFormatRoute)
}


@Composable
private fun Route() {
    val savedFormat = Preferences.dateFormat.value

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PreferencesGroup(label = "Format") {
            for (format in DATE_FORMAT_PRESETES) {
                PreferenceCategory(
                    label = format.formatDate(HijriDate.today()),
                    description = format,
                    icon = { RadioIcon(selected = !Preferences.dateIsCustomFormat.value && savedFormat == format) },
                    onClick = {
                        Preferences.dateIsCustomFormat.value = false
                        Preferences.dateFormat.value = format
                    }
                )
            }

            PreferenceCategory(
                label = "Custom",
                description = "Specify custom date format pattern (e.g., 'dd/MM/yyyy', 'EEEE, MMMM d')",
                icon = { RadioIcon(selected = Preferences.dateIsCustomFormat.value) },
                onClick = {
                    Preferences.dateIsCustomFormat.value = true
                }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 48.dp)
                        .fillMaxWidth(),
                    enabled = Preferences.dateIsCustomFormat.value,
                    value = Preferences.dateCustomFormat.value,
                    onValueChange = {
                        Preferences.dateCustomFormat.value = it
                    }
                )
            }
        }

        if (Preferences.dateIsCustomFormat.value) {
            Spacer(Modifier.requiredHeight(16.dp))

            val annotatedString = buildAnnotatedString {
                append(
                    """
                    Customize how the date appears on your widget

                    You can use these codes:
                    • d = day number (1, 2, 3...)
                    • dd = day with zero (01, 02, 03...)
                    • M = month number (1, 2, 3...)
                    • MM = month with zero (01, 02, 03...)
                    • MMM = short month name (Muh, Saf, Rab...)
                    • MMMM = full month name (Muharram, Safar...)
                    • yy = short year (47, 48...)
                    • yyyy = full year (1447, 1448...)
                    • EE = short week day (Thu, Fri...)
                    • EEEE = full week day (Thursday, Friday...)

                    Examples:
                    • dd/MM/yyyy = 21/02/1447
                    • MMMM d, yyyy = Safar 21, 1447
                    • EE, MMMM d, yyyy = Fri, Safar 21, 1447
                    • d-M-yy = 21-2-47

                    Mix and match these codes with slashes, dashes, spaces, or commas to create your preferred date style.

                    By default the language used for displaying these codes, is ar-SA but you can specify a different language by using this syntax:

                        <language-code>{<date-codes>}

                    Examples:
                    • en-GB{dd/MMMM/yyyy} = 21/Safar/1447
                    • ar-SA{dd MMMM yyyy} = ${"\u200F"}۲۱ صفر ۱٤٤۷
                    • hi-IN{dd MMMM yyyy} = 06 सफर 1447
                    • tr-TR{dd MMMM yyyy} = 06 Rebiülevvel 1447

                    You can also mix and match multiple languages:
                    • en-GB{dd}/ar-SA{MMMM}/en-GB{yyyy} = 21/صفر${"\u200E"}/1447
                    • en-GB{dd} ar-SA{MMMM} en-GB{yyyy} = 21 صفر${"\u200E"} 1447

                    For the full list of language identifiers:
                """.trimIndent()
                )
                withLink(
                    LinkAnnotation.Url(
                        "http://www.i18nguy.com/unicode/language-identifiers.html",
                        TextLinkStyles(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.7F
                                )
                            )
                        )
                    )
                ) {
                    append("http://www.i18nguy.com/unicode/language-identifiers.html")
                }
            }

            Text(
                annotatedString,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium.merge(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.7F
                    )
                ),
            )
        }

    }
}
