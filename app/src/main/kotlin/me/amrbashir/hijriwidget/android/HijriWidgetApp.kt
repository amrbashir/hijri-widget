package me.amrbashir.hijriwidget.android

import android.app.Application
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.preferences.HijriWidgetLauncherIconWorker
import me.amrbashir.hijriwidget.widget.HijriWidgetWorker

class HijriWidgetApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Preferences.load(this)
        HijriWidgetLauncherIconWorker.setup24Periodic(this)
        HijriWidgetWorker.setup24Periodic(this)
    }
}
