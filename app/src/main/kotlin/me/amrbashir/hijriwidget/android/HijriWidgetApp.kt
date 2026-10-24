package me.amrbashir.hijriwidget.android

import android.app.Application
import android.appwidget.AppWidgetProviderInfo
import android.os.Build
import androidx.collection.intSetOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.migrateConfig
import me.amrbashir.hijriwidget.widget.HijriWidgetReceiver

class HijriWidgetApp : Application() {
	override fun onCreate() {
		super.onCreate()

		val context = this

		// Migration must finish before anything reads widget preferences
		runBlocking { migrateConfig(context) }

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

	companion object {
		/** App-wide scope for background work that outlives any single component */
		val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
	}
}
