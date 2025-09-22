package me.amrbashir.hijriwidget.preference_activity.composables

import CollapsibleButton
import android.text.util.Linkify
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.jeziellago.compose.markdowntext.MarkdownText
import me.amrbashir.hijriwidget.CHANGELOG
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup

@Composable
fun Changelog() {
    PreferenceGroup(
        label = "Changelog"
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
