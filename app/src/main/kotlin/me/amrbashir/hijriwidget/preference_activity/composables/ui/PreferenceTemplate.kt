package me.amrbashir.hijriwidget.preference_activity.composables.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.addIf


@Composable
fun PreferenceTemplate(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    PreferenceTemplate(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = iconModifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        label = label,
        modifier = modifier,
        enabled = enabled,
        description = description,
        onClick = onClick,
        endContent = endContent,
        content = content,
    )
}

@Composable
fun PreferenceTemplate(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .addIf(onClick != null) { clickable(enabled, onClick = onClick!!) }
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceBright)
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            icon?.let {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(32.dp),
                ) {
                    icon()
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1F)
                ) {
                    Text(
                        label,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    description?.let {
                        Text(
                            description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                if (endContent != null) endContent()
            }
        }

        if (content != null) content()
    }
}
