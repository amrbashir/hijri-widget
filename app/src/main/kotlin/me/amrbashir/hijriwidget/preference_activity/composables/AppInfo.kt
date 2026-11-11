package me.amrbashir.hijriwidget.preference_activity.composables

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import me.amrbashir.hijriwidget.BuildConfig
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.composables.ui.Item
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ItemGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.adaptiveIconPainterResource

private data class QuickLink(
	val label: Int,
	@param:DrawableRes val iconResId: Int? = null,
	val icon: ImageVector? = null,
	val url: String,
)

private val QUICK_LINKS =
	listOf(
		QuickLink(
			label = R.string.quick_link_github,
			iconResId = R.drawable.ic_fab_github,
			url = "https://github.com/amrbashir/hijri-widget",
		),
		QuickLink(
			label = R.string.quick_link_twitter,
			iconResId = R.drawable.ic_fab_twitter,
			url = "https://twitter.com/amrbashir_dev",
		),
		QuickLink(
			label = R.string.quick_link_linkedin,
			iconResId = R.drawable.ic_fab_linkedin,
			url = "https://www.linkedin.com/in/amrbashir-dev",
		),
		QuickLink(
			label = R.string.quick_link_privacy,
			icon = Icons.Filled.PrivacyTip,
			url = "https://hijri-widget.amrbashir.me/PRIVACY.md",
		),
	)

@Composable
internal fun AppInfo() {
	ItemGroup(label = stringResource(R.string.about_screen_title)) {
		Item(
			label = stringResource(R.string.app_name),
			description = "${BuildConfig.VERSION_NAME} (${BuildConfig.GIT_SHA})",
			verticalAlignment = Alignment.CenterVertically,
			iconSize = 64.dp,
			icon = {
				Image(
					painter = adaptiveIconPainterResource(R.mipmap.ic_launcher),
					contentDescription = null,
					modifier = Modifier.size(64.dp),
				)
			},
		)

		Item {
			Row(
				horizontalArrangement = Arrangement.SpaceEvenly,
				modifier = Modifier.fillMaxWidth(),
			) {
				for (link in QUICK_LINKS) QuickLinkButton(link)
			}
		}
	}
}

@Composable
private fun RowScope.QuickLinkButton(link: QuickLink) {
	val context = LocalContext.current
	val description = stringResource(link.label)

	val openLink = {
		context.startActivity(
			Intent(
				Intent.ACTION_VIEW,
				link.url.toUri(),
			),
		)
	}

	TextButton(
		modifier = Modifier.weight(1F).semantics { contentDescription = description },
		colors =
			ButtonDefaults
				.textButtonColors()
				.copy(contentColor = MaterialTheme.colorScheme.onSurface),
		shape = RoundedCornerShape(12.dp),
		onClick = openLink,
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			QuickLinkButtonIcon(link)
			Text(stringResource(link.label))
		}
	}
}

@Composable
private fun QuickLinkButtonIcon(link: QuickLink) {
	val modifier = Modifier.size(24.dp)

	link.iconResId?.let {
		Image(
			painter = painterResource(link.iconResId),
			contentDescription = null,
			modifier = modifier,
			colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
		)
	}

	link.icon?.let {
		Icon(
			imageVector = link.icon,
			contentDescription = null,
			modifier = modifier,
		)
	}
}
