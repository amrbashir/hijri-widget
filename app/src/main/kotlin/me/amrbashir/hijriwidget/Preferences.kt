package me.amrbashir.hijriwidget

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.icu.util.Calendar
import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val PREF = "HijriWidgetPref"
private const val LANG_KEY = "LANG"
private const val THEME_KEY = "THEME"
private const val CUSTOM_COLOR_KEY = "CUSTOM_COLOR"
private const val BG_THEME_KEY = "BG_THEME"
private const val BG_CUSTOM_COLOR_KEY = "BG_CUSTOM_COLOR"
private const val SHADOW_KEY = "SHADOW"
private const val CUSTOM_TEXT_SIZE_KEY = "CUSTOM_TEXT_SIZE"
private const val DAY_START_HOUR_KEY = "DAY_START_HOUR"
private const val DAY_START_MINUTE_KEY = "DAY_START_MINUTE"
private const val DAY_OFFSET_KEY = "DAY_OFFSET"
private const val CALENDAR_CALCULATION_METHOD_KEY = "CALENDAR_CALCULATION_METHOD"

object Preferences {
    val language: MutableState<SupportedLanguage> = mutableStateOf(Defaults.language)
    val theme: MutableState<SupportedTheme> = mutableStateOf(Defaults.theme)
    val color: MutableState<Int> = mutableIntStateOf(Defaults.color)
    val customColor: MutableState<Int> = mutableIntStateOf(Defaults.color)
    val bgTheme: MutableState<SupportedTheme> = mutableStateOf(Defaults.bgTheme)
    val bgColor: MutableState<Int> = mutableIntStateOf(Defaults.bgColor)
    val bgCustomColor: MutableState<Int> = mutableIntStateOf(Defaults.bgColor)
    val customTextSize: MutableState<Float> = mutableFloatStateOf(Defaults.customTextSize)
    val shadow: MutableState<Boolean> = mutableStateOf(Defaults.shadow)
    val dayStart: MutableState<DayStart> = mutableStateOf(Defaults.dayStart)
    val dayOffset: MutableState<Int> = mutableIntStateOf(Defaults.dayOffset)
    val calendarCalculationMethod: MutableState<String> =
        mutableStateOf(Defaults.calendarCalculationMethod)

