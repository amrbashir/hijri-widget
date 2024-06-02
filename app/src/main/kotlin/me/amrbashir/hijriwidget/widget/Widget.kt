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
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.SupportedTheme

class HijriWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HijriWidget()
}

class HijriWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Preferences.load(context)
        HijriDate.syncDatabaseIfNot(context)
        HijriDate.load(context, Preferences.language.value)
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
        return if (Preferences.theme.value == SupportedTheme.Dynamic) getDynamicView()
        else getNormalView()
    }

    @Composable
    private fun getDynamicView(): RemoteViews {
        val layout = if (Preferences.shadow.value) R.layout.widget_text_view_dynamic
        else R.layout.widget_text_view_dynamic_no_shadow
        return RemoteViews(LocalContext.current.packageName, layout)
    }

    @Composable
    private fun getNormalView(): RemoteViews {
        val layout = if (Preferences.shadow.value) R.layout.widget_text_view
        else R.layout.widget_text_view_no_shadow
        val view = RemoteViews(LocalContext.current.packageName, layout)
        view.setTextColor(R.id.widget_text_view, Preferences.color.value)
        return view
    }

    companion object {
        suspend fun update(context: Context) {
            HijriWidget().apply {
                Preferences.load(context)
                HijriDate.load(context, Preferences.language.value)
                updateAll(context)
            }
        }
    }
}
