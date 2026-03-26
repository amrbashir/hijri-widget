package me.amrbashir.hijriwidget.android

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.media.AudioManager
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.logTimestamp
import me.amrbashir.hijriwidget.preference_activity.changeLauncherIcon
import me.amrbashir.hijriwidget.widget.HijriWidget

import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents

// Tambahan Import untuk Opt-In ExperimentalTime
import kotlin.OptIn
import kotlin.time.ExperimentalTime

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Action received: ${intent.action} at: ${logTimestamp()}")

        val prefsManager = PreferencesManager.load(context)
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            ACTION_MUTE -> {
                if (prefsManager.autoSilentEnabled.value) {
                    if (notificationManager.isNotificationPolicyAccessGranted) {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                        Log.d("AlarmReceiver", "Phone muted for prayer")

                        val durationInMillis = prefsManager.silentDuration.value * 60 * 1000L
                        scheduleExactAction(context, ACTION_UNMUTE, System.currentTimeMillis() + durationInMillis, 999)
                    } else {
                        Log.d("AlarmReceiver", "Failed to mute: DND permission not granted")
                    }
                }
            }
            ACTION_UNMUTE -> {
                if (notificationManager.isNotificationPolicyAccessGranted) {
                    audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                    Log.d("AlarmReceiver", "Phone unmuted")
                }
            }
            else -> {
                changeLauncherIcon(context, prefsManager)
                runBlocking { HijriWidget.updateAll(context) }
                setup24Periodic(context, prefsManager)

                scheduleDailyPrayers(context, prefsManager)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun scheduleDailyPrayers(context: Context, prefsManager: PreferencesManager) {
        val alarmManager = context.getSystemService<AlarmManager>() ?: return
        if (!AlarmManagerCompat.canScheduleExactAlarms(alarmManager)) return

        val lat = prefsManager.prayerLatitude.value.toDouble()
        val lng = prefsManager.prayerLongitude.value.toDouble()
        val coordinates = Coordinates(lat, lng)

        val now = Calendar.getInstance()
        val dateComponents = DateComponents(
            year = now[Calendar.YEAR],
            month = now[Calendar.MONTH] + 1,
            day = now[Calendar.DAY_OF_MONTH]
        )

        val methodString = prefsManager.prayerCalcMethod.value
        val calcMethod = try {
            CalculationMethod.valueOf(methodString)
        } catch (e: Exception) {
            CalculationMethod.MUSLIM_WORLD_LEAGUE
        }
        val parameters = calcMethod.parameters

        val prayerTimes = PrayerTimes(coordinates, dateComponents, parameters)

        scheduleExactAction(context, ACTION_MUTE, prayerTimes.fajr.toEpochMilliseconds(), 101)
        scheduleExactAction(context, ACTION_MUTE, prayerTimes.dhuhr.toEpochMilliseconds(), 102)
        scheduleExactAction(context, ACTION_MUTE, prayerTimes.asr.toEpochMilliseconds(), 103)
        scheduleExactAction(context, ACTION_MUTE, prayerTimes.maghrib.toEpochMilliseconds(), 104)
        scheduleExactAction(context, ACTION_MUTE, prayerTimes.isha.toEpochMilliseconds(), 105)
    }

    private fun scheduleExactAction(context: Context, action: String, timeInMillis: Long, requestCode: Int) {
        if (timeInMillis <= System.currentTimeMillis()) return

        val alarmManager = context.getSystemService<AlarmManager>() ?: return
        val intent = Intent(context, AlarmReceiver::class.java).apply { this.action = action }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        Log.d("AlarmReceiver", "Scheduled $action at ${logTimestamp(timeInMillis)}")
    }

    companion object {
        private const val ALARM_REQUEST_CODE = 1
        const val ACTION_MUTE = "me.amrbashir.hijriwidget.ACTION_MUTE"
        const val ACTION_UNMUTE = "me.amrbashir.hijriwidget.ACTION_UNMUTE"

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
            val alarmManager = context.getSystemService<AlarmManager>() ?: return
            if (!AlarmManagerCompat.canScheduleExactAlarms(alarmManager)) return

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
            Log.d("AlarmReceiver", "Next alarm will fire at: ${logTimestamp(nextUpdateMillis)}")
        }
    }
}
