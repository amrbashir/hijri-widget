package me.amrbashir.hijriwidget.preference_activity.composables.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

internal class ConnectedButtonGroupScope internal constructor(
	private val rowScope: RowScope,
) : RowScope by rowScope {
	@Composable
	fun ConnectedButton(
		onClick: () -> Unit,
		modifier: Modifier = Modifier,
		enabled: Boolean = true,
		content: @Composable RowScope.() -> Unit,
	) {
		val interactionSource = remember { MutableInteractionSource() }
		val pressed by interactionSource.collectIsPressedAsState()
		val cornerRadius by animateDpAsState(
			targetValue = if (pressed) 20.dp else 4.dp,
			label = "connected button corners",
		)

		Button(
			onClick = onClick,
			enabled = enabled,
			modifier = modifier.height(40.dp),
			shape = RoundedCornerShape(cornerRadius),
			interactionSource = interactionSource,
			contentPadding = PaddingValues(horizontal = 12.dp),
			content = content,
		)
	}
}

/** Lays out buttons with shared outer corners and narrow gaps. */
@Composable
internal fun ConnectedButtonGroup(
	modifier: Modifier = Modifier,
	content: @Composable ConnectedButtonGroupScope.() -> Unit,
) {
	Row(
		modifier = modifier.clip(RoundedCornerShape(24.dp)),
		horizontalArrangement = Arrangement.spacedBy(2.dp),
	) {
		ConnectedButtonGroupScope(this).content()
	}
}
