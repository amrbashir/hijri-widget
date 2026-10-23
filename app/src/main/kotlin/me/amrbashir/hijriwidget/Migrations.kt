package me.amrbashir.hijriwidget

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import me.amrbashir.hijriwidget.widget.HijriWidget

/** Name of the [SharedPreferences] file holding app-level state, e.g. [CONFIG_VERSION] */
private const val APP_PREF = "HijriWidgetApp"
private const val CONFIG_VERSION_KEY = "CONFIG_VERSION"

/** Name of the [SharedPreferences] file used before preferences became per-widget */
private const val LEGACY_PREF = "HijriWidgetPref"

/**
 * Version of the stored config, independent of the app version.
 * Bump it and add a migration step in [migrateConfig] whenever the stored config changes shape.
 *
 * - v0: global preferences in [LEGACY_PREF], with old keys (e.g. `LANG`, `THEME`)
 * - v1: global preferences in [LEGACY_PREF], with current keys
 * - v2: per-widget preferences, stored in each widget's Glance state
 */
const val CONFIG_VERSION = 2

/** Migrates the stored config to [CONFIG_VERSION], must finish before anything reads preferences */
suspend fun migrateConfig(context: Context) {
    val appPrefs = context.getSharedPreferences(APP_PREF, 0)
    val legacyPrefs = context.getSharedPreferences(LEGACY_PREF, 0)

    var version = when {
        appPrefs.contains(CONFIG_VERSION_KEY) -> appPrefs.getInt(CONFIG_VERSION_KEY, CONFIG_VERSION)
        // Old keys are kept after v0 -> v1, so v0 and v1 can't be told apart,
        // but v0 -> v1 is a no-op for v1 config
        legacyPrefs.all.isNotEmpty() -> 0
        // New install
        else -> CONFIG_VERSION
    }

    // Config is from a newer app version (i.e. the app was downgraded), leave it untouched
    if (version > CONFIG_VERSION) return

    while (version < CONFIG_VERSION) {
        when (version) {
            0 -> migrateV0ToV1(legacyPrefs)
            1 -> migrateV1ToV2(context, legacyPrefs)
        }
        version++
        // Saved after each step, so an interrupted migration resumes where it stopped
        appPrefs.edit(commit = true) { putInt(CONFIG_VERSION_KEY, version) }
    }

    // New install, nothing to migrate
    if (!appPrefs.contains(CONFIG_VERSION_KEY)) {
        appPrefs.edit(commit = true) { putInt(CONFIG_VERSION_KEY, version) }
    }
}

// ======== //
// v1 -> v2 //
// ======== //

/**
 * Copies the global preferences into every placed widget that has no state yet.
 * If no widget is placed, they are simply dropped and the app continues as a new install.
 */
private suspend fun migrateV1ToV2(context: Context, legacyPrefs: SharedPreferences) {
    val ids = GlanceAppWidgetManager(context).getGlanceIds(HijriWidget::class.java)
    if (ids.isNotEmpty()) {
        // v1 keys and value types match the current ones, so parsing them validates
        // values and drops leftover keys from v0
        val legacy = PreferencesManager.load(legacyPrefs.toPreferences())

        for (id in ids) {
            val state = getAppWidgetState(context, PreferencesGlanceStateDefinition, id)
            if (state.asMap().isEmpty()) legacy.save(context, id)
        }
    }

    legacyPrefs.edit(commit = true) { clear() }
}

private fun SharedPreferences.toPreferences(): Preferences {
    val prefs = mutablePreferencesOf()
    for ((key, value) in all) {
        when (value) {
            is Int -> prefs[intPreferencesKey(key)] = value
            is Float -> prefs[floatPreferencesKey(key)] = value
            is Boolean -> prefs[booleanPreferencesKey(key)] = value
            is String -> prefs[stringPreferencesKey(key)] = value
        }
    }
    return prefs
}

// ======== //
// v0 -> v1 //
// ======== //

private const val TEXT_COLOR_MODE_KEY = "TEXT_COLOR_MODE"
private const val THEME_KEY = "THEME"
private const val TEXT_CUSTOM_COLOR_KEY = "TEXT_CUSTOM_COLOR"
private const val CUSTOM_COLOR_KEY = "CUSTOM_COLOR"
private const val TEXT_SIZE_KEY = "TEXT_SIZE"
private const val CUSTOM_TEXT_SIZE_KEY = "CUSTOM_TEXT_SIZE"
private const val TEXT_SHADOW_KEY = "TEXT_SHADOW"
private const val SHADOW_KEY = "SHADOW"
private const val DATE_FORMAT_KEY = "DATE_FORMAT"
private const val LANG_KEY = "LANG"
private const val DAY_START_KEY = "DAY_START"
private const val DAY_START_HOUR_KEY = "DAY_START_HOUR"
private const val DAY_START_MINUTE_KEY = "DAY_START_MINUTE"
private const val CALENDAR_CALCULATION_METHOD_KEY = "CALENDAR_CALCULATION_METHOD"

/** Converts old keys to the current ones, e.g. `LANG` into `DATE_FORMAT` */
private fun migrateV0ToV1(sharedPreferences: SharedPreferences) {
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
                runCatching {
                    HijriDateCalculationMethod.valueOf(method)
                    // if success, no need for migrations
                }.onFailure(logException).getOrElse {
                    val methodAsEnum = HijriDateCalculationMethod.fromId(method)
                    putString(CALENDAR_CALCULATION_METHOD_KEY, methodAsEnum.toString())
                }
            }

        }

    }
}

