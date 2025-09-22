package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.preference_activity.LocalAnimatedContentScope
import me.amrbashir.hijriwidget.preference_activity.LocalSharedTransitionScope


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PreferenceScreenLayout(
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SharedWidgetPreview()
        content()
    }
}

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
private fun SharedWidgetPreview() {
    val key = "SharedWidgetPreview"
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val sharedAnimatedContentScope = LocalAnimatedContentScope.current

    with(receiver = sharedTransitionScope) {
        WidgetPreview(
            modifier = Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key),
                animatedVisibilityScope = sharedAnimatedContentScope
            )
        )
    }
}
