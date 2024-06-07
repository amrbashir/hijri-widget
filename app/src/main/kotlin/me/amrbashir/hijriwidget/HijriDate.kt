package me.amrbashir.hijriwidget

import android.content.Context
import android.icu.util.Calendar
import android.icu.util.ULocale
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


val AR_MONTHS = arrayOf("مُحَرَّم",
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

    fun load(context: Context, lang: SupportedLanguage) {
        this.today.value = todayForLang(context, lang)
    }

    fun todayForLang(_c: Context, lang: SupportedLanguage): String {
        val calendar = Calendar.getInstance(ULocale("@calendar=islamic-umalqura"))
        val day = calendar[Calendar.DAY_OF_MONTH].toString().convertNumbersToLang(lang)
        val month = when (lang) {
            SupportedLanguage.English -> EN_MONTHS[calendar[Calendar.MONTH]]
            else -> AR_MONTHS[calendar[Calendar.MONTH]]
        }
        val year = calendar[Calendar.YEAR].toString().convertNumbersToLang(lang)

        return "$day $month $year"
    }

    fun todayNumber(_c: Context): Int {
        val calendar = Calendar.getInstance(ULocale("@calendar=islamic-umalqura"))
        return calendar[Calendar.DAY_OF_MONTH]
    }
}


// This is the prrevious implementation which used `aladhan` REST API
// However, this API was not accurate and so we needed to either
// switch to another API or use the `IslamicCalendar` class
//
//import android.content.Context
//import android.icu.util.Calendar
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.engine.android.Android
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.request.get
//import io.ktor.serialization.kotlinx.json.json
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//
//private const val PREF = "HijriWidgetDateDatabasePref"
//private const val DATE_KEY_PREFIX = "_g_"
//private const val LAST_UPDATE = "lastUpdate"
//
//
//@Serializable
//data class GregorianDate(val date: String)
//
//@Serializable
//data class HijriMonthData(val number: Int, val en: String, val ar: String)
//
//@Serializable
//data class HijriDateDataClass(val day: String, val month: HijriMonthData, val year: Int)
//
//@Serializable
//data class ResponseDataEntry(val hijri: HijriDateDataClass, val gregorian: GregorianDate)
//
//@Serializable
//data class ResponseData(val data: List<ResponseDataEntry>)
//
//
//object HijriDate {
//    val today: MutableState<String> = mutableStateOf("")
//
//    fun load(context: Context, lang: SupportedLanguage) {
//        this.today.value = todayForLang(context, lang)
//    }
//
//    fun todayForLang(context: Context, lang: SupportedLanguage): String {
//        val sharedPreferences = context.getSharedPreferences(PREF, 0)
//
//        val dateKey = "$DATE_KEY_PREFIX${todayAsGregorian()}"
//
//        val dateJson = sharedPreferences.getString(dateKey, "")
//        if (dateJson.isNullOrEmpty()) return "null"
//
//        val date = Json.decodeFromString<HijriDateDataClass>(dateJson)
//
//        val day = date.day.convertNumbersToLang(lang)
//        val month = when (lang) {
//            SupportedLanguage.English -> date.month.en
//            else -> date.month.ar
//        }
//        val year = date.year.toString().convertNumbersToLang(lang)
//
//        return "$day $month $year"
//    }
//
//    fun todayNumber(context: Context): Int? {
//        val sharedPreferences = context.getSharedPreferences(PREF, 0)
//
//        val dateKey = "$DATE_KEY_PREFIX${todayAsGregorian()}"
//
//        val dateJson = sharedPreferences.getString(dateKey, "")
//        if (dateJson.isNullOrEmpty()) return null
//
//        val date = Json.decodeFromString<HijriDateDataClass>(dateJson)
//        return date.day.toInt()
//    }
//
//
//    suspend fun syncDatabase(context: Context) {
//
//        val calendar = getCalendarForCurrentYear()
//
//        val sharedPreferences = context.getSharedPreferences(PREF, 0)
//
//        sharedPreferences.edit().run {
//            sharedPreferences.all.forEach { (k, _) ->
//                if (k.startsWith(DATE_KEY_PREFIX)) {
//                    remove(k)
//                }
//            }
//
//            commit()
//        }
//
//        sharedPreferences.edit().run {
//            for ((gregorian, hijri) in calendar) {
//                putString(
//                    "$DATE_KEY_PREFIX$gregorian",
//                    Json.encodeToString(HijriDateDataClass.serializer(), hijri)
//                )
//            }
//
//            val now = Calendar.getInstance()
//            putLong(
//                LAST_UPDATE,
//                now.timeInMillis
//            )
//
//            commit()
//        }
//    }
//
//    suspend fun syncDatabaseIfNot(context: Context) {
//        if (lastUpdate(context) == null) {
//            syncDatabase(context)
//        }
//    }
//
//    private fun lastUpdate(context: Context): Long? {
//        val sharedPreferences = context.getSharedPreferences(PREF, 0)
//        val lastUpdate = sharedPreferences.getLong(LAST_UPDATE, 0)
//        return if (lastUpdate == 0.toLong()) null else lastUpdate
//    }
//
//
//    private fun todayAsGregorian(): String {
//        val calendar = Calendar.getInstance()
//        val currentYear = calendar[Calendar.YEAR]
//        val currentMonth = "%02d".format(calendar[Calendar.MONTH] + 1)
//        val currentDay = "%02d".format(calendar[Calendar.DAY_OF_MONTH])
//        return "$currentDay-$currentMonth-$currentYear"
//    }
//
//
//    private suspend fun getCalendarForCurrentYear(): Map<String, HijriDateDataClass> {
//        val calendar = Calendar.getInstance()
//        val currentYear = calendar[Calendar.YEAR]
//        return getCalendarForYear(currentYear)
//    }
//
//
//    private suspend fun getCalendarForYear(year: Int): Map<String, HijriDateDataClass> {
//        val client = HttpClient(Android) {
//            install(ContentNegotiation) {
//                json(Json {
//                    encodeDefaults = true
//                    isLenient = true
//                    allowSpecialFloatingPointValues = true
//                    allowStructuredMapKeys = true
//                    prettyPrint = false
//                    useArrayPolymorphism = false
//                    ignoreUnknownKeys = true
//                })
//            }
//        }
//
//        val data = mutableMapOf<String, HijriDateDataClass>()
//        for (month in 1..12) {
//            val url = "https://api.aladhan.com/v1/gToHCalendar/$month/$year"
//            val response = client.get(url).body<ResponseData>()
//            for (m in response.data) {
//                data[m.gregorian.date] = m.hijri
//
//            }
//        }
//
//        return data
//    }
//}
