package me.amrbashir.hijriwidget.preference_activity.composables

import android.text.SpannableString
import android.view.View
import android.widget.TextView
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.applyFontWeight
import me.amrbashir.hijriwidget.applyHorizontalAlignment
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
			contentAlignment =
				BiasAbsoluteAlignment(
					prefsManager.horizontalAlignment.value.bias,
					prefsManager.verticalAlignment.value.bias,
				),
			modifier =
				Modifier
					.height(110.dp)
					.width(175.dp)
					.widgetCornerRadius(context)
					.background(prefsManager.getBgColor(context).getColor(context))
					.padding(
						start = prefsManager.paddingStart.value.dp,
						top = prefsManager.paddingTop.value.dp,
						end = prefsManager.paddingEnd.value.dp,
						bottom = prefsManager.paddingBottom.value.dp,
					),
		) {
			val textColor = prefsManager.getTextColor(context).toArgb()
			val isDeviceRTL = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

			AndroidView(
				modifier = Modifier.fillMaxWidth(),
				factory = { TextView(it) },
				update = { view ->
					view.text =
						SpannableString(date).apply {
							applyFontWeight(context, prefsManager.textFontFamily.value, prefsManager.getFontWeight())
							applyHorizontalAlignment(
								prefsManager.horizontalAlignment.value,
								isDeviceRTL,
							)
						}
					view.textSize = prefsManager.fontSize.value
					view.gravity = prefsManager.horizontalAlignment.value.gravity
					view.setTextColor(textColor)
					if (prefsManager.textShadow.value) {
						view.setShadowLayer(1F, 1F, 1F, android.graphics.Color.argb(128, 0, 0, 0))
					} else {
						view.setShadowLayer(0F, 0F, 0F, android.graphics.Color.TRANSPARENT)
					}
				},
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
