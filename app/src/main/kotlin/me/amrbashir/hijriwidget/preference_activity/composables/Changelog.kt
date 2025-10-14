package me.amrbashir.hijriwidget.preference_activity.composables

import android.text.util.Linkify
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
}
