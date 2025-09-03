package me.amrbashir.hijriwidget.preferences.composables

import android.app.WallpaperManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.widgetCornerRadius

@Composable
fun WidgetPreview(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var date by remember {
        mutableStateOf(
            HijriDate.todayStr()
        )
    }

    val textSize = Preferences.textSize.value.sp

    val textColor = Preferences.getTextColor(context)
    val bgColor = Preferences.getBgColor(context)

    val wallpaperManager = WallpaperManager.getInstance(context)
    val builtinWallpaper = wallpaperManager.builtInDrawable.toBitmap().asImageBitmap()

    LaunchedEffect(
        Preferences.dayStart.value,
        Preferences.dateFormat.value,
        Preferences.dateCustomFormat.value,
        Preferences.dateIsCustomFormat.value,
        Preferences.dayOffset.value,
        Preferences.calendarCalculationMethod.value,
        HijriDate.today.value
    ) {
        date = HijriDate.todayStr()
    }

    // Wallpaper container
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .height(150.dp)
            .fillMaxWidth()
            .drawBehind {
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
                    dstOffset = IntOffset(offsetX.toInt(), offsetY.toInt())
                )
            }
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(110.dp)
                .width(175.dp)
                .widgetCornerRadius(context)
                .background(bgColor.getColor(context))
        ) {
            Text(
                date,
                color = textColor,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = textSize,
                    shadow = if (Preferences.textShadow.value) Shadow(
                        color = Color(0, 0, 0, 128),
                        offset = Offset(x = 1f, y = 1f),
                        blurRadius = 1f,
                    ) else null,
                ),
            )
        }
    }
}
