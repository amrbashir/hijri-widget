package me.amrbashir.hijriwidget.preference_activity.composables

import android.content.Intent
import android.text.util.Linkify
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.jeziellago.compose.markdowntext.MarkdownText
import me.amrbashir.hijriwidget.CHANGELOG
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.composables.ui.CollapsibleButton
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ItemGroup

@Composable
internal fun Changelog() {
	val entries = CHANGELOG.filter { it.content.isNotBlank() }.take(3)

	ItemGroup(
		label = stringResource(R.string.changelog),
		headerAction = { FullChangelogButton() },
	) {
		entries.forEachIndexed { index, entry ->
			CollapsibleButton(
				header = entry.header,
				collapsed = index != 0,
				endContent = {
					if (index == 0) {
						LatestReleaseBadge()
					}
				},
			) {
				MarkdownText(
					markdown = entry.content,
					modifier = Modifier.fillMaxWidth(),
					syntaxHighlightColor = MaterialTheme.colorScheme.surfaceBright,
					linkifyMask = Linkify.WEB_URLS,
				)
			}
		}
	}
}

@Composable
private fun LatestReleaseBadge() {
	Surface(
		shape = RoundedCornerShape(10.dp),
		color = MaterialTheme.colorScheme.surfaceContainer,
		contentColor = MaterialTheme.colorScheme.primary,
	) {
		Text(
			stringResource(R.string.latest_release),
			modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
			style = MaterialTheme.typography.labelSmall,
		)
	}
}

@Composable
private fun FullChangelogButton() {
	val context = LocalContext.current

	CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
		TextButton(
			modifier = Modifier.height(20.dp),
			contentPadding = PaddingValues(0.dp),
			onClick = {
				context.startActivity(
					Intent(
						Intent.ACTION_VIEW,
						"https://github.com/amrbashir/hijri-widget/blob/master/CHANGELOG.md".toUri(),
					),
				)
			},
		) {
			Text(
				stringResource(R.string.full_changelog) + " ↗",
				style = MaterialTheme.typography.labelMedium,
			)
		}
	}
}
