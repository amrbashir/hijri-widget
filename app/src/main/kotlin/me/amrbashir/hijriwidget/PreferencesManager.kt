package me.amrbashir.hijriwidget

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.edit
import androidx.glance.GlanceTheme
import androidx.glance.unit.ColorProvider

const val DARK = 0xFF151515
private const val PREF = "HijriWidgetPref"

class Preference<T>(
    val key: String,
    val default: T,
    loader: SharedPreferences.(String, T) -> T,
    saver: SharedPreferences.Editor.(String, T) -> Unit,
) {
    private val _value = mutableStateOf(default)
    var value: T
        get() = _value.value
        set(v) {
            _value.value = v
        }

    val load = { prefs: SharedPreferences ->
        this.value = loader(prefs, this.key, this.default)
    }

    val save: SharedPreferences.Editor.() -> Unit = {
        saver(this, this@Preference.key, this@Preference.value)
    }

    fun reset() {
        value = default
    }
}

class PreferencesManager {
    private val _preferences: MutableList<Preference<Any>> = mutableListOf()


    var bgColorMode = enumPreference(
        key = "BG_COLOR_MODE",
        default = ColorMode.Transparent,
    )

    val bgCustomColor = intPreference(
        key = "BG_CUSTOM_COLOR",
        default = Color.Transparent.toArgb(),
    )

    val textColorMode = enumPreference(
        key = "TEXT_COLOR_MODE",
        default = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ColorMode.Dynamic else ColorMode.System,
    )

    val textCustomColor = intPreference(
        key = "TEXT_CUSTOM_COLOR",
        default = Color.White.toArgb(),
    )

    val textSize = floatPreference(
        key = "TEXT_SIZE",
        default = 22F,
    )

    val textShadow = booleanPreference(
        key = "TEXT_SHADOW",
        default = true
    )

    val dateFormat = stringPreference(
        key = "DATE_FORMAT",
        default = "d MMMM yyyy"
    )

    val dateIsCustomFormat = booleanPreference(
        key = "DATE_IS_CUSTOM_FORMAT",
        default = false
    )

    val dateCustomFormat = stringPreference(
        key = "DATE_CUSTOM_FORMAT",
        default = ""
    )

    val dayStart = intPreference(
        key = "DAY_START",
        default = 0, // 12:00 AM
    )

    val calendarCalculationMethod = enumPreference(
        key = "CALENDAR_CALCULATION_METHOD",
        default = HijriDateCalculationMethod.ISLAMIC_UMALQURA
    )

    val dayOffset = intPreference(
        key = "DAY_OFFSET",
        default = 0
    )

    @Composable
    fun getTextColor(context: Context): Color {
        return when {
            this.textColorMode.value == ColorMode.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> GlanceTheme.colors.primary.getColor(
                context
            )

            this.textColorMode.value == ColorMode.System && context.isDark() -> Color(DARK)
            this.textColorMode.value == ColorMode.System && !context.isDark() -> Color.White
            this.textColorMode.value == ColorMode.Dark -> Color(DARK)
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
            this.bgColorMode.value == ColorMode.System && context.isDark() -> ColorProvider(
                Color(
                    DARK
                )
            )

            this.bgColorMode.value == ColorMode.System && !context.isDark() -> ColorProvider(Color.White)
            this.bgColorMode.value == ColorMode.Dark -> ColorProvider(Color(DARK))
            this.bgColorMode.value == ColorMode.Light -> ColorProvider(Color.White)
            this.bgColorMode.value == ColorMode.Custom -> ColorProvider(Color(this.bgCustomColor.value))
            else -> ColorProvider(Color.Transparent)
        }
    }

    companion object {
        fun load(context: Context): PreferencesManager {
            val sharedPreferences = context.getSharedPreferences(PREF, 0)

            migratePreferences(sharedPreferences)

            val manager = PreferencesManager()
            for (pref in manager._preferences) {
                pref.load(sharedPreferences)
            }
            return manager
        }
    }

