package me.amrbashir.hijriwidget

import android.annotation.SuppressLint
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.edit
import androidx.glance.GlanceTheme
import androidx.glance.unit.ColorProvider
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val PREF = "HijriWidgetPref"
private const val THEME_KEY = "THEME"
private const val CUSTOM_COLOR_KEY = "CUSTOM_COLOR"
private const val BG_THEME_KEY = "BG_THEME"
private const val BG_CUSTOM_COLOR_KEY = "BG_CUSTOM_COLOR"
private const val TEXT_SHADOW_KEY = "TEXT_SHADOW"
private const val CUSTOM_TEXT_SIZE_KEY = "CUSTOM_TEXT_SIZE"
private const val DAY_START_HOUR_KEY = "DAY_START_HOUR"
private const val DAY_START_MINUTE_KEY = "DAY_START_MINUTE"
private const val DAY_OFFSET_KEY = "DAY_OFFSET"
private const val CALENDAR_CALCULATION_METHOD_KEY = "CALENDAR_CALCULATION_METHOD"
private const val FORMAT_KEY = "FORMAT"
private const val IS_CUSTOM_FORMAT_KEY = "IS_CUSTOM_FORMAT"
private const val CUSTOM_FORMAT_KEY = "CUSTOM_FORMAT"


object Preferences {
    val theme: MutableState<SupportedTheme> = mutableStateOf(Defaults.theme)
    val customColor: MutableState<Int> = mutableIntStateOf(Color.White.toArgb())
    val bgTheme: MutableState<SupportedTheme> = mutableStateOf(Defaults.bgTheme)
    val bgCustomColor: MutableState<Int> = mutableIntStateOf(Color.Transparent.toArgb())
    val textSize: MutableState<Float> = mutableFloatStateOf(Defaults.textSize)
    val textShadow: MutableState<Boolean> = mutableStateOf(Defaults.textShadow)
    val dayStart: MutableState<DayStart> = mutableStateOf(Defaults.dayStart)
    val dayOffset: MutableState<Int> = mutableIntStateOf(Defaults.dayOffset)
    val calendarCalculationMethod: MutableState<String> =
        mutableStateOf(Defaults.calendarCalculationMethod)
    val format: MutableState<String> = mutableStateOf(Defaults.format)
    val isCustomFormat: MutableState<Boolean> = mutableStateOf(Defaults.isCustomFormat)
    val customFormat: MutableState<String> = mutableStateOf(Defaults.customFormat)

