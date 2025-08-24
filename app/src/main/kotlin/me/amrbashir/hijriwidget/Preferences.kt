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
private const val BG_COLOR_MODE_KEY = "BG_COLOR_MODE"
private const val BG_CUSTOM_COLOR_KEY = "BG_CUSTOM_COLOR"
private const val TEXT_COLOR_MODE_KEY = "TEXT_COLOR_MODE"
private const val TEXT_CUSTOM_COLOR_KEY = "TEXT_CUSTOM_COLOR"
private const val TEXT_SIZE_KEY = "TEXT_SIZE"
private const val TEXT_SHADOW_KEY = "TEXT_SHADOW"
private const val TEXT_FORMAT_KEY = "TEXT_FORMAT"
private const val TEXT_IS_CUSTOM_FORMAT_KEY = "TEXT_IS_CUSTOM_FORMAT"
private const val TEXT_CUSTOM_FORMAT_KEY = "TEXT_CUSTOM_FORMAT"
private const val DAY_START_HOUR_KEY = "DAY_START_HOUR"
private const val DAY_START_MINUTE_KEY = "DAY_START_MINUTE"
private const val CALENDAR_CALCULATION_METHOD_KEY = "CALENDAR_CALCULATION_METHOD"
private const val DAY_OFFSET_KEY = "DAY_OFFSET"


object Preferences {
    val bgColorMode: MutableState<ColorMode> = mutableStateOf(Defaults.bgColorMode)
    val bgCustomColor: MutableState<Int> = mutableIntStateOf(Color.Transparent.toArgb())
    val textColorMode: MutableState<ColorMode> = mutableStateOf(Defaults.textColorMode)
    val textCustomColor: MutableState<Int> = mutableIntStateOf(Color.White.toArgb())
    val textSize: MutableState<Float> = mutableFloatStateOf(Defaults.textSize)
    val textShadow: MutableState<Boolean> = mutableStateOf(Defaults.textShadow)
    val textFormat: MutableState<String> = mutableStateOf(Defaults.textFormat)
    val textIsCustomFormat: MutableState<Boolean> = mutableStateOf(Defaults.textIsCustomFormat)
    val textCustomFormat: MutableState<String> = mutableStateOf(Defaults.textCustomFormat)
    val dayStart: MutableState<DayStart> = mutableStateOf(Defaults.dayStart)
    val calendarCalculationMethod: MutableState<String> =
        mutableStateOf(Defaults.calendarCalculationMethod)
    val dayOffset: MutableState<Int> = mutableIntStateOf(Defaults.dayOffset)

    @Suppress("ConstPropertyName")
    object Defaults {
        val bgColorMode =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ColorMode.Dynamic else ColorMode.System
        val textColorMode =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ColorMode.Dynamic else ColorMode.System
        const val textSize = 22F
        const val textShadow = true
        val textFormat = FORMAT_PRESETES[0]
        const val textIsCustomFormat = false
        const val textCustomFormat = ""
        val dayStart = DayStart(0, 0)
        val calendarCalculationMethod = HijriDateCalculationMethod.ISLAMIC_UMALQURA.id
        const val dayOffset = 0
    }

    fun restoreDefaults() {
        this.textColorMode.value = Defaults.textColorMode
        this.bgColorMode.value = Defaults.bgColorMode
        this.textSize.value = Defaults.textSize
        this.textShadow.value = Defaults.textShadow
        this.dayStart.value = Defaults.dayStart
        this.dayOffset.value = Defaults.dayOffset
        this.calendarCalculationMethod.value = Defaults.calendarCalculationMethod
        this.textFormat.value = Defaults.textFormat
        this.textIsCustomFormat.value = Defaults.textIsCustomFormat
    }

    fun load(context: Context) {
        migratePreferences(context)

        val sharedPreferences = context.getSharedPreferences(PREF, 0)

        val bgColorMode =
            sharedPreferences.getString(BG_COLOR_MODE_KEY, Defaults.bgColorMode.toString())
                ?: Defaults.bgColorMode.toString()
        this.bgColorMode.value = ColorMode.valueOf(bgColorMode)
        this.bgCustomColor.value =
            sharedPreferences.getInt(BG_CUSTOM_COLOR_KEY, Color.Transparent.toArgb())

        val textColorMode =
            sharedPreferences.getString(TEXT_COLOR_MODE_KEY, Defaults.textColorMode.toString())
                ?: Defaults.textColorMode.toString()
        this.textColorMode.value = ColorMode.valueOf(textColorMode)
        this.textCustomColor.value =
            sharedPreferences.getInt(TEXT_CUSTOM_COLOR_KEY, Color.White.toArgb())

        this.textSize.value = sharedPreferences.getFloat(TEXT_SIZE_KEY, 22F)

        this.textShadow.value = sharedPreferences.getBoolean(TEXT_SHADOW_KEY, true)

        this.textFormat.value =
            sharedPreferences.getString(TEXT_FORMAT_KEY, Defaults.textFormat) ?: Defaults.textFormat
        this.textIsCustomFormat.value =
            sharedPreferences.getBoolean(TEXT_IS_CUSTOM_FORMAT_KEY, Defaults.textIsCustomFormat)
        this.textCustomFormat.value = sharedPreferences.getString(TEXT_CUSTOM_FORMAT_KEY, "") ?: ""

        this.dayStart.value = DayStart(
            sharedPreferences.getInt(DAY_START_HOUR_KEY, 0),
            sharedPreferences.getInt(DAY_START_MINUTE_KEY, 0)
        )

        this.calendarCalculationMethod.value =
            sharedPreferences.getString(
                CALENDAR_CALCULATION_METHOD_KEY,
                Defaults.calendarCalculationMethod
            )
                ?: Defaults.calendarCalculationMethod

        this.dayOffset.value = sharedPreferences.getInt(DAY_OFFSET_KEY, 0)
    }

