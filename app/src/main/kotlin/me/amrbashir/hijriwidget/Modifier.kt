package me.amrbashir.hijriwidget

import androidx.compose.ui.Modifier

inline fun Modifier.addIf(condition: Boolean, crossinline factory: Modifier.() -> Modifier): Modifier =
    if (condition) factory() else this
