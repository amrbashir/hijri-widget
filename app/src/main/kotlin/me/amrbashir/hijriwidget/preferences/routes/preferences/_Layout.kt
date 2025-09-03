package me.amrbashir.hijriwidget.preferences.routes.preferences

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.preferences.LocalAnimatedContentScope
import me.amrbashir.hijriwidget.preferences.LocalSharedTransitionScope
import me.amrbashir.hijriwidget.preferences.composables.WidgetPreview


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PreferenceRouteLayout(
    content: @Composable () -> Unit
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val sharedAnimatedContentScope = LocalAnimatedContentScope.current

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        with(sharedTransitionScope) {
            WidgetPreview(
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "WidgetPreview"),
                        animatedVisibilityScope = sharedAnimatedContentScope,
                    )
            )
        }

        content()
    }
}
