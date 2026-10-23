package me.amrbashir.hijriwidget.preference_activity.composables

import android.app.WallpaperManager
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalAnimatedContentScope
import me.amrbashir.hijriwidget.preference_activity.LocalSharedTransitionScope
import me.amrbashir.hijriwidget.preference_activity.LocalWidgetState
import me.amrbashir.hijriwidget.widgetCornerRadius

@Composable
fun WidgetPreview(
	modifier: Modifier = Modifier,
	onSettingsClick: (() -> Unit)? = null,
) {
	val context = LocalContext.current
	val prefsManager = LocalWidgetState.current.prefsManager
	val wallpaperManager = WallpaperManager.getInstance(context)

	val date = HijriDate.todayFormatted(prefsManager)

	// Wallpaper container
	Box(
		contentAlignment = Alignment.Center,
		modifier =
			modifier
				.clip(RoundedCornerShape(20.dp))
				.height(150.dp)
				.fillMaxWidth()
				.drawWithCache {
					val builtinWallpaper = wallpaperManager.builtInDrawable.toBitmap().asImageBitmap()
					onDrawBehind {
						val imageWidth = builtinWallpaper.width.toFloat()
						val imageHeight = builtinWallpaper.height.toFloat()
						val canvasWidth = size.width
						val canvasHeight = size.height

						val scale = maxOf(canvasWidth / imageWidth, canvasHeight / imageHeight)

						val scaledWidth = imageWidth * scale
						val scaledHeight = imageHeight * scale

						val offsetX = (canvasWidth - scaledWidth) / 2
						val offsetY = (canvasHeight - scaledHeight) / 2

						drawImage(
							image = builtinWallpaper,
							dstSize = IntSize(scaledWidth.toInt(), scaledHeight.toInt()),
							dstOffset = IntOffset(offsetX.toInt(), offsetY.toInt()),
						)
					}
				},
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
	val widgetState = LocalWidgetState.current

	val key = "widget_preview_${widgetState.appWidgetId}"

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
