package me.amrbashir.hijriwidget.widget

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import me.amrbashir.hijriwidget.nextDayStartWorkerDelay
import java.util.concurrent.TimeUnit

class HijriWidgetWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        try {
            HijriWidget.update(applicationContext)
            return Result.success()
        } catch (ex: Exception) {
            ex.printStackTrace()
            return Result.retry()
        }
    }


    companion object {
        fun setup24Periodic(context: Context, cancelAndRequeue: Boolean = false) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "hijriWidgetWorker",
                if (cancelAndRequeue) ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE else ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequest.Builder(
                    HijriWidgetWorker::class.java,
                    24,
                    TimeUnit.HOURS
                ).setInitialDelay(nextDayStartWorkerDelay(), TimeUnit.MILLISECONDS).build()
            )
        }
    }
}

