package me.amrbashir.hijriwidget

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale

val DATE_FORMAT_PRESETES = arrayOf(
    "d MMMM yyyy",
    "en-GB{d MMMM yyyy}",
    "en-GB{d} ar-SA{MMMM} en-GB{yyyy}",
    "d / MM / yy",
    "en-GB{d / MM / yy}",
)

data class FormatSegment(val langCode: String?, val format: String)

fun parseFormatString(input: String): List<FormatSegment> {
    val regex = Regex("""([a-z-A-Z]{2}-[a-z-A-Z]{2})\{([^}]+)\}""")
    val result = mutableListOf<FormatSegment>()
    var currentIndex = 0

    for (match in regex.findAll(input)) {
        val matchStart = match.range.first
        val matchEnd = match.range.last + 1

        // Add literal text before the match
        if (matchStart > currentIndex) {
            val literal = input.substring(currentIndex, matchStart)
            result.add(FormatSegment(null, literal))
        }

        // Add the language-format pair
        val langCode = match.groupValues[1]
        val formatStr = match.groupValues[2]
        result.add(FormatSegment(langCode, formatStr))

        currentIndex = matchEnd
    }

    // Add any trailing literal text
    if (currentIndex < input.length) {
        result.add(FormatSegment(null, input.substring(currentIndex)))
    }

    return result
}


@SuppressLint("SimpleDateFormat")
fun String.formatHijriDate(date: Calendar, calcMethod: HijriDateCalculationMethod): String {
    return parseFormatString(this).fold("") { acc, it ->
        val locale = ULocale("${it.langCode ?: "ar-SA"}@calendar=${calcMethod}")
        try {
            val dateFormatter = SimpleDateFormat(it.format, locale)
            val formatted = dateFormatter.format(date)
            // In mixed languages, like `en-GB{dd} ar-SA{MMMM} en-GB{yyyy}`,
            // adding LTR marker after the output of RTL segments `ar-SA{MMMM}` -> `صفر\u2000E`
            // will force the flow of characters to stay consistent i.e left-to-right, while keeping flow
            // from right-to-left, if the formatted output is all RTL like `ar-SA{dd MMMM yyyy}`
            val leftToRightMarker = if (formatted.any { it.isRtl() }) "\u200E" else ""
            acc + formatted + leftToRightMarker
        } catch (_: Exception) {
            acc + it.format
        }
    }
}
