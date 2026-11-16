package me.amrbashir.hijriwidget.android

import android.app.Application
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.collection.intSetOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.logException
import me.amrbashir.hijriwidget.migrateConfig
import me.amrbashir.hijriwidget.widget.HijriLockScreenWidgetReceiver
import me.amrbashir.hijriwidget.widget.HijriWidgetReceiver

class HijriWidgetApp : Application() {
	override fun onCreate() {
		super.onCreate()

		val context = this

		// Migration must finish before anything reads widget preferences.
		// A failure is logged instead of crashing every app start, the failed step
		// isn't marked done, so it's retried on the next start.
		runBlocking { runCatching { migrateConfig(context) }.onFailure(logException) }

		runCatching { disableLockScreenWidget(context) }.onFailure(logException)

		// Not needed to show the UI, so don't block the main thread on it
		scope.launch {
			AlarmReceiver.scheduleNextUpdate(context)

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
				val appWidgetManager = GlanceAppWidgetManager(context)
				appWidgetManager.setWidgetPreviews(
					HijriWidgetReceiver::class,
					intSetOf(AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN),
				)
			}
		}
	}

	/**
	 * Only Samsung's lock screen needs [HijriLockScreenWidgetReceiver], elsewhere it would
	 * show up as a second lock screen widget next to the home screen one.
	 *
	 * It's disabled here rather than enabled on Samsung, because Samsung's lock screen only
	 * picks up widgets that are enabled when the app is installed, so enabling it at runtime
	 * leaves the widget blank on the lock screen.
	 */
	private fun disableLockScreenWidget(context: Context) {
		if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) return

		val component = ComponentName(context, HijriLockScreenWidgetReceiver::class.java)
		val state = PackageManager.COMPONENT_ENABLED_STATE_DISABLED
		val packageManager = context.packageManager
		if (packageManager.getComponentEnabledSetting(component) == state) return
		packageManager.setComponentEnabledSetting(component, state, PackageManager.DONT_KILL_APP)
	}

	companion object {
		/** App-wide scope for background work that outlives any single component */
		val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
	}
}
