package me.amrbashir.hijriwidget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

const val PREF = "HijriWidgetPref"
const val LANG_KEY = "LANG"

object Settings {
    val language: MutableState<String> = mutableStateOf("Arabic")
    val date: MutableState<String> = mutableStateOf("")

    fun load(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0);

        this.language.value = sharedPreferences.getString(LANG_KEY, "Arabic") ?: "Arabic";
        runBlocking {
            val calendar = getCalendarForCurrentYear()
            for ((g, h) in calendar) {
                sharedPreferences.edit()
                    .putString("_g_$g", Json.encodeToString(HijriDate.serializer(), h)).apply()
            }

        }
    }

    fun loadDate(context: Context, dateKey: String) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0);
        val dateJson = sharedPreferences.getString("_g_$dateKey", "") ?: "";
        val date = Json.decodeFromString<HijriDate>(dateJson)

        Log.e("", dateKey)

        val day = date.day.convertNumbersToAr()
        val month = when (language.value) {
            "English" -> date.month.en
            else -> date.month.ar
        }
        val year = date.year.toString().convertNumbersToAr()


        this.date.value = "$day $month $year"
    }

    fun save(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0);
        sharedPreferences.edit()?.putString(LANG_KEY, this.language.value)?.apply()
    }
}