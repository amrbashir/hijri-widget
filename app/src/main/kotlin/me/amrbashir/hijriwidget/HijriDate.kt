package me.amrbashir.hijriwidget

import android.icu.util.Calendar
import android.icu.util.ULocale
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object HijriDate {
    val today: MutableState<String> = mutableStateOf("")

    fun load() {
        this.today.value = todayStr()
    }

    fun today(): Calendar {
        val locale = ULocale("@calendar=${Preferences.calendarCalculationMethod.value}")
        val calendar = Calendar.getInstance(locale)

        val dayStart = Preferences.dayStart.value

        calendar.add(Calendar.DAY_OF_MONTH, Preferences.dayOffset.value)

        val currentMinutes = calendar[Calendar.HOUR_OF_DAY] * 60 + calendar[Calendar.MINUTE]
        val dayStartMinutes = dayStart.hour * 60 + dayStart.minute

        if (currentMinutes < dayStartMinutes) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }

        return calendar
    }

    fun todayStr(): String {
        val today = today()
        return if (Preferences.dateIsCustomFormat.value) Preferences.dateCustomFormat.value.formatHijriDate(
            today
        )
        else Preferences.dateFormat.value.formatHijriDate(today)
    }

    fun todayNumber(): Int {
        return today()[Calendar.DAY_OF_MONTH]
    }
}



