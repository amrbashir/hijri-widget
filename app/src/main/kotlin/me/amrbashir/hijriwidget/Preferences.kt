package me.amrbashir.hijriwidget

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

private const val PREF = "HijriWidgetPref"
private const val LANG_KEY = "LANG"
private const val THEME_KEY = "THEME"
private const val CUSTOM_COLOR_KEY = "CUSTOM_COLOR"
private const val SHADOW_KEY = "SHADOW"
private const val IS_CUSTOM_TEXT_SIZE_KEY = "IS_CUSTOM_TEXT_SIZE"
private const val CUSTOM_TEXT_SIZE_KEY = "CUSTOM_TEXT_SIZE"

object Preferences {
    val language: MutableState<SupportedLanguage> = mutableStateOf(SupportedLanguage.Arabic)
    val theme: MutableState<SupportedTheme> =
        mutableStateOf(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) SupportedTheme.Dynamic else SupportedTheme.System)
    val color: MutableState<Int> = mutableIntStateOf(Color.White.toArgb())
    val customColor: MutableState<Int> = mutableIntStateOf(Color.White.toArgb())
    val shadow: MutableState<Boolean> = mutableStateOf(true)
    val isCustomTextSize: MutableState<Boolean> = mutableStateOf(false)
    val customTextSize: MutableState<Float> = mutableFloatStateOf(22F)


    fun load(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)

        val lang = sharedPreferences.getString(LANG_KEY, "Arabic") ?: "Arabic"
        this.language.value = SupportedLanguage.valueOf(lang)

        val theme = sharedPreferences.getString(THEME_KEY, "Dynamic") ?: "Dynamic"
        this.theme.value = SupportedTheme.valueOf(theme)

        this.customColor.value = sharedPreferences.getInt(CUSTOM_COLOR_KEY, Color.White.toArgb())

        this.shadow.value = sharedPreferences.getBoolean(SHADOW_KEY, true)
        this.isCustomTextSize.value = sharedPreferences.getBoolean(IS_CUSTOM_TEXT_SIZE_KEY, false)

        this.customTextSize.value = sharedPreferences.getFloat(CUSTOM_TEXT_SIZE_KEY, 22F)

        this.updateColor(context)
    }

    fun save(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)
        sharedPreferences.edit()?.run {
            putString(LANG_KEY, this@Preferences.language.value.toString())
            putString(THEME_KEY, this@Preferences.theme.value.toString())
            putInt(CUSTOM_COLOR_KEY, this@Preferences.customColor.value)
            putBoolean(SHADOW_KEY, this@Preferences.shadow.value)
            putBoolean(IS_CUSTOM_TEXT_SIZE_KEY, this@Preferences.isCustomTextSize.value)
            putFloat(CUSTOM_TEXT_SIZE_KEY, this@Preferences.customTextSize.value)
            commit()
        }
    }

    fun updateColor(context: Context) {
        val newColor = when {
            this.theme.value == SupportedTheme.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (context.isDark()) dynamicDarkColorScheme(context).primary
                else dynamicLightColorScheme(context).primary
            }

            this.theme.value == SupportedTheme.System && context.isDark() -> darkScheme.surface
            this.theme.value == SupportedTheme.System && !context.isDark() -> lightScheme.surface
            this.theme.value == SupportedTheme.Dark -> darkScheme.surface
            this.theme.value == SupportedTheme.Custom -> Color(this.customColor.value)
            else -> lightScheme.surface
        }

        this.color.value = newColor.toArgb()
    }
}


fun Context.isDark(): Boolean {
    return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}