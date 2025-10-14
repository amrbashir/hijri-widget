package me.amrbashir.hijriwidget.preference_activity.composables.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import me.amrbashir.hijriwidget.R

@Composable
fun CollapsibleButton(
    header: String,
    collapsed: Boolean = true,
    content: @Composable (() -> Unit),
) {
    var collapsed by remember { mutableStateOf(collapsed) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (collapsed) 0f else 180f,
        label = "accordion-arrow"
    )

    PreferenceTemplate(
        header,
        endContent = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.outline_keyboard_arrow_down_24),
                contentDescription = null,
                modifier = Modifier.rotate(arrowRotation)
            )
        },
        onClick = { collapsed = !collapsed },
    ) {
        AnimatedVisibility(
            visible = !collapsed,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            content()
        }
    }
}
