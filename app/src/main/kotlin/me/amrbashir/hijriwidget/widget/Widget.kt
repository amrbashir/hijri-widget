package me.amrbashir.hijriwidget.widget

import android.content.Context
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.SupportedTheme
import me.amrbashir.hijriwidget.android.AlarmReceiver

class HijriWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HijriWidget()
}

class HijriWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Preferences.load(context)
        HijriDate.load(Preferences.language.value, Preferences.dayStart.value)

        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current

        val remoteView = getView()

        remoteView.setTextViewText(R.id.widget_text_view, HijriDate.today.value)

        if (Preferences.isCustomTextSize.value) {
            remoteView.setTextViewTextSize(
                R.id.widget_text_view,
                TypedValue.COMPLEX_UNIT_SP,
                Preferences.customTextSize.value
            )
        } else {
            remoteView.setTextViewTextSize(R.id.widget_text_view, TypedValue.COMPLEX_UNIT_SP, 22F)
        }

        AndroidRemoteViews(remoteView, modifier = GlanceModifier.clickable {
            runBlocking {
                update(context)
                AlarmReceiver.setup24Periodic(context)
            }
        })
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
                HijriDate.load(Preferences.language.value, Preferences.dayStart.value)
                updateAll(context)
            }
        }
    }
}
