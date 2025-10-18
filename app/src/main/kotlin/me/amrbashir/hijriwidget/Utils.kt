package me.amrbashir.hijriwidget

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.icu.text.DateFormat
import android.os.Build
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun Char.isRtl(): Boolean = when (Character.getDirectionality(this)) {
    Character.DIRECTIONALITY_RIGHT_TO_LEFT,
    Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC,
    Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING,
    Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE,
        -> true

    else -> false
}

fun Context.isDark(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}


inline fun Modifier.addIf(
    condition: Boolean,
    crossinline factory: Modifier.() -> Modifier
): Modifier = if (condition) factory() else this


fun GlanceModifier.widgetCornerRadius(): GlanceModifier {
    val cornerRadiusModifier =
        if (Build.VERSION.SDK_INT >= 31) {
            GlanceModifier.cornerRadius(android.R.dimen.system_app_widget_background_radius)
        } else {
            GlanceModifier
        }

    return this.then(cornerRadiusModifier)
}

@Composable
fun Modifier.widgetCornerRadius(context: Context): Modifier {
    val cornerRadiusModifier =
        if (Build.VERSION.SDK_INT >= 31) {
            val radiusId = android.R.dimen.system_app_widget_background_radius
            val radius = context.resources.getDimension(radiusId)
            Modifier.clip(RoundedCornerShape(radius))
        } else {
            Modifier
        }

    return this.then(cornerRadiusModifier)
}


fun Int.formatTime(): String? {
    val hour = this / 60
    val minute = this % 60
    val localTime = LocalTime.of(hour, minute)
    return localTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
}

fun logTimestamp(date: Long = System.currentTimeMillis()): String {
    val dateFormatter = DateFormat.getDateTimeInstance(
        DateFormat.DEFAULT,
        DateFormat.DEFAULT,
        Locale.ENGLISH
    )
    return dateFormatter.format(Date(date))
}

val logException = fun(tr: Throwable) { Log.e("Exception", null, tr) }
