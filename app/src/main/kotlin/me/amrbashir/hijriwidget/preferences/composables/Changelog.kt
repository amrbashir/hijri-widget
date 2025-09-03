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

@Composable
fun Changelog() {
    Text(
        "Changelog",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineLarge
    )

    for (entry in CHANGELOG) {
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
