package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalAnimatedContentScope
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget
import me.amrbashir.hijriwidget.preference_activity.LocalSharedTransitionScope
import me.amrbashir.hijriwidget.widgetCornerRadius

@Composable
fun WidgetPreview(
	modifier: Modifier = Modifier,
	onSettingsClick: (() -> Unit)? = null,
) {
	val context = LocalContext.current
	val prefsManager = LocalSelectedWidget.current.prefsManager

	val date = HijriDate.todayFormatted(prefsManager)

	// Wallpaper container
	Box(
		contentAlignment = Alignment.Center,
		modifier =
			modifier
				.clip(RoundedCornerShape(20.dp))
				.height(150.dp)
				.fillMaxWidth()
				// Clearing the pixels shows the home screen wallpaper behind the window,
				// reading the wallpaper itself needs permissions not granted to regular apps
				.drawBehind { drawRect(Color.Transparent, blendMode = BlendMode.Clear) },
	) {
		Box(
			contentAlignment = Alignment.Center,
			modifier =
				Modifier
					.height(110.dp)
					.width(175.dp)
					.widgetCornerRadius(context)
					.background(prefsManager.getBgColor().getColor(context)),
		) {
			Text(
				date,
				color = prefsManager.getTextColor(context),
				style =
					TextStyle(
						textAlign = TextAlign.Center,
						fontSize = prefsManager.textSize.value.sp,
						shadow =
							if (prefsManager.textShadow.value) {
								Shadow(
									color = Color(0, 0, 0, 128),
									offset = Offset(x = 1f, y = 1f),
									blurRadius = 1f,
								)
							} else {
								null
							},
					),
			)
		}

		if (onSettingsClick != null) {
			FilledTonalIconButton(
				onClick = onSettingsClick,
				modifier =
					Modifier
						.align(Alignment.BottomEnd)
						.padding(8.dp),
			) {
				Icon(
					Icons.Outlined.Settings,
					contentDescription = stringResource(R.string.home_screen_widget_settings),
				)
			}
		}
	}
}

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun SharedWidgetPreview(
	modifier: Modifier = Modifier,
	onSettingsClick: (() -> Unit)? = null,
) {
	val sharedTransitionScope = LocalSharedTransitionScope.current
	val sharedAnimatedContentScope = LocalAnimatedContentScope.current
	val widget = LocalSelectedWidget.current

	val key = "widget_preview_${widget.appWidgetId}"

	with(receiver = sharedTransitionScope) {
		WidgetPreview(
			onSettingsClick = onSettingsClick,
			modifier =
				modifier.sharedElement(
					sharedContentState = rememberSharedContentState(key),
					animatedVisibilityScope = sharedAnimatedContentScope,
				),
		)
	}
}
