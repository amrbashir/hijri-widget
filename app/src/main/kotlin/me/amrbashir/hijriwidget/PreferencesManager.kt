package me.amrbashir.hijriwidget

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextDirectionHeuristics
import android.text.style.AlignmentSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import android.view.Gravity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.unit.ColorProvider
import me.amrbashir.hijriwidget.widget.HijriWidget
import kotlin.math.roundToInt

val Color.Companion.Dark: Color get() = Color(0xFF151515)

class PreferencesManager {
	private val _preferences: MutableList<Preference<*, *>> = mutableListOf()

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

	val fontSize =
		floatPreference(
			key = "FONT_SIZE",
			default = 22F,
		)

	val textFontFamily = stringPreference("TEXT_FONT_FAMILY", "")

	val textWeight =
		floatPreference(
			key = "TEXT_WEIGHT",
			default = 400F,
		)

	val horizontalAlignment =
		enumPreference("HORIZONTAL_ALIGNMENT", HorizontalAlignment.Center)

	val verticalAlignment =
		enumPreference("VERTICAL_ALIGNMENT", VerticalAlignment.Center)

	val paddingStart = intPreference("PADDING_START", 0)

	val paddingTop = intPreference("PADDING_TOP", 0)

	val paddingEnd = intPreference("PADDING_END", 0)

	val paddingBottom = intPreference("PADDING_BOTTOM", 0)

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

	fun supportedFontWeights(): List<Int> = systemFontFamily(textFontFamily.value).supportedWeights()

	fun getFontWeight(): Int {
		val requested = textWeight.value.roundToInt()
		return supportedFontWeights().minBy { kotlin.math.abs(it - requested) }
	}

	@Composable
	fun getTextColor(
		context: Context,
		mode: ColorMode = textColorMode.value,
	): Color {
		val isDark =
			context.resources.configuration.uiMode and
				Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
		return when (mode) {
			ColorMode.Dynamic if Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
				GlanceTheme.colors.primary.getColor(context)
			ColorMode.System -> if (isDark) Color.Dark else Color.White
			ColorMode.Dark -> Color.Dark
			ColorMode.Light -> Color.White
			ColorMode.Custom -> Color(textCustomColor.value)
			else -> Color.White
		}
	}

	@Composable
	fun getBgColor(
		context: Context,
		mode: ColorMode = bgColorMode.value,
	): ColorProvider {
		val isDark =
			context.resources.configuration.uiMode and
				Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
		return when (mode) {
			ColorMode.Dynamic if Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
				GlanceTheme.colors.widgetBackground
			ColorMode.System -> ColorProvider(if (isDark) Color.Dark else Color.White)
			ColorMode.Dark -> ColorProvider(Color.Dark)
			ColorMode.Light -> ColorProvider(Color.White)
			ColorMode.Custom -> ColorProvider(Color(bgCustomColor.value))
			else -> ColorProvider(Color.Transparent)
		}
	}

	companion object {
		/**
		 * Parses already read preferences, e.g. a widget's state.
		 *
		 * Missing values (e.g. a newly added widget) get the defaults.
		 */
		fun fromPreferences(prefs: Preferences): PreferencesManager {
			val manager = PreferencesManager()
			for (pref in manager._preferences) {
				pref.load(prefs)
			}
			return manager
		}

		/** Reads and parses the stored preferences of a widget */
		suspend fun load(
			context: Context,
			glanceId: GlanceId,
		): PreferencesManager {
			val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
			return fromPreferences(prefs)
		}

		/** Loads preferences of every placed widget, in the order of [HijriWidget.glanceIds] */
		suspend fun loadAll(context: Context): List<PreferencesManager> =
			HijriWidget.glanceIds(context).map { load(context, it) }

		/**
		 * Loads preferences used for app-wide behavior (e.g. the launcher icon),
		 * which follows the first widget returned by [HijriWidget.glanceIds],
		 * or defaults if no widget is placed.
		 */
		suspend fun loadFirstOrDefault(context: Context): PreferencesManager =
			HijriWidget.glanceIds(context).firstOrNull()?.let { load(context, it) }
				?: PreferencesManager()
	}

	suspend fun save(
		context: Context,
		glanceId: GlanceId,
	) {
		updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
			prefs.toMutablePreferences().apply {
				for (pref in _preferences) {
					pref.save(this)
				}
			}
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
		_preferences.add(p)
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

enum class HorizontalAlignment(
	val gravity: Int,
	val bias: Float,
	val textAlign: TextAlign,
) {
	Left(Gravity.LEFT, -1F, TextAlign.Left),
	Center(Gravity.CENTER_HORIZONTAL, 0F, TextAlign.Center),
	Right(Gravity.RIGHT, 1F, TextAlign.Right),
}

enum class VerticalAlignment(
	val gravity: Int,
	val bias: Float,
) {
	Top(Gravity.TOP, -1F),
	Center(Gravity.CENTER_VERTICAL, 0F),
	Bottom(Gravity.BOTTOM, 1F),
}

internal fun SpannableString.applyHorizontalAlignment(
	horizontalAlignment: HorizontalAlignment,
	isDeviceRTL: Boolean,
) {
	val directionHeuristic =
		if (isDeviceRTL) {
			TextDirectionHeuristics.FIRSTSTRONG_RTL
		} else {
			TextDirectionHeuristics.FIRSTSTRONG_LTR
		}

	// Alignment is relative to paragraph direction; separate spans keep mixed LTR/RTL lines
	// aligned to the same physical left or right edge.
	var start = 0
	for (paragraph in split('\n')) {
		val isRtl = directionHeuristic.isRtl(paragraph, 0, paragraph.length)

		val alignment =
			when (horizontalAlignment) {
				HorizontalAlignment.Left ->
					if (isRtl) Layout.Alignment.ALIGN_OPPOSITE else Layout.Alignment.ALIGN_NORMAL
				HorizontalAlignment.Center -> Layout.Alignment.ALIGN_CENTER
				HorizontalAlignment.Right ->
					if (isRtl) Layout.Alignment.ALIGN_NORMAL else Layout.Alignment.ALIGN_OPPOSITE
			}

		val end = start + paragraph.length

		setSpan(
			AlignmentSpan.Standard(alignment),
			start,
			end,
			Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
		)

		start = end + 1
	}
}

internal fun SpannableString.applyFontWeight(
	context: Context,
	familyName: String,
	weight: Int,
) {
	val family = systemFontFamily(familyName)
	val name = family.weightAliases[weight] ?: family.name
	setSpan(TypefaceSpan(name), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

	if (family.variable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
		val nearestWeight = (100..900 step 100).minBy { kotlin.math.abs(it - weight) }
		val appearance =
			when (nearestWeight) {
				100 -> R.style.WidgetWeight100
				200 -> R.style.WidgetWeight200
				300 -> R.style.WidgetWeight300
				400 -> R.style.WidgetWeight400
				500 -> R.style.WidgetWeight500
				600 -> R.style.WidgetWeight600
				700 -> R.style.WidgetWeight700
				800 -> R.style.WidgetWeight800
				else -> R.style.WidgetWeight900
			}
		setSpan(TextAppearanceSpan(context, appearance), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
	} else if (weight == 700) {
		setSpan(StyleSpan(Typeface.BOLD), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
	}
}
