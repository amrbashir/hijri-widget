package me.amrbashir.hijriwidget.android

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.getLocalDateTime
import me.amrbashir.hijriwidget.preference_activity.changeLauncherIcon
import me.amrbashir.hijriwidget.widget.HijriWidget


class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm fired at: ${getLocalDateTime()}")

        val prefsManager = PreferencesManager.load(context)

        changeLauncherIcon(context, prefsManager)
        runBlocking { HijriWidget.updateAll(context) }
        setup24Periodic(context, prefsManager)
    }


    companion object {
        private const val ALARM_REQUEST_CODE = 1

        fun nextUpdateDateInMillis(prefsManager: PreferencesManager): Long {
            val nextDay = Calendar.getInstance()

            val currentTime = nextDay[Calendar.HOUR_OF_DAY] * 60 + nextDay[Calendar.MINUTE]
            if (currentTime >= prefsManager.dayStart.value) {
                nextDay[Calendar.DAY_OF_MONTH] = nextDay[Calendar.DAY_OF_MONTH] + 1
            }

            nextDay[Calendar.HOUR_OF_DAY] = prefsManager.dayStart.value / 60
            nextDay[Calendar.MINUTE] = prefsManager.dayStart.value % 60
            nextDay[Calendar.SECOND] = 0

            return nextDay.timeInMillis
        }

        fun setup24Periodic(context: Context, prefsManager: PreferencesManager) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

            val service = Context.ALARM_SERVICE
            val alarmManager = context.getSystemService(service) as AlarmManager? ?: return
            if (!alarmManager.canScheduleExactAlarms()) return

            val nextUpdateMillis = nextUpdateDateInMillis(prefsManager)

            val intent = Intent(context, AlarmReceiver::class.java)
            alarmManager.setExact(
                AlarmManager.RTC,
                nextUpdateMillis,
                PendingIntent.getBroadcast(
                    context,
                    ALARM_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            Log.d("AlarmReceiver", "Next alarm will fire at: ${getLocalDateTime(nextUpdateMillis)}")
        }
    }
}
