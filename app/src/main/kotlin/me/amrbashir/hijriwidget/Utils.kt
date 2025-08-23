package me.amrbashir.hijriwidget

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.glance.GlanceModifier

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

@Composable
inline fun GlanceModifier.addIf(
    condition: Boolean,
    crossinline factory: @Composable GlanceModifier.() -> GlanceModifier
): GlanceModifier = if (condition) factory() else this

