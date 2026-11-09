package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.preference_activity.LocalShowWidgetPreview

@Composable
fun PreferenceScreenLayout(
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	ScreenLayout(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		if (LocalShowWidgetPreview.current) {
			SharedWidgetPreview()
		}
		content()
	}
}
