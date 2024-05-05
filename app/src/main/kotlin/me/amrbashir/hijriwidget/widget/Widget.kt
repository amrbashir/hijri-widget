package me.amrbashir.hijriwidget.widget

import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.padding
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.Settings

class HijriWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HijriWidget()
}

class HijriWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Settings.load(context)
        HijriDate.load(context, Settings.language.value)
        HijriWidgetWorker.setup24Periodic(context)

        val lastUpdate = HijriDate.lastUpdate(context)
        if (lastUpdate == null) {
            HijriDate.syncDatabase(context)
        }

        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        val packageName = LocalContext.current.packageName
        val remoteView = RemoteViews(packageName, R.layout.widget_text_view)

        remoteView.setTextViewText(R.id.widget_text_view, HijriDate.today.value)

        AndroidRemoteViews(
            remoteViews = remoteView,
            modifier = GlanceModifier.padding(8.dp)
        )
    }

    companion object {
        suspend fun update(context: Context) {
            HijriWidget().apply {
                Settings.load(context)
                HijriDate.load(context, Settings.language.value)
                updateAll(context)
            }
        }
    }
}
