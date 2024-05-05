package me.amrbashir.hijriwidget.widget

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.runBlocking

class TimeChangedReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent?) {
        runBlocking {
            HijriWidget.update(context)
            HijriWidgetWorker.setup24Periodic(context)
        }
    }
}