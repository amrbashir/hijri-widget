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
        return if (Preferences.textIsCustomFormat.value) Preferences.textCustomFormat.value.formatDate(
            today
        )
        else Preferences.textFormat.value.formatDate(today)
    }

    fun todayNumber(): Int {
        return today()[Calendar.DAY_OF_MONTH]
    }
}


enum class HijriDateCalculationMethod(val id: String, val label: String, val description: String) {
    ISLAMIC(
        "islamic",
        "Isalmic",
        "Uses pure astronomical calculations to determine when the new moon occurs"
    ),
    ISLAMIC_UMALQURA(
        "islamic-umalqura",
        "Islamic Umm al-Qura",
        "Based on the calculations used by the Umm al-Qura University in Mecca"
    ),
    ISLAMIC_CIVIL(
        "islamic-civil",
        "Islamic Civil",
        "Uses a fixed algorithmic approach with a 30-year cycle"
    ),
}

