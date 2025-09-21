package me.amrbashir.hijriwidget.android

import android.app.Application
import android.appwidget.AppWidgetProviderInfo
import android.os.Build
import androidx.collection.intSetOf
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.widget.HijriWidgetReceiver

class HijriWidgetApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val prefsManager = PreferencesManager.load(this)
        AlarmReceiver.setup24Periodic(this, prefsManager)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            runBlocking {
                GlanceAppWidgetManager(this@HijriWidgetApp).setWidgetPreviews(
                    HijriWidgetReceiver::class,
                    intSetOf(AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN)
                )
            }
        }
    }
}
