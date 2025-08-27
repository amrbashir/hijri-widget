package me.amrbashir.hijriwidget.preferences.routes

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.serialization.Serializable
import me.amrbashir.hijriwidget.BuildConfig
import me.amrbashir.hijriwidget.CHANGELOG
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preferences.LocalNavController
import me.amrbashir.hijriwidget.preferences.composables.ui.adaptiveIconPainterResource

@Serializable
object AboutRoute

fun NavGraphBuilder.aboutRoute() {
    composable<AboutRoute> { Route() }
}

fun NavController.navigateToAbout() {
    navigate(route = AboutRoute)
}


data class QuickLink(
    val label: String,
    val icon: Int,
    val url: String,
)

val QUIKC_LINKS = arrayOf(
    QuickLink("GitHub", R.drawable.ic_fab_github, "https://github.com/amrbashir/hijri-widget"),
    QuickLink("Twitter", R.drawable.ic_fab_twitter, "https://twitter.com/amrbashir_dev"),
    QuickLink("LinkedIn", R.drawable.ic_fab_linkedin, "https://www.linkedin.com/in/amrbashir-dev"),
    QuickLink(
        "Privacy Policy",
        R.drawable.baseline_privacy_tip_24,
        "https://hijri-widget.amrbashir.me/PRIVACY.md"
    ),
)

@Composable
private fun Route() {
    val navController = LocalNavController.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Image(
            painter = adaptiveIconPainterResource(R.mipmap.ic_launcher),
            contentDescription = null,
            modifier = Modifier.requiredSize(64.dp)
        )

        Text(
            "Hijri Widget",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            "${BuildConfig.VERSION_NAME} (12dqwe2)",
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceBright)
                .padding(8.dp)
        ) {
            for (link in QUIKC_LINKS) {
                TextButton(
                    colors = ButtonDefaults.textButtonColors()
                        .copy(contentColor = MaterialTheme.colorScheme.onSurface),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        navController.context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                link.url.toUri()
                            )
                        )
                    },
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            modifier = Modifier.requiredSize(24.dp),
                            painter = painterResource(link.icon),
                            contentDescription = null,
                        )
                        Text(link.label)
                    }
                }
            }
        }

        MarkdownText(
            CHANGELOG.replace("## [Unreleased]", ""),
            syntaxHighlightColor = MaterialTheme.colorScheme.surfaceBright
        )
    }
}
