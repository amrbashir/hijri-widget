package me.amrbashir.hijriwidget

import android.icu.util.Calendar
import android.icu.util.ULocale
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


val AR_MONTHS = arrayOf(
    "مُحَرَّم",
    "صَفَر",
    "رَبِيع الأوَّل",
    "رَبِيع الثَّاني",
    "جُمَادى الأوَّل",
    "جُمَادى الثَّاني",
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


object HijriDate {
    val today: MutableState<String> = mutableStateOf("")

    fun load(lang: SupportedLanguage) {
        this.today.value = todayForLang(lang)
    }

    fun todayForLang(lang: SupportedLanguage): String {
        val calendar = Calendar.getInstance(ULocale("@calendar=islamic-umalqura"))
        val day = calendar[Calendar.DAY_OF_MONTH].toString().convertNumbersToLang(lang)
        val month = when (lang) {
            SupportedLanguage.English -> EN_MONTHS[calendar[Calendar.MONTH]]
            else -> AR_MONTHS[calendar[Calendar.MONTH]]
        }
        val year = calendar[Calendar.YEAR].toString().convertNumbersToLang(lang)

        return "$day $month $year"
    }

    fun todayNumber(): Int {
        val calendar = Calendar.getInstance(ULocale("@calendar=islamic-umalqura"))
        return calendar[Calendar.DAY_OF_MONTH]
    }
}
