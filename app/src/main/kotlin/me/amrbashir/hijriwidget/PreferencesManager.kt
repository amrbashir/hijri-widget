package me.amrbashir.hijriwidget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.unit.ColorProvider
import me.amrbashir.hijriwidget.widget.HijriWidget

val Color.Companion.Dark: Color get() = Color(0xFF151515)

class PreferencesManager {
	private val _preferences: MutableList<Preference<Any, Any>> = mutableListOf()

	val bgColorMode =
		enumPreference(
			key = "BG_COLOR_MODE",
			default = ColorMode.Transparent,
		)

	val bgCustomColor =
		intPreference(
			key = "BG_CUSTOM_COLOR",
			default = Color.Transparent.toArgb(),
		)

	val textColorMode =
		enumPreference(
			key = "TEXT_COLOR_MODE",
			default =
				if (Build.VERSION.SDK_INT >=
					Build.VERSION_CODES.S
				) {
					ColorMode.Dynamic
				} else {
					ColorMode.System
				},
		)

	val textCustomColor =
		intPreference(
			key = "TEXT_CUSTOM_COLOR",
			default = Color.White.toArgb(),
		)

	val textSize =
		floatPreference(
			key = "TEXT_SIZE",
			default = 22F,
		)

	val textShadow =
		booleanPreference(
			key = "TEXT_SHADOW",
			default = true,
		)

	val dateFormat =
		stringPreference(
			key = "DATE_FORMAT",
			default = "d MMMM yyyy",
		)

	val dateIsCustomFormat =
		booleanPreference(
			key = "DATE_IS_CUSTOM_FORMAT",
			default = false,
		)

	val dateCustomFormat =
		stringPreference(
			key = "DATE_CUSTOM_FORMAT",
			default = "",
		)

	val dayStart =
		intPreference(
			key = "DAY_START",
			default = 0, // 12:00 AM
		)

	val calendarCalculationMethod =
		enumPreference(
			key = "CALENDAR_CALCULATION_METHOD",
			default = HijriDateCalculationMethod.ISLAMIC_UMALQURA,
		)

	val dayOffset =
		intPreference(
			key = "DAY_OFFSET",
			default = 0,
		)

	@Composable
	fun getTextColor(context: Context): Color =
		when (this.textColorMode.value) {
			ColorMode.Dynamic if Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
				GlanceTheme.colors.primary.getColor(
					context,
				)

			ColorMode.System if isSystemInDarkTheme() -> Color.Dark
			ColorMode.System if !isSystemInDarkTheme() -> Color.White
			ColorMode.Dark -> Color.Dark
			ColorMode.Light -> Color.White
			ColorMode.Custom -> Color(this.textCustomColor.value)
			else -> Color.White
		}

	@SuppressLint("RestrictedApi")
	@Composable
	fun getBgColor(): ColorProvider =
		when (this.bgColorMode.value) {
			ColorMode.Dynamic if Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
				GlanceTheme.colors.widgetBackground

			ColorMode.System if isSystemInDarkTheme() ->
				ColorProvider(
					Color.Dark,
				)

			ColorMode.System if !isSystemInDarkTheme() ->
				ColorProvider(
					Color.White,
				)

			ColorMode.Dark -> ColorProvider(Color.Dark)
			ColorMode.Light -> ColorProvider(Color.White)
			ColorMode.Custom -> ColorProvider(Color(this.bgCustomColor.value))
			else -> ColorProvider(Color.Transparent)
		}

	companion object {
		/**
		 * Loads preferences from a widget's state.
		 *
		 * A widget without any saved state (e.g. a newly added widget) gets the defaults.
		 */
		fun load(prefs: Preferences): PreferencesManager {
			val manager = PreferencesManager()
			for (pref in manager._preferences) {
				pref.load(prefs)
			}
			return manager
		}

		suspend fun load(
			context: Context,
			glanceId: GlanceId,
		): PreferencesManager {
			val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
			return load(prefs)
		}

		/** Loads preferences of every placed widget, in placement order */
		suspend fun loadAll(context: Context): List<Pair<GlanceId, PreferencesManager>> =
			GlanceAppWidgetManager(context)
				.getGlanceIds(HijriWidget::class.java)
				.map { id -> id to load(context, id) }

		/**
		 * Loads preferences used for app-wide behavior (e.g. the launcher icon),
		 * which follows the first placed widget, or defaults if no widget is placed.
		 */
		suspend fun loadPrimary(context: Context): PreferencesManager =
			loadAll(context).firstOrNull()?.second ?: PreferencesManager()
	}

	suspend fun save(
		context: Context,
		glanceId: GlanceId,
	) {
		updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
			prefs.toMutablePreferences().apply { saveInto(this) }
		}
	}

	private fun saveInto(prefs: MutablePreferences) {
		for (pref in _preferences) {
			pref.save(prefs)
		}
	}

	fun reset() {
		for (pref in _preferences) {
			pref.reset()
		}
	}

	// ======================================= //
	// Helper methods to construct preferences //
	// ======================================= //
	fun <T, K> preference(
		key: Preferences.Key<K>,
		default: T,
		parser: (K) -> T,
		serializer: (T) -> K,
	): Preference<T, K> {
		val p =
			Preference(
				key,
				default,
				parser,
				serializer,
			)
		@Suppress("UNCHECKED_CAST")
		_preferences.add(p as Preference<Any, Any>)
		return p
	}

	fun intPreference(
		key: String,
		default: Int,
	) = preference(
		key = intPreferencesKey(key),
		default = default,
		parser = { it },
		serializer = { it },
	)

	fun floatPreference(
		key: String,
		default: Float,
	) = preference(
		key = floatPreferencesKey(key),
		default = default,
		parser = { it },
		serializer = { it },
	)

	fun stringPreference(
		key: String,
		default: String,
	) = preference(
		key = stringPreferencesKey(key),
		default = default,
		parser = { it },
		serializer = { it },
	)

	fun booleanPreference(
		key: String,
		default: Boolean,
	) = preference(
		key = booleanPreferencesKey(key),
		default = default,
		parser = { it },
		serializer = { it },
	)

	inline fun <reified T : Enum<T>> enumPreference(
		key: String,
		default: T,
	) = preference(
		key = stringPreferencesKey(key),
		default = default,
		parser = { enumValueOf<T>(it) },
		serializer = { it.toString() },
	)
}

class Preference<T, K>(
	val key: Preferences.Key<K>,
	val default: T,
	private val parser: (K) -> T,
	private val serializer: (T) -> K,
) {
	private val _value = mutableStateOf(default)
	var value: T
		get() = _value.value
		set(v) {
			_value.value = v
		}

	fun load(prefs: Preferences) {
		this.value =
			prefs[key]?.let { runCatching { parser(it) }.onFailure(logException).getOrNull() }
				?: default
	}

	fun save(prefs: MutablePreferences) {
		prefs[key] = serializer(value)
	}

	fun reset() {
		value = default
	}
}
