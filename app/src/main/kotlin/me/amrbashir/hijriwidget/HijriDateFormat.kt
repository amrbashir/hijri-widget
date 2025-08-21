package me.amrbashir.hijriwidget

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.ULocale

val FORMAT_PRESETES = arrayOf(
    "dd MMMM yyyy",
    "en-GB{dd MMMM yyyy}",
    "en-GB{dd} ar-SA{MMMM} en-GB{yyyy}",
    "dd / MM / yy",
    "en-GB{dd / MM / yy}",
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
fun String.formatDate(date: Calendar): String {
    val calcMethod = Preferences.calendarCalculationMethod.value
    return parseFormatString(this).fold("") { acc, it ->
        val locale = ULocale("${it.langCode ?: "ar-SA"}@calendar=$calcMethod")
        try {
            val dateFormatter = SimpleDateFormat(it.format, locale)
            acc + dateFormatter.format(date)
        } catch (_: Exception) {
            acc + it.format
        }
    }
}