    @Suppress("ConstPropertyName")
    object Defaults {
        val theme =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) SupportedTheme.Dynamic else SupportedTheme.System
        val bgTheme = SupportedTheme.Transparent
        const val textSize = 22F
        const val textShadow = true
        val dayStart = DayStart(0, 0)
        const val dayOffset = 0
        val calendarCalculationMethod = HijriDateCalculationMethod.ISLAMIC_UMALQURA.id
        val format = FORMAT_PRESETES[0]
        const val isCustomFormat = false
        const val customFormat = ""
    }

    fun restoreDefaults() {
        this.theme.value = Defaults.theme
        this.bgTheme.value = Defaults.bgTheme
        this.textSize.value = Defaults.textSize
        this.textShadow.value = Defaults.textShadow
        this.dayStart.value = Defaults.dayStart
        this.dayOffset.value = Defaults.dayOffset
        this.calendarCalculationMethod.value = Defaults.calendarCalculationMethod
        this.format.value = Defaults.format
        this.isCustomFormat.value = Defaults.isCustomFormat
    }


    fun migratePreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)

        if (!sharedPreferences.contains(FORMAT_KEY)) {
            sharedPreferences.getString("LANG", null)?.let {
                sharedPreferences.edit(commit = true) {
                    when (it) {
                        "Arabic" -> putString(
                            FORMAT_KEY,
                            "d MMMM yyyy"
                        )

                        "English" -> putString(
                            FORMAT_KEY,
                            "en-GB{d MMMM yyyy}"
                        )

                        else -> {}
                    }

                }
            }
        }

        if (!sharedPreferences.contains(TEXT_SHADOW_KEY)) {
            sharedPreferences.getBoolean("SHADOW", true).let {
                sharedPreferences.edit(commit = true) {
                    putBoolean(
                        TEXT_SHADOW_KEY,
                        it
                    )
                }
            }
        }
    }

    fun load(context: Context) {
        migratePreferences(context)

        val sharedPreferences = context.getSharedPreferences(PREF, 0)

        val theme = sharedPreferences.getString(THEME_KEY, Defaults.theme.toString())
            ?: Defaults.theme.toString()
        this.theme.value = SupportedTheme.valueOf(theme)
        this.customColor.value = sharedPreferences.getInt(CUSTOM_COLOR_KEY, Color.White.toArgb())

        val bgTheme = sharedPreferences.getString(BG_THEME_KEY, Defaults.bgTheme.toString())
            ?: Defaults.bgTheme.toString()
        this.bgTheme.value = SupportedTheme.valueOf(bgTheme)
        this.bgCustomColor.value =
            sharedPreferences.getInt(BG_CUSTOM_COLOR_KEY, Color.Transparent.toArgb())

        this.textShadow.value = sharedPreferences.getBoolean(TEXT_SHADOW_KEY, true)

        this.textSize.value = sharedPreferences.getFloat(CUSTOM_TEXT_SIZE_KEY, 22F)

        this.dayStart.value = DayStart(
            sharedPreferences.getInt(DAY_START_HOUR_KEY, 0),
            sharedPreferences.getInt(DAY_START_MINUTE_KEY, 0)
        )

        this.dayOffset.value = sharedPreferences.getInt(DAY_OFFSET_KEY, 0)

        this.calendarCalculationMethod.value =
            sharedPreferences.getString(
                CALENDAR_CALCULATION_METHOD_KEY,
                Defaults.calendarCalculationMethod
            )
                ?: Defaults.calendarCalculationMethod

        this.format.value =
            sharedPreferences.getString(FORMAT_KEY, Defaults.format) ?: Defaults.format
        this.isCustomFormat.value =
            sharedPreferences.getBoolean(IS_CUSTOM_FORMAT_KEY, Defaults.isCustomFormat)
        this.customFormat.value = sharedPreferences.getString(CUSTOM_FORMAT_KEY, "") ?: ""
    }

    fun save(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)
        sharedPreferences.edit(commit = true) {
            putString(THEME_KEY, this@Preferences.theme.value.toString())
            putInt(CUSTOM_COLOR_KEY, this@Preferences.customColor.value)
            putString(BG_THEME_KEY, this@Preferences.bgTheme.value.toString())
            putInt(BG_CUSTOM_COLOR_KEY, this@Preferences.bgCustomColor.value)
            putBoolean(TEXT_SHADOW_KEY, this@Preferences.textShadow.value)
            putFloat(CUSTOM_TEXT_SIZE_KEY, this@Preferences.textSize.value)
            putInt(DAY_START_HOUR_KEY, this@Preferences.dayStart.value.hour)
            putInt(DAY_START_MINUTE_KEY, this@Preferences.dayStart.value.minute)
            putInt(DAY_OFFSET_KEY, this@Preferences.dayOffset.value)
            putString(
                CALENDAR_CALCULATION_METHOD_KEY,
                this@Preferences.calendarCalculationMethod.value
            )
            putString(FORMAT_KEY, this@Preferences.format.value)
            putBoolean(IS_CUSTOM_FORMAT_KEY, this@Preferences.isCustomFormat.value)
            putString(CUSTOM_FORMAT_KEY, this@Preferences.customFormat.value)
        }
    }

    val Dark = Color(0xFF151515)

    @Composable
    fun getTextColor(context: Context): Color {
        return when {
            this.theme.value == SupportedTheme.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> GlanceTheme.colors.primary.getColor(
                context
            )

            this.theme.value == SupportedTheme.System && context.isDark() -> Color.White
            this.theme.value == SupportedTheme.System && !context.isDark() -> Dark
            this.theme.value == SupportedTheme.Dark -> Color.White
            this.theme.value == SupportedTheme.Light -> Dark
            this.theme.value == SupportedTheme.Custom -> Color(this.customColor.value)
            else -> Color.White
        }

    }

    @SuppressLint("RestrictedApi")
    @Composable
    fun getBgColor(context: Context): ColorProvider {
        return when {
            this.bgTheme.value == SupportedTheme.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> GlanceTheme.colors.widgetBackground
            this.theme.value == SupportedTheme.System && context.isDark() -> ColorProvider(Dark)
            this.theme.value == SupportedTheme.System && !context.isDark() -> ColorProvider(Color.White)
            this.theme.value == SupportedTheme.Dark -> ColorProvider(Dark)
            this.theme.value == SupportedTheme.Light -> ColorProvider(Color.White)
            this.theme.value == SupportedTheme.Custom -> ColorProvider(Color(this.customColor.value))
            else -> ColorProvider(Color.Transparent)
        }
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

