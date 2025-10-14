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

data class DateFormatSegment(val langCode: String?, val format: String)

fun parseDateFormat(input: String): List<DateFormatSegment> {
    // Regex to parse a string like "en-GB{dd}"
    val regex = Regex("""([a-z-A-Z]{2}-[a-z-A-Z]{2})\{([^}]+)\}""")
    val result = mutableListOf<DateFormatSegment>()
    var currentIndex = 0

    for (match in regex.findAll(input)) {
        val matchStart = match.range.first
        val matchEnd = match.range.last + 1

        // Add literal text before the match
        // for example: dd MMMM en-GB{yyyy}
        //              ^^^^^^^^
        // or for example: en-GB{dd} MMMM en-GB{yyyy}
        //                           ^^^^
        if (matchStart > currentIndex) {
            val literal = input.substring(currentIndex, matchStart)
            result.add(DateFormatSegment(null, literal))
        }

        // Add the language-format pair
        val langCode = match.groupValues[1]
        val formatStr = match.groupValues[2]
        result.add(DateFormatSegment(langCode, formatStr))

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
        result.add(DateFormatSegment(null, input.substring(currentIndex)))
    }

    return result
}


@SuppressLint("SimpleDateFormat")
fun String.formatHijriDate(date: Calendar, calcMethod: HijriDateCalculationMethod): String {
    return parseDateFormat(this).fold("") { acc, it ->
        val locale = ULocale("${it.langCode ?: "ar-SA"}@calendar=${calcMethod.id}")
        try {
            val dateFormatter = SimpleDateFormat(it.format, locale)
            var formatted = dateFormatter.format(date)

            // In mixed languages, like `en-GB{dd} ar-SA{MMMM} en-GB{yyyy}`,
            // adding LTR marker after the output of RTL segments `ar-SA{MMMM}` -> `صفر\u2000E`
            // will force the flow of characters to stay consistent i.e left-to-right, while keeping flow
            // from right-to-left, if the formatted output is all RTL like `ar-SA{dd MMMM yyyy}`
            formatted += if (formatted.any { it.isRtl() }) "\u200E" else ""

            acc + formatted
        } catch (_: Exception) {
            // if failed to format probably due to invalid format
            // just add the format as is
            acc + it.format
        }
    }
}
