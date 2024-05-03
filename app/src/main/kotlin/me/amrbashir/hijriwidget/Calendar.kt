package me.amrbashir.hijriwidget

import android.icu.util.Calendar
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GregorianDate(val date: String)

@Serializable
data class HijriMonthData(val number: Int, val en: String, val ar: String)

@Serializable
data class HijriDate(val day: String, val month: HijriMonthData, val year: Int)

@Serializable
data class ResponseDataEntry(val hijri: HijriDate, val gregorian: GregorianDate)

@Serializable
data class ResponseData(@Suppress("ArrayInDataClass") val data: Array<ResponseDataEntry>)


suspend fun getCalendarForCurrentYear(): Map<String, HijriDate> {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    return getCalendarForYear(currentYear)
}


suspend fun getCalendarForYear(year: Int): Map<String, HijriDate> {
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

    val data = mutableMapOf<String, HijriDate>()
    for (month in 1..12) {
        val url = "https://api.aladhan.com/v1/gToHCalendar/$month/$year"
        val response = client.get(url).body<ResponseData>()
        for (m in response.data) {
            data[m.gregorian.date] = m.hijri

        }
    }

    return data
}