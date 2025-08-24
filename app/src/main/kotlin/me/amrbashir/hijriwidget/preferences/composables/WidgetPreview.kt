import android.app.WallpaperManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences

@Composable
fun WidgetPreview(context: Context) {
    var date by remember {
        mutableStateOf(
            HijriDate.todayStr()
        )
    }

    val textSize = Preferences.textSize.value.sp

    val textColor = Preferences.getColor(context)
    val bgColor = Preferences.getBgColor(context)

    val wallpaperManager = WallpaperManager.getInstance(context)
    val builtinWallpaper = wallpaperManager.builtInDrawable.toBitmap().asImageBitmap()

    LaunchedEffect(
        Preferences.dayStart.value,
        Preferences.format.value,
        Preferences.customFormat.value,
        Preferences.isCustomFormat.value,
        Preferences.dayOffset.value,
        Preferences.calendarCalculationMethod.value,
        HijriDate.today.value
    ) {
        date = HijriDate.todayStr()
    }

    // Wallpaper container
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .height(220.dp)
            .fillMaxWidth()
            .drawBehind {
                drawImage(
                    image = builtinWallpaper,
                    dstSize = IntSize(size.width.toInt(), size.height.toInt())
                )
            }
    ) {
        // Widget container simulating the resize bounds/handles on actual widget provided by the OS
        Box(
            modifier = Modifier
                .height(110.dp)
                .width(175.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            // This is the same UI tree that is used for the widget
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .fillMaxSize()
                        .background(bgColor.getColor(context))
                ) {
                    Text(
                        date,
                        color = textColor.getColor(context),
                        style = TextStyle(
                            fontSize = textSize,
                            shadow = if (Preferences.shadow.value) Shadow(
                                color = Color(0, 0, 0, 128),
                                offset = Offset(x = 1f, y = 1f),
                                blurRadius = 1f,
                            ) else null,
                        ),
                    )
                }
            }
        }
    }
}
