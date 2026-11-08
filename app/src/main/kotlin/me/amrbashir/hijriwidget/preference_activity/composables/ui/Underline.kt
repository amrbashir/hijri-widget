package me.amrbashir.hijriwidget.preference_activity.composables.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Draws a solid or dashed underline along the bottom edge without affecting layout. */
internal fun Modifier.underline(
	color: Color,
	dashed: Boolean = false,
	thickness: Dp = 2.dp,
): Modifier =
	drawBehind {
		val strokeWidth = thickness.toPx()
		val y = size.height - strokeWidth / 2
		drawLine(
			color = color,
			start = Offset(0F, y),
			end = Offset(size.width, y),
			strokeWidth = strokeWidth,
			pathEffect =
				if (dashed) {
					PathEffect.dashPathEffect(
						floatArrayOf(4.dp.toPx(), 3.dp.toPx()),
					)
				} else {
					null
				},
		)
	}
