package me.amrbashir.hijriwidget

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.runBlocking

const val PREF = "HijriWidgetPref"
const val LANG_KEY = "LANG"

object Settings {
    val language: MutableState<String> = mutableStateOf("Arabic")

    fun load(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0);
        this.language.value = sharedPreferences.getString(LANG_KEY, "Arabic") ?: "Arabic";
    }

    fun save(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREF, 0);
        sharedPreferences.edit()?.putString(LANG_KEY, this.language.value)?.apply()

        runBlocking {
            HijriWidget().apply {
                load(context)
                HijriDate.load(context, language.value)
                updateAll(context)
            }
        }
    }
}