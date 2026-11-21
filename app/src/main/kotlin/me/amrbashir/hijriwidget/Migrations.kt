package me.amrbashir.hijriwidget

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import me.amrbashir.hijriwidget.widget.HijriWidget

/**
 * Version of the stored config, independent of the app version.
 * Bump it and add a step in [migrateConfig] whenever the stored config changes shape.
 *
 * - v1: global preferences in [LEGACY_PREF]
 * - v2: per-widget preferences, explicit Arabic date locale, TEXT_SIZE renamed to FONT_SIZE, and explicit dynamic color roles
 * - v3: PADDING_START/END renamed to PADDING_LEFT/RIGHT
 */
private const val CONFIG_VERSION = 3

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
			2 -> migrateV2ToV3(context)
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


	if (ids.isEmpty()) {
		legacyPrefs.edit(commit = true) { clear() }
		return
	}

	val legacy =
		legacyPrefs.toPreferences().toMutablePreferences().apply {
			rename(floatPreferencesKey("TEXT_SIZE"), floatPreferencesKey("FONT_SIZE"))

			val defaults = v2Defaults(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
			for ((key, value) in defaults.asMap()) {
				if (!contains(key)) {
					@Suppress("UNCHECKED_CAST")
					this[key as Preferences.Key<Any>] = value
				}
			}

			val textColorKey = stringPreferencesKey("TEXT_COLOR_MODE")
			val bgColorKey = stringPreferencesKey("BG_COLOR_MODE")
			if (this[textColorKey] == "System") this[textColorKey] = "DeviceTheme"
			if (this[bgColorKey] == "System") this[bgColorKey] = "DeviceTheme"
			if (this[textColorKey] == "Dynamic") this[textColorKey] = "DynamicPrimary"
			if (this[bgColorKey] == "Dynamic") this[bgColorKey] = "DynamicPrimaryContainer"
			for (key in listOf(textColorKey, bgColorKey)) {
				if (this[key] !in v2ColorModes) this[key] = checkNotNull(defaults[key])
			}
			for ((key, allowed) in v2EnumValues) {
				if (this[key] !in allowed) this[key] = checkNotNull(defaults[key])
			}

			val formatKey = stringPreferencesKey("DATE_FORMAT")
			val customFormatKey = stringPreferencesKey("DATE_CUSTOM_FORMAT")
			val isCustomKey = booleanPreferencesKey("DATE_IS_CUSTOM_FORMAT")
			this[formatKey] = preserveArabicDateFormat(checkNotNull(this[formatKey]))
			this[customFormatKey] = preserveArabicDateFormat(checkNotNull(this[customFormatKey]))
			if (this[isCustomKey] == false && this[formatKey] !in v2DatePresets) {
				this[customFormatKey] = checkNotNull(this[formatKey])
				this[isCustomKey] = true
			}
		}

	for (id in ids) {
		updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { state ->
			if (state.asMap().isEmpty()) legacy else state
		}
	}

	legacyPrefs.edit(commit = true) { clear() }
}

// ======== //
// v2 -> v3 //
// ======== //

private suspend fun migrateV2ToV3(context: Context) {
	for (id in HijriWidget.glanceIds(context)) {
		updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
			prefs.toMutablePreferences().apply {
				rename(intPreferencesKey("PADDING_START"), intPreferencesKey("PADDING_LEFT"))
				rename(intPreferencesKey("PADDING_END"), intPreferencesKey("PADDING_RIGHT"))
			}
		}
	}
}

private fun preserveArabicDateFormat(format: String): String =
	parseDateFormat(format).joinToString("") { segment ->
		"${segment.langCode ?: "ar-SA"}{${segment.format}}"
	}

// Snapshots of v2.0.0's stored defaults and accepted values, not aliases to current UI settings.
private fun v2Defaults(supportsDynamicColors: Boolean): Preferences =
	preferencesOf(
		stringPreferencesKey("BG_COLOR_MODE") to "Transparent",
		intPreferencesKey("BG_CUSTOM_COLOR") to 0,
		stringPreferencesKey("TEXT_COLOR_MODE") to
			if (supportsDynamicColors) "DynamicPrimary" else "DeviceTheme",
		intPreferencesKey("TEXT_CUSTOM_COLOR") to -1,
		floatPreferencesKey("FONT_SIZE") to 22F,
		stringPreferencesKey("TEXT_FONT_FAMILY") to "",
		floatPreferencesKey("TEXT_WEIGHT") to 400F,
		stringPreferencesKey("HORIZONTAL_ALIGNMENT") to "Center",
		stringPreferencesKey("VERTICAL_ALIGNMENT") to "Center",
		intPreferencesKey("PADDING_START") to 0,
		intPreferencesKey("PADDING_TOP") to 0,
		intPreferencesKey("PADDING_END") to 0,
		intPreferencesKey("PADDING_BOTTOM") to 0,
		booleanPreferencesKey("TEXT_SHADOW") to true,
		stringPreferencesKey("DATE_FORMAT") to "d MMMM yyyy",
		booleanPreferencesKey("DATE_IS_CUSTOM_FORMAT") to false,
		stringPreferencesKey("DATE_CUSTOM_FORMAT") to "",
		intPreferencesKey("DAY_START") to 0,
		stringPreferencesKey("CALENDAR_CALCULATION_METHOD") to "ISLAMIC_UMALQURA",
		intPreferencesKey("DAY_OFFSET") to 0,
	)

private val v2ColorModes =
	setOf(
		"Dynamic",
		"DynamicPrimary",
		"DynamicSecondary",
		"DynamicTertiary",
		"DynamicPrimaryContainer",
		"DynamicSecondaryContainer",
		"DynamicTertiaryContainer",
		"DeviceTheme",
		"Dark",
		"Light",
		"Transparent",
		"Custom",
	)

private val v2EnumValues =
	mapOf(
		stringPreferencesKey("HORIZONTAL_ALIGNMENT") to setOf("Left", "Center", "Right"),
		stringPreferencesKey("VERTICAL_ALIGNMENT") to setOf("Top", "Center", "Bottom"),
		stringPreferencesKey("CALENDAR_CALCULATION_METHOD") to
			setOf("ISLAMIC", "ISLAMIC_UMALQURA", "ISLAMIC_CIVIL"),
	)

private val v2DatePresets =
	setOf(
		"d MMMM yyyy",
		"d / MM / yy",
		"ar-SA{d MMMM yyyy}",
		"ar-SA{d / MM / yy}",
		"en-GB{d MMMM yyyy}",
		"en-GB{d / MM / yy}",
		"en-GB{d} ar-SA{MMMM} en-GB{yyyy}",
	)

private fun SharedPreferences.toPreferences(): Preferences {
	val prefs = mutablePreferencesOf()
	for ((key, value) in all) {
		when (value) {
			is Int -> prefs[intPreferencesKey(key)] = value
			is Long -> prefs[longPreferencesKey(key)] = value
			is Float -> prefs[floatPreferencesKey(key)] = value
			is Boolean -> prefs[booleanPreferencesKey(key)] = value
			is String -> prefs[stringPreferencesKey(key)] = value
			is Set<*> -> {
				@Suppress("UNCHECKED_CAST")
				prefs[stringSetPreferencesKey(key)] = value as Set<String>
			}
		}
	}
	return prefs
}

private fun <T> MutablePreferences.rename(
	oldKey: Preferences.Key<T>,
	newKey: Preferences.Key<T>,
) {
	val oldValue = this[oldKey]
	if (this[newKey] == null && oldValue != null) this[newKey] = oldValue
	remove(oldKey)
}
