package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.amrbashir.hijriwidget.preference_activity.LocalShowWidgetPreview
import me.amrbashir.hijriwidget.preference_activity.screenPadding

@Composable
fun PreferenceScreenLayout(
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
		modifier = modifier.screenPadding(),
	) {
		if (LocalShowWidgetPreview.current) {
			SharedWidgetPreview()
		}
		content()
	}
}
