package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Scrollable screen content with room to reveal its last item above the floating controls. */
@Composable
internal fun ScrollableScreenColumn(
	modifier: Modifier = Modifier,
	verticalArrangement: Arrangement.Vertical = Arrangement.Top,
	contentPadding: PaddingValues = PaddingValues(0.dp),
	content: @Composable ColumnScope.() -> Unit,
) {
	Column(
		modifier =
			modifier
				.verticalScroll(
					rememberScrollState(),
				).mainNavigationBarPadding()
				.padding(contentPadding),
		verticalArrangement = verticalArrangement,
		content = content,
	)
}
