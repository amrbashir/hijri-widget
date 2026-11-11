package me.amrbashir.hijriwidget.preference_activity.composables.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun ItemGroup(
	label: String? = null,
	headerAction: (@Composable () -> Unit)? = null,
	content: @Composable ColumnScope.() -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(16.dp),
	) {
		label?.let {
			Row(
				modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					label,
					style = MaterialTheme.typography.labelLarge,
					color = MaterialTheme.colorScheme.primary,
					modifier = Modifier.weight(1F),
				)

				headerAction?.invoke()
			}
		}

		Column(
			modifier = Modifier.clip(RoundedCornerShape(20.dp)),
			verticalArrangement = Arrangement.spacedBy(2.dp),
		) {
			content()
		}
	}
}
