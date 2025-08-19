package me.amrbashir.hijriwidget.android

import android.app.Application
import me.amrbashir.hijriwidget.Preferences

class HijriWidgetApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Preferences.load(this)
        AlarmReceiver.setup24Periodic(this)
    }
}
