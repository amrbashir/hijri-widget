package me.amrbashir.hijriwidget.preference_activity.composables

import android.text.util.Linkify
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import dev.jeziellago.compose.markdowntext.MarkdownText
import me.amrbashir.hijriwidget.CHANGELOG
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.composables.ui.CollapsibleButton
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup

@Composable
fun Changelog() {
    PreferenceGroup(
        label = stringResource(R.string.changelog)
    ) {
        for ((index, entry) in CHANGELOG.withIndex()) {
            CollapsibleButton(
                header = entry.header,
                collapsed = index != 0 /* expand first by default */
            ) {
                MarkdownText(
                    markdown = entry.content,
                    modifier = Modifier.fillMaxWidth(),
                    syntaxHighlightColor = MaterialTheme.colorScheme.surfaceBright,
                    linkifyMask = Linkify.WEB_URLS
                )
            }
        }


    }

    val linkStyles = TextLinkStyles(
        style = SpanStyle(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7F))
    )
    val changelogUrl = "https://github.com/amrbashir/hijri-widget/blob/master/CHANGELOG.md"

    Text(
        AnnotatedString.fromHtml(
            stringResource(R.string.checkout_the_full_changelog, changelogUrl),
            linkStyles,
        ),
        style = MaterialTheme.typography.bodyMedium.merge(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                alpha = 0.7F
            )
        ),
    )
}
