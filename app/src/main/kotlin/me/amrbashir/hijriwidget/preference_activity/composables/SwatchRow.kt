package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.ColorMode
import kotlin.math.ceil
import kotlin.math.floor

@Composable
internal fun SwatchRow(
	modes: List<ColorMode>,
	selectedMode: ColorMode,
	previewColor: @Composable (ColorMode) -> Color,
	onModeSelected: (ColorMode) -> Unit,
) {
	BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
		val count = modes.size
		val capacity = floor((maxWidth.value + 8F) / 58F).toInt().coerceAtLeast(1)
		val rowCount = ceil(count.toFloat() / capacity).toInt().coerceAtLeast(1)
		val itemsPerRow = ceil(count.toFloat() / rowCount).toInt().coerceAtLeast(1)

		FlowRow(
			maxItemsInEachRow = itemsPerRow,
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
			verticalArrangement = Arrangement.spacedBy(8.dp),
		) {
			modes.forEach { mode ->
				Swatch(
					mode = mode,
					color = previewColor(mode),
					description = stringResource(mode.label),
					selectedOption = mode == selectedMode,
					onClick = { onModeSelected(mode) },
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Swatch(
	mode: ColorMode,
	color: Color,
	description: String,
	selectedOption: Boolean,
	onClick: () -> Unit,
) {
	val icon =
		when (mode) {
			ColorMode.DeviceTheme -> DeviceThemeColorIcon
			ColorMode.Transparent -> Icons.Outlined.Block
			ColorMode.Custom -> Icons.Outlined.Colorize
			else -> null
		}

	// These colors swap brightness with the theme; choose the one that contrasts with the swatch.
	val contentColors =
		listOf(MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.inverseOnSurface)
	val tint =
		when {
			mode == ColorMode.DeviceTheme -> Color.Unspecified
			mode != ColorMode.Custom -> LocalContentColor.current
			color.luminance() > 0.5F -> contentColors.minBy { it.luminance() }
			else -> contentColors.maxBy { it.luminance() }
		}

	val ringColor =
		if (selectedOption) {
			MaterialTheme.colorScheme.primary
		} else {
			MaterialTheme.colorScheme.outlineVariant
		}

	TooltipBox(
		positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
		tooltip = { PlainTooltip { Text(description) } },
		state = rememberTooltipState(),
	) {
		Box(
			modifier =
				Modifier
					.size(50.dp)
					.detachedRing(color = ringColor)
					.padding(5.dp),
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
			) {
				if (icon != null) {
					Icon(
						imageVector = icon,
						contentDescription = null,
						modifier = if (mode == ColorMode.Custom) Modifier.padding(10.dp) else Modifier,
						tint = tint,
					)
				}
			}

			if (mode.isDynamic) {
				DynamicColorBadge(modifier = Modifier.align(Alignment.BottomEnd).offset(x = 4.dp, y = 4.dp))
			}
		}
	}
}

@Composable
private fun DynamicColorBadge(modifier: Modifier = Modifier) {
	Surface(
		modifier = modifier.size(20.dp),
		shape = CircleShape,
		color = MaterialTheme.colorScheme.secondaryContainer,
		border = BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceBright),
	) {
		Icon(
			imageVector = Icons.Outlined.AutoAwesome,
			contentDescription = null,
			modifier = Modifier.padding(4.dp),
		)
	}
}

private val DeviceThemeColorIcon =
	ImageVector
		.Builder(
			name = "SystemColor",
			defaultWidth = 40.dp,
			defaultHeight = 40.dp,
			viewportWidth = 40F,
			viewportHeight = 40F,
		).apply {
			path(fill = SolidColor(Color(0xFF151515))) {
				moveTo(20F, 0F)
				arcTo(20F, 20F, 0F, false, false, 20F, 40F)
				close()
			}
			path(fill = SolidColor(Color.White)) {
				moveTo(20F, 0F)
				arcTo(20F, 20F, 0F, false, true, 20F, 40F)
				close()
			}
		}.build()

private fun Modifier.detachedRing(color: Color): Modifier =
	drawBehind {
		val stroke = 3.dp.toPx()
		drawCircle(
			color = color,
			radius = size.minDimension / 2 - stroke / 2,
			style = Stroke(stroke),
		)
	}
