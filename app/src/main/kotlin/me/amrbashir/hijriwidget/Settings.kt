package me.amrbashir.hijriwidget

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color
import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import me.amrbashir.hijriwidget.settings.SupportedLanguage
import me.amrbashir.hijriwidget.settings.SupportedTheme
import me.amrbashir.hijriwidget.settings.darkScheme
import me.amrbashir.hijriwidget.settings.lightScheme

private const val PREF = "HijriWidgetSettingsPref"
private const val LANG_KEY = "LANG"
private const val THEME_KEY = "THEME"
private const val COLOR_KEY = "COLOR"

object Settings {
    val language: MutableState<SupportedLanguage> = mutableStateOf(SupportedLanguage.Arabic)
    val theme: MutableState<SupportedTheme> =
        mutableStateOf(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) SupportedTheme.Dynamic else SupportedTheme.System)
    val color: MutableState<Int> = mutableIntStateOf(Color.WHITE)

    fun load(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)
        this.language.value =
            SupportedLanguage.valueOf(sharedPreferences.getString(LANG_KEY, "Arabic") ?: "Arabic")
        this.theme.value =
            SupportedTheme.valueOf(sharedPreferences.getString(THEME_KEY, "Dynamic") ?: "Dynamic")

        if (sharedPreferences.contains(COLOR_KEY)) {
            this.color.value = sharedPreferences.getInt(COLOR_KEY, Color.WHITE)
        } else {
            this.updateColor(context)
        }
    }

    fun save(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)
        sharedPreferences.edit()?.run {
            putString(LANG_KEY, this@Settings.language.value.toString())
            putString(THEME_KEY, this@Settings.theme.value.toString())
            putInt(COLOR_KEY, this@Settings.color.value)
            commit()
        }
    }

    fun updateColor(context: Context) {

        val newColor = when {
            this.theme.value == SupportedTheme.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (context.isDark()) dynamicDarkColorScheme(context).surfaceTint else dynamicLightColorScheme(
                    context
                ).surfaceTint
            }

            this.theme.value == SupportedTheme.System && context.isDark() -> darkScheme.surface
            this.theme.value == SupportedTheme.System && !context.isDark() -> lightScheme.surface
            this.theme.value == SupportedTheme.Dark -> darkScheme.surface
            else -> lightScheme.surface
        }

        this.color.value = newColor.toArgb()
    }
}


fun Context.isDark(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
}