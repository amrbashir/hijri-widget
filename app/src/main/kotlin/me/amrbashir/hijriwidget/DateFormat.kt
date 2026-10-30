package me.amrbashir.hijriwidget

import android.annotation.SuppressLint
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale
import java.text.AttributedCharacterIterator
import java.text.CharacterIterator

val DATE_FORMAT_PRESETES =
	listOf(
		"d MMMM yyyy",
		"d / MM / yy",
		"ar-SA{d MMMM yyyy}",
		"ar-SA{d / MM / yy}",
		"en-GB{d MMMM yyyy}",
		"en-GB{d / MM / yy}",
		"en-GB{d} ar-SA{MMMM} en-GB{yyyy}",
	)

data class DateFormatSegment(
	/** null uses the device’s default locale for unwrapped text and anonymous {...} groups. */
	val langCode: String?,
	val format: String,
)

fun parseDateFormat(input: String): List<DateFormatSegment> =
	buildList {
		// Match quoted text first so braces and language tags inside it stay literal.
		// Two apostrophes are escaped; an unfinished quote protects the remaining text.
		val quotedText = """'(?:''|[^'])*(?:'|$)"""
		val languageTag = """[a-zA-Z]{2,8}(?:-[a-zA-Z0-9]{1,8})*"""
		val group = """($languageTag)?\{((?:$quotedText|[^'}])+)\}"""
		val regex = Regex("$quotedText|$group")
		var currentIndex = 0

		for (match in regex.findAll(input)) {
			// Quoted matches have no group body. Keep them in the pending text for ICU.
			if (match.groups[2] == null) continue

			val matchStart = match.range.first
			val matchEnd = match.range.last + 1

			// Add literal text before the match
			// for example: dd MMMM en-GB{yyyy}
			//              ^^^^^^^^
			// or for example: en-GB{dd} MMMM en-GB{yyyy}
			//                           ^^^^
			if (matchStart > currentIndex) {
				val literal = input.substring(currentIndex, matchStart)
				add(DateFormatSegment(null, literal))
			}

			// Add the language-format pair
			val langCode = match.groupValues[1].ifEmpty { null }
			val formatStr = match.groupValues[2]
			add(DateFormatSegment(langCode, formatStr))

			// Update the current index for the next iteration
			currentIndex = matchEnd
		}

		// Add any trailing literal text
		// for example: en-GB{dd MMMM} yyyy
		//                             ^^^^
		// or in case there was never language-format pair, add whole string
		// for example: dd MMMM yyyy
		//              ^^^^^^^^^^^^^
		if (currentIndex < input.length) {
			add(DateFormatSegment(null, input.substring(currentIndex)))
		}
	}

// Lint assumes java.text.SimpleDateFormat, this is ICU's, and the locale is always given
@SuppressLint("SimpleDateFormat")
fun String.formatHijriDate(
	date: Calendar,
	calcMethod: HijriDateCalculationMethod,
	weekdayDate: Calendar = date,
): String =
	parseDateFormat(this).fold("") { acc, it ->
		val language = it.langCode ?: ULocale.getDefault().toLanguageTag()
		val locale = ULocale("$language@calendar=${calcMethod.id}")
		runCatching {
			val dateFormatter = SimpleDateFormat(it.format, locale)
			var formatted = dateFormatter.format(date, weekdayDate)

			// In mixed languages, like `en-GB{dd} ar-SA{MMMM} en-GB{yyyy}`,
			// adding LTR marker after the output of RTL segments `ar-SA{MMMM}` -> `صفر\u2000E`
			// will force the flow of characters to stay consistent i.e left-to-right, while keeping flow
			// from right-to-left, if the formatted output is all RTL like `ar-SA{dd MMMM yyyy}`
			formatted += if (formatted.any { it.isRtl() }) "\u200E" else ""

			acc + formatted
		}.onFailure(logException).getOrElse { _ ->
			// if failed to format probably due to invalid format
			// just add the format as is
			acc + it.format
		}
	}

private val WEEKDAY_FIELDS = setOf(DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.DOW_LOCAL)

/** Formats [date], but with the weekday of [weekdayDate] */
private fun SimpleDateFormat.format(
	date: Calendar,
	weekdayDate: Calendar,
): String {
	// Both outputs come from the same pattern, so their weekday parts appear in the same order
	val weekdays =
		formatToCharacterIterator(weekdayDate)
			.runs()
			.filter { it.second }
			.map { it.first }
			.iterator()

	return formatToCharacterIterator(date).runs().joinToString("") { (text, isWeekday) ->
		if (isWeekday && weekdays.hasNext()) weekdays.next() else text
	}
}

/** Splits the formatted text into runs, each paired with whether it's a weekday */
private fun AttributedCharacterIterator.runs(): List<Pair<String, Boolean>> {
	val runs = mutableListOf<Pair<String, Boolean>>()
	first()
	while (current() != CharacterIterator.DONE) {
		val isWeekday = attributes.keys.any { it in WEEKDAY_FIELDS }
		val limit = getRunLimit(WEEKDAY_FIELDS)
		val text = StringBuilder()
		while (index < limit) {
			text.append(current())
			next()
		}
		runs.add(text.toString() to isWeekday)
	}
	return runs
}
