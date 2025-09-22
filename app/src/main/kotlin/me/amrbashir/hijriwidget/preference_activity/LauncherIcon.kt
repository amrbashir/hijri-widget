package me.amrbashir.hijriwidget.preference_activity

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import me.amrbashir.hijriwidget.BuildConfig
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.PreferencesManager

fun changeLauncherIcon(context: Context, prefsManager: PreferencesManager) {
    val today = HijriDate.todayNumber(prefsManager)

    val packageManager = context.packageManager

    if (!BuildConfig.DEBUG) {
        val cls = "me.amrbashir.hijriwidget.preference_activity.PreferenceActivity"
        val mainActivity = ComponentName(context, cls)
        packageManager.setComponentEnabledSetting(
            mainActivity,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    for (day in 1..30) {
        val cls = "me.amrbashir.hijriwidget.preference_activity.Calendar_$day"
        val dayActivity = ComponentName(context, cls)
        packageManager.setComponentEnabledSetting(
            dayActivity,
            if (today == day) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}


