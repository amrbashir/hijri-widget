package me.amrbashir.hijriwidget

import android.content.Context
import android.icu.util.Calendar
import android.icu.util.ULocale

const val TWELVE_HOURS: Int = 720 // 12 * 60

object HijriDate {
    fun today(prefsManager: PreferencesManager): Calendar {
        val calcMethod = prefsManager.calendarCalculationMethod.value.id
        val locale = ULocale("@calendar=$calcMethod")
        val calendar = Calendar.getInstance(locale)

        // Offset the day by configured value
        calendar.add(Calendar.DAY_OF_MONTH, prefsManager.dayOffset.value)


        val dayStartIsPM = prefsManager.dayStart.value >= TWELVE_HOURS
        val currentMinutes = calendar[Calendar.HOUR_OF_DAY] * 60 + calendar[Calendar.MINUTE]

        // PM times are used to start Hijri day before Gregorian day starts
        // for example:
        //   Gregorian day is 14 and its calculated Hijri day is 22,
        //   while dayStart is set to 6:00PM (At sunset, i.e Maghrib).
        //
        //   When the clock hits 6:00 PM, Hijri day becomes 23 while Gregorian day
        //   stays at 14.
        if (currentMinutes >= prefsManager.dayStart.value && dayStartIsPM) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // AM times are used to start Hijri day after Gregorian day starts
        // for example:
        //   Gregorian day is 13 and its calculated Hijri day is 22,
        //   while dayStart is set to 6:00AM (At dawn, i.e Fajr).
        //
        //   When the clock hits 12:00 AM, Gregorian day becomes 23 while Hijri day
        //   stays as 22, then when the clock hits 6:00 AM, Hijri day becomes 23.
        if (currentMinutes <= prefsManager.dayStart.value && !dayStartIsPM) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        return calendar
    }

    fun todayFormatted(context: Context, prefsManager: PreferencesManager): String {
        val format = if (prefsManager.dateIsCustomFormat.value) {
            prefsManager.dateCustomFormat.value
        } else {
            prefsManager.dateFormat.value
        }
        val today = today(prefsManager)
        return format.formatHijriDate(context, today, prefsManager.calendarCalculationMethod.value)
    }

    fun todayNumber(prefsManager: PreferencesManager): Int {
        return today(prefsManager)[Calendar.DAY_OF_MONTH]
    }
}



