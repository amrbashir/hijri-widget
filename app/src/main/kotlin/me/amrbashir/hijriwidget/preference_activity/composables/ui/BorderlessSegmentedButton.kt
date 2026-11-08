package me.amrbashir.hijriwidget.preference_activity.composables.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/** A square segmented button without an outline, with an optional divider on its leading edge. */
@Composable
fun SingleChoiceSegmentedButtonRowScope.BorderlessSegmentedButton(
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	showLeadingDivider: Boolean = false,
	label: @Composable () -> Unit,
) {
	val dividerColor = MaterialTheme.colorScheme.outlineVariant

	SegmentedButton(
		selected = selected,
		onClick = onClick,
		shape = RectangleShape,
		border = BorderStroke(0.dp, Color.Transparent),
		colors = SegmentedButtonDefaults.colors(inactiveContainerColor = Color.Transparent),
		contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
		modifier =
			modifier.fillMaxHeight().drawWithContent {
				drawContent()
				if (showLeadingDivider) {
					val x = if (layoutDirection == LayoutDirection.Rtl) size.width else 0F
					drawLine(dividerColor, Offset(x, 0F), Offset(x, size.height), 1.dp.toPx())
				}
			},
		label = label,
	)
}
