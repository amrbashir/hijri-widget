package me.amrbashir.hijriwidget.android

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.os.Build
import android.util.Log
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.changeLauncherIcon
import me.amrbashir.hijriwidget.widget.HijriWidget
import java.util.Date;


class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarm fired at: " + DateFormat.getDateTimeInstance().format(Date(System.currentTimeMillis())) )

        runBlocking {
            changeLauncherIcon(context)
            HijriWidget.update(context)
        }

        setup24Periodic(context)
    }


    companion object {
        private const val ALARM_REQUEST_CODE = 1

        fun setup24Periodic(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

            val alarmManager: AlarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager? ?: return

            if (!alarmManager.canScheduleExactAlarms()) return

            val nextUpdateMillis = Preferences.nextUpdateDateInMillis()

            Log.d("AlarmReceiver", "Setting up next alarm to fire at: " + DateFormat.getDateTimeInstance().format(Date(nextUpdateMillis)))


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
        }
    }
}
