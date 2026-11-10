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
 * - v2: per-widget preferences, explicit Arabic date locale, TEXT_SIZE renamed to FONT_SIZE, and explicit dynamic color roles
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
		// Convert legacy keys and enum values before parsing the current preferences.
		val legacy =
			PreferencesManager.fromPreferences(
				legacyPrefs.toPreferences().migrateFontSize().migrateColorModes(),
			)
		legacy.dateFormat.value = preserveArabicDateFormat(legacy.dateFormat.value)
		legacy.dateCustomFormat.value = preserveArabicDateFormat(legacy.dateCustomFormat.value)
		if (!legacy.dateIsCustomFormat.value && legacy.dateFormat.value !in DATE_FORMAT_PRESETES) {
			legacy.dateCustomFormat.value = legacy.dateFormat.value
			legacy.dateIsCustomFormat.value = true
		}

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

/** Pins old bare segments to Arabic before their default changes to the device locale. */
private fun preserveArabicDateFormat(format: String): String =
	parseDateFormat(format).joinToString("") { segment ->
		"${segment.langCode ?: "ar-SA"}{${segment.format}}"
	}

private fun Preferences.migrateFontSize(): Preferences =
	toMutablePreferences().apply {
		val oldKey = floatPreferencesKey("TEXT_SIZE")
		val newKey = floatPreferencesKey("FONT_SIZE")
		val oldSize = this[oldKey]
		if (this[newKey] == null && oldSize != null) this[newKey] = oldSize
		remove(oldKey)
	}

private fun Preferences.migrateColorModes(): Preferences =
	toMutablePreferences().apply {
		val textKey = stringPreferencesKey("TEXT_COLOR_MODE")
		val backgroundKey = stringPreferencesKey("BG_COLOR_MODE")
		if (this[textKey] == "System") this[textKey] = ColorMode.DeviceTheme.name
		if (this[backgroundKey] == "System") this[backgroundKey] = ColorMode.DeviceTheme.name
		if (this[textKey] == "Dynamic") this[textKey] = ColorMode.DynamicPrimary.name
		if (this[backgroundKey] == "Dynamic") this[backgroundKey] = ColorMode.DynamicPrimaryContainer.name
	}