    fun save(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)
        sharedPreferences.edit(commit = true) {
            for (pref in _preferences) {
                pref.save(this)
            }
        }
    }

    fun reset() {
        for (pref in _preferences) {
            pref.reset()
        }
    }

    fun <T> preference(
        key: String,
        default: T,
        loader: SharedPreferences.(String, T) -> T,
        saver: SharedPreferences.Editor.(String, T) -> Unit,
    ): Preference<T> {
        val p = Preference(
            key,
            default,
            loader,
            saver
        )
        @Suppress("UNCHECKED_CAST")
        _preferences.add(p as Preference<Any>)
        return p
    }

    fun intPreference(
        key: String,
        default: Int,
    ) = preference(
        key = key,
        default = default,
        loader = SharedPreferences::getInt,
        saver = SharedPreferences.Editor::putInt,
    )

    fun floatPreference(
        key: String,
        default: Float,
    ) = preference(
        key = key,
        default = default,
        loader = SharedPreferences::getFloat,
        saver = SharedPreferences.Editor::putFloat,
    )

    fun stringPreference(
        key: String,
        default: String,
    ) = preference(
        key = key,
        default = default,
        loader = { k, def ->
            this.getString(k, def) ?: def
        },
        saver = SharedPreferences.Editor::putString,
    )

    fun booleanPreference(
        key: String,
        default: Boolean,
    ) = preference(
        key = key,
        default = default,
        loader = SharedPreferences::getBoolean,
        saver = SharedPreferences.Editor::putBoolean,
    )

    inline fun <reified T : Enum<T>> enumPreference(
        key: String,
        default: T,
    ) = preference(
        key = key,
        default = default,
        loader = { k, def ->
            val v = this.getString(k, def.toString()) ?: def.toString()
            enumValueOf<T>(v)
        },
        saver = { k, v ->
            putString(k, v.toString())
        }
    )


}

// Keys used for old prefs migrations
const val TEXT_COLOR_MODE_KEY = "TEXT_COLOR_MODE"
const val THEME_KEY = "THEME"
const val TEXT_CUSTOM_COLOR_KEY = "TEXT_CUSTOM_COLOR"
const val CUSTOM_COLOR_KEY = "CUSTOM_COLOR"
const val TEXT_SIZE_KEY = "TEXT_SIZE"
const val CUSTOM_TEXT_SIZE_KEY = "CUSTOM_TEXT_SIZE"
const val TEXT_SHADOW_KEY = "TEXT_SHADOW"
const val SHADOW_KEY = "SHADOW"
const val DATE_FORMAT_KEY = "DATE_FORMAT"
const val LANG_KEY = "LANG"
const val DAY_START_KEY = "DAY_START"
const val DAY_START_HOUR_KEY = "DAY_START_HOUR"
const val DAY_START_MINUTE_KEY = "DAY_START_MINUTE"
const val CALENDAR_CALCULATION_METHOD_KEY = "CALENDAR_CALCULATION_METHOD"
fun migratePreferences(sharedPreferences: SharedPreferences) {
    sharedPreferences.edit(commit = true) {
        if (!sharedPreferences.contains(DATE_FORMAT_KEY)) {
            sharedPreferences.getString(LANG_KEY, null)?.let {
                when (it) {
                    "Arabic" -> putString(
                        DATE_FORMAT_KEY,
                        "d MMMM yyyy"
                    )

                    "English" -> putString(
                        DATE_FORMAT_KEY,
                        "en-GB{d MMMM yyyy}"
                    )

                    else -> {}
                }

            }
        }

        if (!sharedPreferences.contains(TEXT_COLOR_MODE_KEY)) {
            sharedPreferences.getString(THEME_KEY, null)?.let {
                putString(
                    TEXT_COLOR_MODE_KEY,
                    it
                )
            }
        }

        if (!sharedPreferences.contains(TEXT_CUSTOM_COLOR_KEY)) {
            val color = sharedPreferences.getInt(CUSTOM_COLOR_KEY, -1)
            if (color != -1) {
                putInt(
                    TEXT_CUSTOM_COLOR_KEY,
                    color
                )
            }
        }

        if (!sharedPreferences.contains(TEXT_SIZE_KEY)) {
            val size = sharedPreferences.getFloat(CUSTOM_TEXT_SIZE_KEY, -1F)
            if (size != -1F) {
                putFloat(
                    TEXT_SIZE_KEY,
                    size
                )
            }
        }

        if (!sharedPreferences.contains(TEXT_SHADOW_KEY)) {
            putBoolean(
                TEXT_SHADOW_KEY,
                sharedPreferences.getBoolean(SHADOW_KEY, true)
            )
        }

        if (!sharedPreferences.contains(DAY_START_KEY)) {
            val hour = sharedPreferences.getInt(DAY_START_HOUR_KEY, -1)
            val minute = sharedPreferences.getInt(DAY_START_MINUTE_KEY, -1)
            if (hour != -1 && minute != -1) {
                putInt(
                    DAY_START_KEY,
                    hour * 60 + minute
                )
            }
        }

        if (sharedPreferences.contains(CALENDAR_CALCULATION_METHOD_KEY)) {
            val method = sharedPreferences.getString(CALENDAR_CALCULATION_METHOD_KEY, null)
            if (!method.isNullOrEmpty()) {
                try {
                    HijriDateCalculationMethod.valueOf(method)
                    // if success, no need for migrations
                } catch (_: Exception) {
                    val methodAsEnum = HijriDateCalculationMethod.fromId(method)
                    putString(CALENDAR_CALCULATION_METHOD_KEY, methodAsEnum.toString())
                }
            }

        }

    }
}
