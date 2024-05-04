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

            val delay = nextDayStart.timeInMillis - now.timeInMillis;

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


// TODOs:
// 1. Update date when system time changes
// 2. Run logic on widget startup:
//   - check if some `lastUpdate` key exists:
//     - if does not exit: repopulate database and set the key
//     - if exists: check the next hijri month start date and set a job to run to repopulate database
// 3. Run same logic as 2. whenever a Internet Connection is available
