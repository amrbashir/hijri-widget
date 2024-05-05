package me.amrbashir.hijriwidget.widget

import android.content.Context
import android.icu.util.Calendar
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class HijriWidgetWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        HijriWidget.update(applicationContext)
        return Result.success()
    }


    companion object {
        fun setup24Periodic(context: Context) {
            val nextDayStart = Calendar.getInstance()
            nextDayStart[Calendar.DAY_OF_MONTH] = nextDayStart[Calendar.DAY_OF_MONTH] + 1
            nextDayStart[Calendar.HOUR_OF_DAY] = 0
            nextDayStart[Calendar.MINUTE] = 0
            nextDayStart[Calendar.SECOND] = 0

            val now = Calendar.getInstance()

            val delay = nextDayStart.timeInMillis - now.timeInMillis

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "hijriWidgetWorker",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                PeriodicWorkRequest.Builder(
                    HijriWidgetWorker::class.java,
                    24,
                    TimeUnit.HOURS
                ).setInitialDelay(delay, TimeUnit.MILLISECONDS).build()
            )
        }
    }
}

