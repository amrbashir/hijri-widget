package me.amrbashir.hijriwidget.preference_activity

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import me.amrbashir.hijriwidget.BuildConfig
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.PreferencesManager

fun changeLauncherIcon(context: Context, prefsManager: PreferencesManager) {
    val actions = buildList {
        if (!BuildConfig.DEBUG) {
            val cls = "me.amrbashir.hijriwidget.preference_activity.PreferenceActivity"
            val mainActivity = ComponentName(context, cls)
            add(mainActivity to PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        }

        val today = HijriDate.todayNumber(prefsManager)
        for (day in 1..30) {
            val cls = "me.amrbashir.hijriwidget.preference_activity.Calendar_$day"
            val dayActivity = ComponentName(context, cls)
            val newState = if (today == day) PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            add(dayActivity to newState)
        }
    }

    val packageManager = context.packageManager
    val flags = PackageManager.DONT_KILL_APP
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.setComponentEnabledSettings(
            actions.map { (componentName, newState) ->
                PackageManager.ComponentEnabledSetting(componentName, newState, flags)
            }
        )
    } else {
        actions.forEach { (componentName, newState) ->
            packageManager.setComponentEnabledSetting(componentName, newState, flags)
        }
    }
}


