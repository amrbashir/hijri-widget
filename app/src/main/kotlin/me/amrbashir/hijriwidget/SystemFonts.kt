package me.amrbashir.hijriwidget

import android.graphics.Typeface
import android.os.Build
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.File

internal data class SystemFontFamily(
	val name: String,
	val weights: List<Int>,
	val variable: Boolean = false,
	val weightAliases: Map<Int, String> = emptyMap(),
) {
	// Older launchers can select static faces through normal/bold or a named weight alias.
	fun supportedWeights(): List<Int> {
		val supportsVariableWeights = variable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
		val supported =
			weights.filter { weight ->
				val standardWeight = weight == 400 || weight == 700
				val variableWeight = supportsVariableWeights && weight in 100..900 && weight % 100 == 0
				standardWeight || weight in weightAliases || variableWeight
			}

		return supported.ifEmpty { listOf(weights.minBy { kotlin.math.abs(it - 400) }) }
	}
}

private data class FontAlias(
	val name: String,
	val target: String,
	val weight: Int?,
)

private val CONFIG_PATHS =
	listOf(
		"/system/etc/fonts.xml",
		"/product/etc/fonts_customization.xml",
		"/vendor/etc/fonts.xml",
	)

private val systemFonts: Map<String, SystemFontFamily> by lazy { readSystemFonts() }

internal fun systemFontFamilies(): List<String> = systemFonts.keys.sorted()

internal fun systemFontFamily(name: String): SystemFontFamily =
	systemFonts[name.ifEmpty { "sans-serif" }] ?: systemFonts.getValue("sans-serif")

/** Read named families and weight aliases, including vendor additions when available. */
private fun readSystemFonts(): Map<String, SystemFontFamily> {
	val families = mutableMapOf<String, SystemFontFamily>()
	val aliases = mutableListOf<FontAlias>()
	for (path in CONFIG_PATHS) {
		val file = File(path)
		if (!file.canRead()) continue

		runCatching { readFontConfig(file, families, aliases) }.onFailure(logException)
	}

	for (name in listOf("sans-serif", "serif", "monospace")) {
		families.putIfAbsent(name, SystemFontFamily(name, listOf(400)))
	}
	applyFontAliases(families, aliases)

	// Typeface silently falls back for unknown names; don't offer them as distinct fonts.
	return families.filter { (name, _) ->
		// isDefaultFamily || resolvesToDistinctTypeface
		name == "sans-serif" ||
			Typeface.create(name, Typeface.NORMAL) != Typeface.DEFAULT
	}
}

private fun readFontConfig(
	file: File,
	families: MutableMap<String, SystemFontFamily>,
	aliases: MutableList<FontAlias>,
) {
	file.inputStream().use { input ->
		val parser = Xml.newPullParser()
		parser.setInput(input, null)

		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.eventType != XmlPullParser.START_TAG) continue

			when (parser.name) {
				"family" -> readFontFamily(parser)?.let { families[it.name] = it }
				"alias" -> readFontAlias(parser)?.let(aliases::add)
			}
		}
	}
}

private fun readFontFamily(parser: XmlPullParser): SystemFontFamily? {
	val name = parser.getAttributeValue(null, "name")
	val weights = mutableSetOf<Int>()
	var variable = false
	var isNormalFont = false
	while (parser.next() != XmlPullParser.END_DOCUMENT) {
		if (parser.eventType == XmlPullParser.END_TAG && parser.name == "family") break
		if (parser.eventType != XmlPullParser.START_TAG) continue
		when (parser.name) {
			"font" -> {
				isNormalFont = parser.getAttributeValue(null, "style") != "italic"
				if (isNormalFont) weights += parser.getAttributeValue(null, "weight")?.toIntOrNull() ?: 400
			}
			"axis" -> if (isNormalFont && parser.getAttributeValue(null, "tag") == "wght") variable = true
		}
	}

	if (name == null || weights.isEmpty()) return null

	return SystemFontFamily(name, weights.sorted(), variable)
}

private fun readFontAlias(parser: XmlPullParser): FontAlias? {
	val name = parser.getAttributeValue(null, "name") ?: return null
	val target = parser.getAttributeValue(null, "to") ?: return null
	val weight = parser.getAttributeValue(null, "weight")?.toIntOrNull()
	return FontAlias(name, target, weight)
}

private fun applyFontAliases(
	families: MutableMap<String, SystemFontFamily>,
	aliases: List<FontAlias>,
) {
	for (alias in aliases) {
		val family = families[alias.target] ?: continue
		if (alias.weight != null) {
			families[alias.target] =
				family.copy(weightAliases = family.weightAliases + (alias.weight to alias.name))
		}
		families[alias.name] =
			family.copy(name = alias.name, weights = alias.weight?.let(::listOf) ?: family.weights)
	}
}
