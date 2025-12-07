package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.addIf
import me.amrbashir.hijriwidget.preference_activity.screenPadding


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PreferenceScreenLayout(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.screenPadding().addIf(onClick != null) {
            clickable(onClick = onClick!!)
        }
    ) {
        SharedWidgetPreview()
        content()
    }
}
