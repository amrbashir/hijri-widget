package me.amrbashir.hijriwidget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceTheme
import androidx.glance.unit.ColorProvider

val Color.Companion.Dark: Color get() = Color(0xFF151515)


class PreferencesManagerV2(
    val context: Context,
    val prefs: Preferences,
) {
    inner class PreferenceV2<T, K>(
        val key: Preferences.Key<K>,
        val defaultValue: T,
        val parser: (K) -> T = { it as T },
        val save: (T) -> K = { it as K },
    ) {
        val value
            get() = prefs[key]?.let {
                parser(it)
            } ?: defaultValue
    }

    val bgColorMode = PreferenceV2(
        key = stringPreferencesKey("BG_COLOR_MODE"),
        defaultValue = ColorMode.Transparent,
        parser = { enumValueOf(it) },
        save = { it.toString() }
    )

    val bgCustomColor = PreferenceV2(
        key = intPreferencesKey("BG_CUSTOM_COLOR"),
        defaultValue = Color.Transparent.toArgb(),
    )

    val textColorMode = PreferenceV2(
        key = stringPreferencesKey("TEXT_COLOR_MODE"),
        defaultValue = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ColorMode.Dynamic else ColorMode.System,
        parser = { enumValueOf(it) },
        save = { it.toString() }
    )

    val textCustomColor = PreferenceV2(
        key = intPreferencesKey("TEXT_CUSTOM_COLOR"),
        defaultValue = Color.White.toArgb(),
    )

    val textSize = PreferenceV2(
        key = intPreferencesKey("TEXT_SIZE"),
        defaultValue = 22F,
    )

    val textShadow = PreferenceV2(
        key = booleanPreferencesKey("TEXT_SHADOW"),
        defaultValue = true
    )

    val dateFormat = PreferenceV2(
        key = stringPreferencesKey("DATE_FORMAT"),
        defaultValue = "d MMMM yyyy"
    )

    val dateIsCustomFormat = PreferenceV2(
        key = booleanPreferencesKey("DATE_IS_CUSTOM_FORMAT"),
        defaultValue = false
    )

    val dateCustomFormat = PreferenceV2(
        key = stringPreferencesKey("DATE_CUSTOM_FORMAT"),
        defaultValue = ""
    )

    val dayStart = PreferenceV2(
        key = intPreferencesKey("DAY_START"),
        defaultValue = 0, // 12:00 AM
    )

    val calendarCalculationMethod = PreferenceV2(
        key = stringPreferencesKey("CALENDAR_CALCULATION_METHOD"),
        defaultValue = HijriDateCalculationMethod.ISLAMIC_UMALQURA,
        parser = { enumValueOf(it) },
        save = { it.toString() }
    )

    val dayOffset = PreferenceV2(
        key = intPreferencesKey("DAY_OFFSET"),
        defaultValue = 0
    )

    @Composable
    fun getTextColor(): Color {
        return when (this.textColorMode.value) {
            ColorMode.Dynamic if Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> GlanceTheme.colors.primary.getColor(
                context
            )

            ColorMode.System if isSystemInDarkTheme() -> Color.Dark
            ColorMode.System if !isSystemInDarkTheme() -> Color.White
            ColorMode.Dark -> Color.Dark
            ColorMode.Light -> Color.White
            ColorMode.Custom -> Color(this.textCustomColor.value)
            else -> Color.White
        }

    }

    @SuppressLint("RestrictedApi")
    @Composable
    fun getBgColor(): ColorProvider {
        return when (this.bgColorMode.value) {
            ColorMode.Dynamic if Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> GlanceTheme.colors.widgetBackground
            ColorMode.System if isSystemInDarkTheme() -> ColorProvider(
                Color.Dark
            )

            ColorMode.System if !isSystemInDarkTheme() -> ColorProvider(
                Color.White
            )

            ColorMode.Dark -> ColorProvider(Color.Dark)
            ColorMode.Light -> ColorProvider(Color.White)
            ColorMode.Custom -> ColorProvider(Color(this.bgCustomColor.value))
            else -> ColorProvider(Color.Transparent)
        }
    }
}
