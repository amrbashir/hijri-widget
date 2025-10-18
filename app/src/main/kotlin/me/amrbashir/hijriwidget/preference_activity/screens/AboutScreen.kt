package me.amrbashir.hijriwidget.preference_activity.screens

import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.amrbashir.hijriwidget.BuildConfig
import me.amrbashir.hijriwidget.CONTRIBUTORS
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.composables.Changelog
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceTemplate
import me.amrbashir.hijriwidget.preference_activity.composables.ui.adaptiveIconPainterResource

const val ABOUT_DESTINATION = "/About"

fun NavGraphBuilder.aboutDestination() {
    composable(route = ABOUT_DESTINATION) { AboutScreen() }
}

fun NavController.navigateToAbout() {
    navigate(route = ABOUT_DESTINATION)
}


data class QuickLink(
    val label: Int,
    @param:DrawableRes val iconResId: Int? = null,
    val icon: ImageVector? = null,
    val url: String,
)

val QUICK_LINKS = arrayOf(
    QuickLink(
        label = R.string.quick_link_github,
        iconResId = R.drawable.ic_fab_github,
        url = "https://github.com/amrbashir/hijri-widget"
    ),
    QuickLink(
        label = R.string.quick_link_twitter,
        iconResId = R.drawable.ic_fab_twitter,
        url = "https://twitter.com/amrbashir_dev"
    ),
    QuickLink(
        label = R.string.quick_link_linkedin,
        iconResId = R.drawable.ic_fab_linkedin,
        url = "https://www.linkedin.com/in/amrbashir-dev"
    ),
    QuickLink(
        label = R.string.quick_link_privacy,
        icon = Icons.Filled.PrivacyTip,
        url = "https://hijri-widget.amrbashir.me/PRIVACY.md"
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutScreen() {
    LocalAppBarTitle.current.value = stringResource(R.string.about_screen_title)

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {

        AppInfo()

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            for (link in QUICK_LINKS) {
                QuickLinkButton(link)
            }
        }

        Contributors()

        Changelog()
    }
}

@Composable
private fun AppInfo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = adaptiveIconPainterResource(R.mipmap.ic_launcher),
            contentDescription = null,
            modifier = Modifier.requiredSize(64.dp)
        )

        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            "${BuildConfig.VERSION_NAME} (${BuildConfig.GIT_SHA})",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun RowScope.QuickLinkButton(link: QuickLink) {
    val context = LocalContext.current

    val openLink = {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                link.url.toUri()
            )
        )
    }

    TextButton(
        modifier = Modifier.weight(1F),
        colors = ButtonDefaults.textButtonColors()
            .copy(contentColor = MaterialTheme.colorScheme.onSurface),
        shape = RoundedCornerShape(12.dp),
        onClick = openLink,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
    }

    link.icon?.let {
        Icon(
            imageVector = link.icon,
            contentDescription = null,
            modifier = modifier
        )
    }
}

@Composable
private fun Contributors() {
    val context = LocalContext.current

    val openLink = { url: String ->
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                url.toUri()
            )
        )
    }

    PreferenceGroup(stringResource(R.string.top_contributors)) {
        for (contributor in CONTRIBUTORS) {
            PreferenceTemplate(
                label = contributor.username,
                description = stringResource(R.string.contributions, contributor.contributions),
                onClick = { openLink(contributor.url) },
                icon = {
                    Image(
                        modifier = Modifier.clip(RoundedCornerShape(100)),
                        painter = painterResource(contributor.avatar),
                        contentDescription = null,
                    )
                }
            )
        }

        PreferenceTemplate(
            label = stringResource(R.string.checkout_the_full_list_of_contributors),
            onClick = { openLink("https://github.com/amrbashir/hijri-widget/graphs/contributors") },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.People,
                    contentDescription = null,
                )
            }
        )
    }
}
