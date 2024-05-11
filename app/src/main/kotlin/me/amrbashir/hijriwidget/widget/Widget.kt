package me.amrbashir.hijriwidget.widget

import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.Settings
import me.amrbashir.hijriwidget.settings.SupportedTheme

class HijriWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HijriWidget()
}

class HijriWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Settings.load(context)
        HijriDate.syncDatabaseIfNot(context)
        HijriDate.load(context, Settings.language.value)
        HijriWidgetWorker.setup24Periodic(context)

        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        val remoteView = getView()

        remoteView.setTextViewText(R.id.widget_text_view, HijriDate.today.value)

        AndroidRemoteViews(remoteView)
    }

    @Composable
    private fun getView(): RemoteViews {
        return if (Settings.theme.value == SupportedTheme.Dynamic) {
            RemoteViews(LocalContext.current.packageName, R.layout.widget_text_view_dynamic)
        } else {
            val view = RemoteViews(LocalContext.current.packageName, R.layout.widget_text_view)
            view.setTextColor(R.id.widget_text_view, Settings.color.value)

            return view
        }
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
