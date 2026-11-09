package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.ColorMode

@Composable
internal fun ColorOptions(
	modes: List<ColorMode>,
	selectedMode: ColorMode,
	previewColor: @Composable (ColorMode) -> Color,
	onModeSelected: (ColorMode) -> Unit,
) {
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		modes.forEach { mode ->
			val color = previewColor(mode)
			val description = stringResource(mode.label)
			val isSelected = mode == selectedMode

			ColorOption(
				mode = mode,
				color = color,
				description = description,
				selectedOption = isSelected,
				onClick = { onModeSelected(mode) },
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorOption(
	mode: ColorMode,
	color: Color,
	description: String,
	selectedOption: Boolean,
	onClick: () -> Unit,
) {
	val icon =
		when (mode) {
			ColorMode.Transparent -> Icons.Outlined.Block
			ColorMode.Custom -> Icons.Outlined.Colorize
			else -> if (selectedOption) Icons.Outlined.Check else null
		}

	val tint =
		when {
			color.alpha == 0F -> MaterialTheme.colorScheme.onSurface
			color.luminance() > 0.5F -> Color.Black
			else -> Color.White
		}

	TooltipBox(
		positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
		tooltip = { PlainTooltip { Text(description) } },
		state = rememberTooltipState(),
	) {
		Surface(
			onClick = onClick,
			modifier =
				Modifier.size(40.dp).semantics {
					contentDescription = description
					selected = selectedOption
				},
			shape = CircleShape,
			color = color,
			border =
				BorderStroke(
					if (selectedOption) 3.dp else 1.dp,
					MaterialTheme.colorScheme.outline,
				),
		) {
			if (icon != null) {
				Icon(
					imageVector = icon,
					contentDescription = null,
					modifier = Modifier.padding(if (mode == ColorMode.Transparent) 3.dp else 9.dp),
					tint = tint,
				)
			}
		}
	}
}
