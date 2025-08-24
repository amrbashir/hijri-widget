package me.amrbashir.hijriwidget.widget

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.widgetCornerRadius

class HijriWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HijriWidget()
}

class HijriWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Preferences.load(context)
        HijriDate.load()

        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        Preferences.load(context)
        HijriDate.load()

        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current

        val remoteViewId =
            if (Preferences.textShadow.value) R.id.widget_text_shadow else R.id.widget_text
        val remoteViewLayout =
            if (Preferences.textShadow.value) R.layout.widget_text_shadow else R.layout.widget_text
        val remoteViews = RemoteViews(LocalContext.current.packageName, remoteViewLayout)

        remoteViews.setTextViewText(remoteViewId, HijriDate.today.value)
        remoteViews.setTextViewTextSize(
            remoteViewId,
            TypedValue.COMPLEX_UNIT_SP,
            Preferences.textSize.value
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            remoteViews.setColorAttr(remoteViewId, "setTextColor", android.R.attr.colorPrimary)
        } else {
            remoteViews.setTextColor(remoteViewId, Preferences.getTextColor(context).toArgb())
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier.fillMaxSize()
                .appWidgetBackground()
                .widgetCornerRadius()
                .background(Preferences.getBgColor(context))
                .clickable {
                    runBlocking {
                        update(context)
                        AlarmReceiver.setup24Periodic(context)
                    }
                }
        ) {
            AndroidRemoteViews(remoteViews)
        }
    }

    companion object {
        suspend fun update(context: Context) {
            HijriWidget().apply {
                Preferences.load(context)
                HijriDate.load()
                updateAll(context)
            }
        }
    }
}
