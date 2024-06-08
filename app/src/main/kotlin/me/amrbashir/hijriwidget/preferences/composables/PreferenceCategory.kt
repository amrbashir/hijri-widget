package me.amrbashir.hijriwidget.preferences.composables

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.addIf


@Composable
fun PreferenceCategory(
    label: String,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    alternateIcon:  (@Composable () -> Unit)? = null,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null,
    reserveIconSpace: Boolean = true,
) {
    Column(Modifier.addIf(onClick != null) { clickable(enabled, onClick = onClick!!) }) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (reserveIconSpace || icon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(32.dp),
                ) {
                    icon?.let {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = iconModifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    alternateIcon?.let {
                        alternateIcon()
                    }

                }

                Spacer(modifier = Modifier.requiredWidth(16.dp))
            }

            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {

                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onBackground,
                        LocalTextStyle provides MaterialTheme.typography.bodyLarge,
                    ) {
                        Text(label)
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

                if (rightContent != null) {
                    rightContent()
                }
            }
        }

    }
}