    fun save(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)
        sharedPreferences.edit(commit = true) {
            putString(BG_COLOR_MODE_KEY, this@Preferences.bgColorMode.value.toString())
            putInt(BG_CUSTOM_COLOR_KEY, this@Preferences.bgCustomColor.value)

            putString(TEXT_COLOR_MODE_KEY, this@Preferences.textColorMode.value.toString())
            putInt(TEXT_CUSTOM_COLOR_KEY, this@Preferences.textCustomColor.value)

            putFloat(TEXT_SIZE_KEY, this@Preferences.textSize.value)

            putBoolean(TEXT_SHADOW_KEY, this@Preferences.textShadow.value)

            putString(TEXT_FORMAT_KEY, this@Preferences.textFormat.value)
            putBoolean(TEXT_IS_CUSTOM_FORMAT_KEY, this@Preferences.textIsCustomFormat.value)
            putString(TEXT_CUSTOM_FORMAT_KEY, this@Preferences.textCustomFormat.value)

            putInt(DAY_START_HOUR_KEY, this@Preferences.dayStart.value.hour)
            putInt(DAY_START_MINUTE_KEY, this@Preferences.dayStart.value.minute)

            putString(
                CALENDAR_CALCULATION_METHOD_KEY,
                this@Preferences.calendarCalculationMethod.value
            )

            putInt(DAY_OFFSET_KEY, this@Preferences.dayOffset.value)
        }
    }

    val Dark = Color(0xFF151515)

    @Composable
    fun getTextColor(context: Context): Color {
        return when {
            this.textColorMode.value == ColorMode.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> GlanceTheme.colors.primary.getColor(
                context
            )

            this.textColorMode.value == ColorMode.System && context.isDark() -> Dark
            this.textColorMode.value == ColorMode.System && !context.isDark() -> Color.White
            this.textColorMode.value == ColorMode.Dark -> Dark
            this.textColorMode.value == ColorMode.Light -> Color.White
            this.textColorMode.value == ColorMode.Custom -> Color(this.textCustomColor.value)
            else -> Color.White
        }

    }

    @SuppressLint("RestrictedApi")
    @Composable
    fun getBgColor(context: Context): ColorProvider {
        return when {
            this.bgColorMode.value == ColorMode.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> GlanceTheme.colors.widgetBackground
            this.bgColorMode.value == ColorMode.System && context.isDark() -> ColorProvider(Dark)
            this.bgColorMode.value == ColorMode.System && !context.isDark() -> ColorProvider(Color.White)
            this.bgColorMode.value == ColorMode.Dark -> ColorProvider(Dark)
            this.bgColorMode.value == ColorMode.Light -> ColorProvider(Color.White)
            this.bgColorMode.value == ColorMode.Custom -> ColorProvider(Color(this.bgCustomColor.value))
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

    fun migratePreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)

        if (!sharedPreferences.contains(TEXT_FORMAT_KEY)) {
            sharedPreferences.getString("LANG", null)?.let {
                sharedPreferences.edit(commit = true) {
                    when (it) {
                        "Arabic" -> putString(
                            TEXT_FORMAT_KEY,
                            "d MMMM yyyy"
                        )

                        "English" -> putString(
                            TEXT_FORMAT_KEY,
                            "en-GB{d MMMM yyyy}"
                        )

                        else -> {}
                    }

                }
            }
        }

        if (!sharedPreferences.contains(TEXT_COLOR_MODE_KEY)) {
            sharedPreferences.getString("THEME", null).let {
                sharedPreferences.edit(commit = true) {
                    putString(
                        TEXT_COLOR_MODE_KEY,
                        it
                    )
                }
            }
        }

        if (!sharedPreferences.contains(TEXT_CUSTOM_COLOR_KEY)) {
            sharedPreferences.getInt("CUSTOM_COLOR", -1).let {
                if (it != -1) {
                    sharedPreferences.edit(commit = true) {
                        putInt(
                            TEXT_CUSTOM_COLOR_KEY,
                            it
                        )
                    }
                }
            }
        }

        if (!sharedPreferences.contains(TEXT_SIZE_KEY)) {
            sharedPreferences.getInt("CUSTOM_TEXT_SIZE", -1).let {
                if (it != -1) {
                    sharedPreferences.edit(commit = true) {
                        putInt(
                            TEXT_SIZE_KEY,
                            it
                        )
                    }
                }
            }
        }

        if (sharedPreferences.contains(TEXT_SHADOW_KEY)) {
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

}

data class DayStart(val hour: Int, val minute: Int) {
    override fun toString(): String {
        val localTime = LocalTime.of(
            hour, minute
        )
        return localTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
    }
}
