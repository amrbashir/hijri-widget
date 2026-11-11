package me.amrbashir.hijriwidget.preference_activity.composables

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import me.amrbashir.hijriwidget.CONTRIBUTORS
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.composables.ui.Item
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ItemGroup

@Composable
internal fun Contributors() {
	val context = LocalContext.current
	val author = CONTRIBUTORS.first { it.username == "amrbashir" }
	val contributors = CONTRIBUTORS.filterNot { it.username == author.username }

	ItemGroup(label = stringResource(R.string.contributors)) {
		Item(
			label = author.username,
			description =
				stringResource(
					R.string.project_author_contributions,
					author.contributions,
				),
			onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, author.url.toUri())) },
			iconSize = 64.dp,
			verticalAlignment = Alignment.CenterVertically,
			icon = {
				Image(
					painter = painterResource(author.avatar),
					contentDescription = null,
					modifier = Modifier.size(64.dp).clip(CircleShape),
				)
			},
		)

		Item(
			label = stringResource(R.string.meet_contributors),
			description =
				stringResource(
					R.string.contributor_totals,
					contributors.size,
					contributors
						.sumOf {
							it.contributions
						},
				),
			onClick = {
				context.startActivity(
					Intent(
						Intent.ACTION_VIEW,
						"https://github.com/amrbashir/hijri-widget/graphs/contributors".toUri(),
					),
				)
			},
			iconSize = 64.dp,
			verticalAlignment = Alignment.CenterVertically,
			icon = {
				Box(contentAlignment = Alignment.CenterStart) {
					contributors.forEachIndexed { index, contributor ->
						Image(
							painter = painterResource(contributor.avatar),
							contentDescription = contributor.username,
							modifier =
								Modifier
									.zIndex(contributor.contributions.toFloat())
									.padding(
										start = (index * 16).dp,
									).size(32.dp)
									.clip(CircleShape),
						)
					}
				}
			},
			endContent = {
				Icon(
					imageVector = Icons.Outlined.ArrowUpward,
					contentDescription = null,
					modifier = Modifier.rotate(45F),
				)
			},
		)
	}
}
