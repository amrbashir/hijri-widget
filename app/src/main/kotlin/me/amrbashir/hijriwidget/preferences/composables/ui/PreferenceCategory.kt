package me.amrbashir.hijriwidget.preferences.composables.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.addIf


@Composable
fun PreferenceCategory(
    label: String,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    @DrawableRes iconResId: Int? = null,
    icon: (@Composable () -> Unit)? = null,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .addIf(onClick != null) { clickable(enabled, onClick = onClick!!) }
            .background(MaterialTheme.colorScheme.surfaceBright)
            .padding(16.dp)
    ) {
        Row {
            if (iconResId != null || icon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(32.dp),
                ) {
                    iconResId?.let {
                        Icon(
                            imageVector = ImageVector.vectorResource(iconResId),
                            contentDescription = null,
                            modifier = iconModifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    icon?.let {
                        icon()
                    }

                }

                Spacer(modifier = Modifier.requiredWidth(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                        LocalTextStyle provides MaterialTheme.typography.bodyLarge,
                    ) {
                        TextAnyRtl(label)
                    }


                    description?.let {
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
                            LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                        ) {
                            Text(description)
                        }
                    }
                }

                if (endContent != null) {
                    endContent()
                }
            }
        }

        content?.invoke()
    }
}
