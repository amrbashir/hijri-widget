package me.amrbashir.hijriwidget.preferences

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import me.amrbashir.hijriwidget.BuildConfig
import me.amrbashir.hijriwidget.HijriDate
import java.util.concurrent.TimeUnit

class HijriWidgetLauncherIconBroadCastReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent?) {
        HijriWidgetLauncherIconWorker.changeLauncherIcon(context)
        HijriWidgetLauncherIconWorker.setup24Periodic(context)
    }
}

class HijriWidgetLauncherIconWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        changeLauncherIcon(this.applicationContext)
        return Result.success()
    }

    companion object {
        fun changeLauncherIcon(context: Context) {
//            runBlocking { HijriDate.syncDatabaseIfNot(context) }
            val today = HijriDate.todayNumber()

            val packageManager = context.packageManager


            if (!BuildConfig.DEBUG) {
                val mainActivity =
                    ComponentName(context, "me.amrbashir.hijriwidget.preferences.MainActivity")
                packageManager.setComponentEnabledSetting(
                    mainActivity,
                    if (today != null) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            }

            for (day in 1..30) {
                val dayActivity =
                    ComponentName(context, "me.amrbashir.hijriwidget.preferences.Calendar_$day")
                packageManager.setComponentEnabledSetting(
                    dayActivity,
                    if (today != null && today == day) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }


        }

        fun setup24Periodic(context: Context) {
            val nextDayStart = Calendar.getInstance()
            nextDayStart[Calendar.DAY_OF_MONTH] = nextDayStart[Calendar.DAY_OF_MONTH] + 1
            nextDayStart[Calendar.HOUR_OF_DAY] = 0
            nextDayStart[Calendar.MINUTE] = 0
            nextDayStart[Calendar.SECOND] = 0

            val now = Calendar.getInstance()

            val delay = nextDayStart.timeInMillis - now.timeInMillis

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "hijriWidgetLauncherIconWorker",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                PeriodicWorkRequest.Builder(
                    HijriWidgetLauncherIconWorker::class.java,
                    24,
                    TimeUnit.HOURS
                ).setInitialDelay(delay, TimeUnit.MILLISECONDS).build()
            )
        }
    }
}

