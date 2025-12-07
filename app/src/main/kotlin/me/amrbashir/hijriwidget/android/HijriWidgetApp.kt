package me.amrbashir.hijriwidget.android

import android.app.Application
import android.appwidget.AppWidgetProviderInfo
import android.os.Build
import androidx.collection.intSetOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.PreferencesManagerV2
import me.amrbashir.hijriwidget.widget.HijriWidget
import me.amrbashir.hijriwidget.widget.HijriWidgetReceiver

class HijriWidgetApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val context = this

        runBlocking {
            val prefs = HijriWidget.firstWidgetOrEmptyPrefs(context)
            val prefsManager = PreferencesManagerV2(context, prefs)

            AlarmReceiver.setup24Periodic(context, prefsManager)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                val appWidgetManager = GlanceAppWidgetManager(context)
                appWidgetManager.setWidgetPreviews(
                    HijriWidgetReceiver::class,
                    intSetOf(AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN)
                )
            }
        }
    }
}
