package me.amrbashir.hijriwidget.preferences.composables

import android.text.util.Linkify
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import me.amrbashir.hijriwidget.CHANGELOG

data class ChangelogEntry(
    val header: String,
    val content: String
)

/** Pattern to find `## [1.0.1] - 2025-08-29` */
val VERSION_PATTERN = Regex("""## \[(\d+\.\d+\.\d+)] - (\d{4}-\d{2}-\d{2})""")

@Composable
fun Changelog() {
    val matches = VERSION_PATTERN.findAll(CHANGELOG).toList()

    val entries = mutableListOf<ChangelogEntry>()

    // Handle optional "Unreleased" section
    if (matches.isNotEmpty() && matches.first().range.first > 0) {
        val unreleasedContent = CHANGELOG.substring(0, matches.first().range.first).trim()
        if (unreleasedContent.isNotEmpty()) {
            entries.add(ChangelogEntry("Unreleased", unreleasedContent))
        }
    }

    // Handle versioned sections
    matches.forEachIndexed { index, match ->
        val version = match.groupValues[1]
        val date = match.groupValues[2]
        val start = match.range.last + 1
        val end =
            if (index + 1 < matches.size) matches[index + 1].range.first else CHANGELOG.length
        val content = CHANGELOG.substring(start, end).trim()
        entries.add(ChangelogEntry("$version - $date", content))
    }

    Text(
        "Changelog",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineLarge
    )

    for (entry in entries) {
        Text(
            entry.header,
            style = MaterialTheme.typography.labelLarge
        )
        Card {
            MarkdownText(
                entry.content,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                syntaxHighlightColor = MaterialTheme.colorScheme.surfaceBright,
                linkifyMask = Linkify.WEB_URLS
            )
        }
    }
}
