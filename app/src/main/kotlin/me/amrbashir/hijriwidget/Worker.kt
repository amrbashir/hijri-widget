package me.amrbashir.hijriwidget

import android.icu.util.Calendar

fun nextDayStartWorkerDelay(): Long {
    val nextDayStart = Calendar.getInstance()
    if (
        nextDayStart[Calendar.HOUR_OF_DAY] >= Preferences.dayStart.value.hour &&
        nextDayStart[Calendar.MINUTE] >= Preferences.dayStart.value.minute
    ) {
        nextDayStart[Calendar.DAY_OF_MONTH] = nextDayStart[Calendar.DAY_OF_MONTH] + 1
    }
    nextDayStart[Calendar.HOUR_OF_DAY] = Preferences.dayStart.value.hour
    nextDayStart[Calendar.MINUTE] = Preferences.dayStart.value.minute
    nextDayStart[Calendar.SECOND] = 0

    val now = Calendar.getInstance()

    return nextDayStart.timeInMillis - now.timeInMillis
}
