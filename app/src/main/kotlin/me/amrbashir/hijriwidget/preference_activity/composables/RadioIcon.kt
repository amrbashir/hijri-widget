package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun RadioIcon(
    selected: Boolean,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
) {

    val dotRadius = animateDpAsState(
        targetValue = if (selected) 12.dp / 2 else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = ""
    )
    val radioColor = colors.radioColor(enabled, selected)

    Canvas(
        Modifier
            .minimumInteractiveComponentSize()
            .wrapContentSize(Alignment.Center)
            .padding(2.dp)
            .requiredSize(20.dp)
    ) {
        val strokeWidth = 2.dp.toPx()
        drawCircle(
            radioColor.value,
            radius = (20.dp / 2).toPx() - strokeWidth / 2,
            style = Stroke(strokeWidth)
        )
        if (dotRadius.value > 0.dp) {
            drawCircle(radioColor.value, dotRadius.value.toPx() - strokeWidth / 2, style = Fill)
        }
    }
}


@Composable
fun RadioButtonColors.radioColor(enabled: Boolean, selected: Boolean): State<Color> {
    val target = when {
        enabled && selected -> selectedColor
        enabled && !selected -> unselectedColor
        !enabled && selected -> disabledSelectedColor
        else -> disabledUnselectedColor
    }

    return if (enabled) {
        animateColorAsState(target, tween(durationMillis = 100), label = "")
    } else {
        rememberUpdatedState(target)
    }
}
