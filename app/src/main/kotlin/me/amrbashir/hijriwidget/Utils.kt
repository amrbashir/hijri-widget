package me.amrbashir.hijriwidget

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius

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
            val radius =
                context.resources.getDimension(android.R.dimen.system_app_widget_background_radius)
            Modifier.clip(RoundedCornerShape(radius.toFloat()))
        } else {
            Modifier
        }

    return this.then(cornerRadiusModifier)
}
