package me.amrbashir.hijriwidget.settings.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun SettingsItem(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(enabled, onClick = onClick)
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(modifier = Modifier.padding(start = 16.dp)) {
                icon()
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                title()
                if (summary != null) {
                    summary()
                }
            }
        }
    }
}

fun LazyListScope.settingsItem(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    summary: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {

    item {
        me.amrbashir.hijriwidget.settings.composables.SettingsItem(
            title,
            modifier,
            enabled,
            icon,
            summary,
            onClick
        )
    }

}