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
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import me.amrbashir.hijriwidget.widget.HijriWidget

/**
 * Version of the stored config, independent of the app version.
 * Bump it and add a migration step in [migrateConfig] whenever the stored config changes shape.
 *
 * - v1: global preferences in [LEGACY_PREF]
 * - v2: per-widget preferences, stored in each widget's Glance state
 */
private const val CONFIG_VERSION = 2

/** Name of the [SharedPreferences] file holding app-level state, e.g. [CONFIG_VERSION] */
private const val APP_PREF = "HijriWidgetApp"
private const val CONFIG_VERSION_KEY = "CONFIG_VERSION"

/** Name of the [SharedPreferences] file used before preferences became per-widget */
private const val LEGACY_PREF = "HijriWidgetPref"

/** Migrates the stored config to [CONFIG_VERSION], must finish before anything reads preferences */
suspend fun migrateConfig(context: Context) {
	val appPrefs = context.getSharedPreferences(APP_PREF, 0)
	val legacyPrefs = context.getSharedPreferences(LEGACY_PREF, 0)

	var version =
		when {
			appPrefs.contains(CONFIG_VERSION_KEY) -> appPrefs.getInt(CONFIG_VERSION_KEY, CONFIG_VERSION)
			// Versioning was added in v2, so existing global preferences are v1
			legacyPrefs.all.isNotEmpty() -> 1
			// New install
			else -> CONFIG_VERSION
		}

	// Config is from a newer app version (i.e. the app was downgraded), leave it untouched
	if (version > CONFIG_VERSION) return

	while (version < CONFIG_VERSION) {
		when (version) {
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
private suspend fun migrateV1ToV2(
	context: Context,
	legacyPrefs: SharedPreferences,
) {
	val ids = HijriWidget.glanceIds(context)
	if (ids.isNotEmpty()) {
		// v1 keys and value types match the current ones, so parsing them validates values
		val legacy = PreferencesManager.fromPreferences(legacyPrefs.toPreferences())

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
