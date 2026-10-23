package me.amrbashir.hijriwidget.android

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.logTimestamp
import me.amrbashir.hijriwidget.preference_activity.changeLauncherIcon
import me.amrbashir.hijriwidget.widget.HijriWidget

class AlarmReceiver : BroadcastReceiver() {
	@SuppressLint("UnsafeProtectedBroadcastReceiver")
	override fun onReceive(
		context: Context,
		intent: Intent,
	) {
		Log.d("AlarmReceiver", "Alarm fired at: ${logTimestamp()}")

		// Glance's `updateAll` may need the main thread, so blocking it here would deadlock
		val pendingResult = goAsync()
		CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
			try {
				changeLauncherIcon(context, PreferencesManager.loadPrimary(context))
				HijriWidget().updateAll(context)
				setup24Periodic(context)
			} finally {
				pendingResult.finish()
			}
		}
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

		/**
		 * Schedules the next update at the earliest day start among all widgets
		 * (and the launcher icon, which follows the first widget or defaults).
		 */
		suspend fun setup24Periodic(context: Context) {
			val alarmManager = context.getSystemService<AlarmManager>() ?: return
			if (!AlarmManagerCompat.canScheduleExactAlarms(alarmManager)) return

			val prefsManagers =
				PreferencesManager
					.loadAll(context)
					.map { it.second }
					.ifEmpty { listOf(PreferencesManager.loadPrimary(context)) }
			val nextUpdateMillis = prefsManagers.minOf { nextUpdateDateInMillis(it) }

			val intent = Intent(context, AlarmReceiver::class.java)
			alarmManager.setExact(
				AlarmManager.RTC,
				nextUpdateMillis,
				PendingIntent.getBroadcast(
					context,
					ALARM_REQUEST_CODE,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
				),
			)

			Log.d("AlarmReceiver", "Next alarm will fire at: ${logTimestamp(nextUpdateMillis)}")
		}
	}
}