    @Suppress("ConstPropertyName")
    object Defaults {
        val language = SupportedLanguage.Arabic
        val theme =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) SupportedTheme.Dynamic else SupportedTheme.System
        val color = Color.White.toArgb()
        val bgTheme =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) SupportedTheme.Dynamic else SupportedTheme.Transparent
        val bgColor = Color.Transparent.toArgb()
        const val customTextSize = 22F
        const val shadow = true
        val dayStart = DayStart(0, 0)
        const val dayOffset = 0
        val calendarCalculationMethod = CalendarCalculationMethod.ISLAMIC_UMALQURA.toString()
    }

    fun restoreDefaults(context: Context) {
        this.language.value = Defaults.language
        this.theme.value = Defaults.theme
        this.bgTheme.value = Defaults.bgTheme
        this.customTextSize.value = Defaults.customTextSize
        this.shadow.value = Defaults.shadow
        this.dayStart.value = Defaults.dayStart
        this.dayOffset.value = Defaults.dayOffset
        this.calendarCalculationMethod.value = Defaults.calendarCalculationMethod
        this.updateColor(context)
    }


    fun load(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)

        val lang = sharedPreferences.getString(LANG_KEY, "Arabic") ?: "Arabic"
        this.language.value = SupportedLanguage.valueOf(lang)

        val theme = sharedPreferences.getString(THEME_KEY, "Dynamic") ?: "Dynamic"
        this.theme.value = SupportedTheme.valueOf(theme)
        this.customColor.value = sharedPreferences.getInt(CUSTOM_COLOR_KEY, Color.White.toArgb())

        val bgTheme = sharedPreferences.getString(BG_THEME_KEY, "Dynamic") ?: "Dynamic"
        this.bgTheme.value = SupportedTheme.valueOf(bgTheme)
        this.bgCustomColor.value =
            sharedPreferences.getInt(BG_CUSTOM_COLOR_KEY, Color.Transparent.toArgb())

        this.shadow.value = sharedPreferences.getBoolean(SHADOW_KEY, true)

        this.customTextSize.value = sharedPreferences.getFloat(CUSTOM_TEXT_SIZE_KEY, 22F)

        this.dayStart.value = DayStart(
            sharedPreferences.getInt(DAY_START_HOUR_KEY, 0),
            sharedPreferences.getInt(DAY_START_MINUTE_KEY, 0)
        )

        this.dayOffset.value = sharedPreferences.getInt(DAY_OFFSET_KEY, 0)

        this.calendarCalculationMethod.value =
            sharedPreferences.getString(CALENDAR_CALCULATION_METHOD_KEY, "islamic-umalqura")
                ?: "islamic-umalqura"

        this.updateColor(context)
        this.updateBgColor(context)
    }

    fun save(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)
        sharedPreferences.edit()?.run {
            putString(LANG_KEY, this@Preferences.language.value.toString())
            putString(THEME_KEY, this@Preferences.theme.value.toString())
            putInt(CUSTOM_COLOR_KEY, this@Preferences.customColor.value)
            putString(BG_THEME_KEY, this@Preferences.bgTheme.value.toString())
            putInt(BG_CUSTOM_COLOR_KEY, this@Preferences.bgCustomColor.value)
            putBoolean(SHADOW_KEY, this@Preferences.shadow.value)
            putFloat(CUSTOM_TEXT_SIZE_KEY, this@Preferences.customTextSize.value)
            putInt(DAY_START_HOUR_KEY, this@Preferences.dayStart.value.hour)
            putInt(DAY_START_MINUTE_KEY, this@Preferences.dayStart.value.minute)
            putInt(DAY_OFFSET_KEY, this@Preferences.dayOffset.value)
            putString(
                CALENDAR_CALCULATION_METHOD_KEY,
                this@Preferences.calendarCalculationMethod.value
            )
            commit()
        }
    }

    fun updateColor(context: Context) {
        val newColor = when {
            this.theme.value == SupportedTheme.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (context.isDark()) dynamicDarkColorScheme(context).primary
                else dynamicLightColorScheme(context).primary
            }

            this.theme.value == SupportedTheme.System && context.isDark() -> darkScheme.onSurface
            this.theme.value == SupportedTheme.System && !context.isDark() -> lightScheme.onSurface
            this.theme.value == SupportedTheme.Dark -> lightScheme.onSurface
            this.theme.value == SupportedTheme.Light -> darkScheme.onSurface
            this.theme.value == SupportedTheme.Custom -> Color(this.customColor.value)
            else -> lightScheme.onSurface
        }

        this.color.value = newColor.toArgb()
    }

    fun updateBgColor(context: Context) {
        val newColor = when {
            this.bgTheme.value == SupportedTheme.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (context.isDark()) dynamicDarkColorScheme(context).primaryContainer
                else dynamicLightColorScheme(context).primaryContainer
            }

            this.bgTheme.value == SupportedTheme.System && context.isDark() -> darkScheme.surface
            this.bgTheme.value == SupportedTheme.System && !context.isDark() -> lightScheme.surface
            this.bgTheme.value == SupportedTheme.Dark -> darkScheme.surface
            this.bgTheme.value == SupportedTheme.Light -> lightScheme.surface
            this.bgTheme.value == SupportedTheme.Custom -> Color(this.bgCustomColor.value)
            else -> Color.Transparent
        }

        this.bgColor.value = newColor.toArgb()
    }

    fun nextUpdateDateInMillis(): Long {
        val nextDayStart = Calendar.getInstance()
        if (
            nextDayStart[Calendar.HOUR_OF_DAY] >= this.dayStart.value.hour &&
            nextDayStart[Calendar.MINUTE] >= this.dayStart.value.minute
        ) {
            nextDayStart[Calendar.DAY_OF_MONTH] = nextDayStart[Calendar.DAY_OF_MONTH] + 1
        }
        nextDayStart[Calendar.HOUR_OF_DAY] = this.dayStart.value.hour
        nextDayStart[Calendar.MINUTE] = this.dayStart.value.minute
        nextDayStart[Calendar.SECOND] = 0

        val now = Calendar.getInstance()

        val millisToNextDay = nextDayStart.timeInMillis - now.timeInMillis

        return System.currentTimeMillis() + millisToNextDay
    }

}

data class DayStart(val hour: Int, val minute: Int) {
    override fun toString(): String {
        val localTime = LocalTime.of(
            hour, minute
        )
        return localTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    }
}


fun Context.isDark(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}
