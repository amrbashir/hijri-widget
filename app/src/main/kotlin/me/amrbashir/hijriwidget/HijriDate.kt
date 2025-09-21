package me.amrbashir.hijriwidget

import android.icu.util.Calendar
import android.icu.util.ULocale

object HijriDate {
    fun today(prefsManager: PreferencesManager): Calendar {
        val locale = ULocale("@calendar=${prefsManager.calendarCalculationMethod.value}")
        val calendar = Calendar.getInstance(locale)

        val dayStart = prefsManager.dayStart.value

        calendar.add(Calendar.DAY_OF_MONTH, prefsManager.dayOffset.value)

        val currentMinutes = calendar[Calendar.HOUR_OF_DAY] * 60 + calendar[Calendar.MINUTE]

        if (currentMinutes < dayStart) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        return calendar
    }

    fun todayStr(prefsManager: PreferencesManager): String {
        val today = today(prefsManager)
        val format =
            if (prefsManager.dateIsCustomFormat.value) prefsManager.dateCustomFormat.value else prefsManager.dateFormat.value
        return format.formatHijriDate(today, prefsManager.calendarCalculationMethod.value)
    }

    fun todayNumber(prefsManager: PreferencesManager): Int {
        return today(prefsManager)[Calendar.DAY_OF_MONTH]
    }
}



