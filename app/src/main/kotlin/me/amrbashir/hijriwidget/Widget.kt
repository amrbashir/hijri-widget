package me.amrbashir.hijriwidget

import android.content.Context
import android.icu.util.Calendar
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.*
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class HijriWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HijriWidget()
}

class HijriWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Settings.load(context)
        HijriDate.load(context, Settings.language.value)

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

        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        val packageName = LocalContext.current.packageName
        val remoteView = RemoteViews(packageName, R.layout.widget_text_view)

        remoteView.setTextViewText(R.id.widget_text_view, HijriDate.today.value)

        AndroidRemoteViews(
            remoteViews = remoteView,
            modifier = GlanceModifier.padding(8.dp)
        )
    }
}


class HijriWidgetWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        HijriWidget().apply {
            Settings.load(applicationContext)
            HijriDate.load(applicationContext, Settings.language.value)
            updateAll(applicationContext)
        }
        return Result.success()
    }
}


// TODOs:
// 1. Update date when system time changes
// 2. Run logic on widget startup:
//   - check if some `lastUpdate` key exists:
//     - if does not exit: repopulate database and set the key
//     - if exists: check the next hijri month start date and set a job to run to repopulate database
// 3. Run same logic as 2. whenever a Internet Connection is available
