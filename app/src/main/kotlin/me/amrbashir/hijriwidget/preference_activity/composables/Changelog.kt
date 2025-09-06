package me.amrbashir.hijriwidget.preference_activity.composables

import android.text.util.Linkify
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import me.amrbashir.hijriwidget.CHANGELOG
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceTemplate

@Composable
fun Changelog() {
    PreferenceGroup(
        label = "Changelog"
    ) {
        for ((index, entry) in CHANGELOG.withIndex()) {
            // expand latest release by default (latest == first in list)
            var expanded by remember { mutableStateOf(index == 0) }
            val arrowRotation by animateFloatAsState(
                targetValue = if (expanded) 180f else 0f,
                label = "accordion-arrow"
            )

            PreferenceTemplate(
                entry.header,
                endContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_keyboard_arrow_down_24),
                        contentDescription = null,
                        modifier = Modifier.rotate(arrowRotation)
                    )
                },
                onClick = { expanded = !expanded },
            ) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                    ),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                    )
                ) {
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
    }
}
