package me.amrbashir.hijriwidget

import androidx.compose.ui.text.style.TextDirection

enum class SupportedLanguage {
    Arabic {
        override fun dir() = TextDirection.Rtl
    },
    English {
        override fun dir() = TextDirection.Ltr
    };

    abstract fun dir(): TextDirection
}
