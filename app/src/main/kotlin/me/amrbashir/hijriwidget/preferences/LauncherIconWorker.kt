package me.amrbashir.hijriwidget.preferences

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import me.amrbashir.hijriwidget.BuildConfig
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.nextDayStartWorkerDelay
import java.util.concurrent.TimeUnit

class HijriWidgetLauncherIconBroadCastReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent?) {
        Preferences.load(context)
        HijriWidgetLauncherIconWorker.changeLauncherIcon(context)
        HijriWidgetLauncherIconWorker.setup24Periodic(context)
    }
}

class HijriWidgetLauncherIconWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        try {
            changeLauncherIcon(this.applicationContext)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }

    companion object {
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

        fun setup24Periodic(context: Context, cancelAndRequeue: Boolean = false) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "hijriWidgetLauncherIconWorker",
                if (cancelAndRequeue) ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE else ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequest.Builder(
                    HijriWidgetLauncherIconWorker::class.java,
                    24,
                    TimeUnit.HOURS
                ).setInitialDelay(nextDayStartWorkerDelay(), TimeUnit.MILLISECONDS).build()
            )
        }
    }
}

