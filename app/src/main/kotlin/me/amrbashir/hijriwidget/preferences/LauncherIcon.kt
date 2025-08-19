package me.amrbashir.hijriwidget.preferences

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import me.amrbashir.hijriwidget.BuildConfig
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences

fun changeLauncherIcon(context: Context) {
    val today = HijriDate.todayNumber(Preferences.dayStart.value)

    val packageManager = context.packageManager

    if (!BuildConfig.DEBUG) {
        val mainActivity =
            ComponentName(context, "me.amrbashir.hijriwidget.preferences.MainActivity")
        packageManager.setComponentEnabledSetting(
            mainActivity,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    for (day in 1..30) {
        val dayActivity =
            ComponentName(context, "me.amrbashir.hijriwidget.preferences.Calendar_$day")
        packageManager.setComponentEnabledSetting(
            dayActivity,
            if (today == day) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}


