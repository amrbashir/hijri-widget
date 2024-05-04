package me.amrbashir.hijriwidget

import android.content.Context
import android.icu.util.Calendar
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


object HijriDate {
    val today: MutableState<String> = mutableStateOf("")

    fun load(context: Context, lang: String) {
        this.today.value = todayForLang(context, lang)
    }

    fun todayForLang(context: Context, lang: String): String {
        val sharedPreferences = context.getSharedPreferences(PREF, 0)

        val dateKey = "_g_${todayAsGorgeian()}"

        val dateJson = sharedPreferences.getString(dateKey, "") ?: ""
        val date = Json.decodeFromString<HijriDateDataClass>(dateJson)

        val day = date.day.convertNumbersToLang(lang)
        val month = when (lang) {
            "English" -> date.month.en
            else -> date.month.ar
        }
        val year = date.year.toString().convertNumbersToLang(lang)

        return "$day $month $year";
    }


    suspend fun populateDatabase(context: Context) {
        val calendar = getCalendarForCurrentYear()
        val sharedPreferences = context.getSharedPreferences(PREF, 0)
        for ((g, h) in calendar) {
            sharedPreferences.edit()
                .putString("_g_$g", Json.encodeToString(HijriDateDataClass.serializer(), h)).apply()
        }
    }
}


fun todayAsGorgeian(): String {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = "%02d".format(calendar.get(Calendar.MONTH) + 1)
    val currentDay = "%02d".format(calendar.get(Calendar.DAY_OF_MONTH))

    return "$currentDay-$currentMonth-$currentYear"
}


@Serializable
data class GregorianDate(val date: String)

@Serializable
data class HijriMonthData(val number: Int, val en: String, val ar: String)

@Serializable
data class HijriDateDataClass(val day: String, val month: HijriMonthData, val year: Int)

@Serializable
data class ResponseDataEntry(val hijri: HijriDateDataClass, val gregorian: GregorianDate)

@Serializable
data class ResponseData(@Suppress("ArrayInDataClass") val data: Array<ResponseDataEntry>)


suspend fun getCalendarForCurrentYear(): Map<String, HijriDateDataClass> {
    val calendar = Calendar.getInstance()
    val currentYear = calendar[Calendar.YEAR]
    return getCalendarForYear(currentYear)
}


suspend fun getCalendarForYear(year: Int): Map<String, HijriDateDataClass> {
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                encodeDefaults = true
                isLenient = true
                allowSpecialFloatingPointValues = true
                allowStructuredMapKeys = true
                prettyPrint = false
                useArrayPolymorphism = false
                ignoreUnknownKeys = true
            })
        }
    }

    val data = mutableMapOf<String, HijriDateDataClass>()
    for (month in 1..12) {
        val url = "https://api.aladhan.com/v1/gToHCalendar/$month/$year"
        val response = client.get(url).body<ResponseData>()
        for (m in response.data) {
            data[m.gregorian.date] = m.hijri

        }
    }

    return data
}