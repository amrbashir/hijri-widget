package me.amrbashir.hijriwidget

import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

const val LANG_KEY = "LANG"

object Settings : ViewModel() {
    val language: MutableState<String> = mutableStateOf("Arabic")

    fun load(sharedPreferences: SharedPreferences) {
        this.language.value = sharedPreferences.getString(LANG_KEY, "Arabic") ?: "Arabic";
    }

    fun save(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit()?.putString(LANG_KEY, this.language.value)?.apply()
    }
}