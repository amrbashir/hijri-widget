package me.amrbashir.hijriwidget

import android.icu.util.Calendar
import android.icu.util.ULocale
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


val AR_MONTHS = arrayOf(
    "مُحَرَّم",
    "صَفَر",
    "رَبِيع الأوَّل",
    "رَبِيع الثَّاني",
    "جُمَادى الأولى",
    "جُمَادى الآخرة",
    "رَجَب",
    "شَعْبَان",
    "رَمَضَان",
    "شَوَّال",
    "ذُو الْقَعْدَة",
    "ذُو الْحِجَّة"
)
val EN_MONTHS = arrayOf(
    "Muharram",
    "Safar",
    "Rabi’ al-Awwal",
    "Rabi’ al-Thani",
    "Jumada al-Awwal",
    "Jumada al-Thani",
    "Rajab",
    "Sha’ban",
    "Ramadan",
    "Shawwal",
    "Dhu al-Qi’dah",
    "Dhu al-Hijjah",
)


class HijriDate (val day: Int, val month: Int, val year: Int) {

    fun display(): String {
        val lang = Preferences.language.value

        val day = day.toString().convertNumbersToLang(Preferences.language.value)
        val monthName = when (lang) {
            SupportedLanguage.English -> EN_MONTHS[month]
            else -> AR_MONTHS[month]
        }
        val year = year.toString().convertNumbersToLang(Preferences.language.value)

        return "$day $monthName $year"
    }

    companion object {
        val today: MutableState<HijriDate> = mutableStateOf(HijriDate(0,0, 0))

        fun load() {
            this.today.value = today()
        }

        fun today(): HijriDate {
            val dayStart = Preferences.dayStart.value

            val calendar = Calendar.getInstance(ULocale("@calendar=${Preferences.calendarCalculationMethod.value}"))

            Log.d("HijriDate", "Day before offset: ${calendar[Calendar.DAY_OF_MONTH]}/${calendar[Calendar.MONTH]}")
            calendar.add(Calendar.DAY_OF_MONTH, Preferences.dayOffset.value)
            Log.d("HijriDate", "Day after offset: ${calendar[Calendar.DAY_OF_MONTH]}/${calendar[Calendar.MONTH]}")

            val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
            val day = if (
                calendar[Calendar.HOUR_OF_DAY] >= dayStart.hour &&
                calendar[Calendar.MINUTE] >= dayStart.minute
            ) dayOfMonth else dayOfMonth - 1

            val month = calendar[Calendar.MONTH]
            val year = calendar[Calendar.YEAR]

            return HijriDate(day, month, year)
        }
    }
}
