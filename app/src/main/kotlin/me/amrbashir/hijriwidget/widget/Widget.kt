package me.amrbashir.hijriwidget.widget

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.ColorMode
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.widgetCornerRadius

class HijriWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HijriWidget()
}

class HijriWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        // DO NOT REMOVE, needed for glance to recompose when this value is changed
        val updateSignal = WidgetUpdateSignal.value

        val prefsManager = PreferencesManager.load(context)

        val remoteViewId =
            if (prefsManager.textShadow.value) R.id.widget_text_shadow else R.id.widget_text
        val remoteViewLayout =
            if (prefsManager.textShadow.value) R.layout.widget_text_shadow else R.layout.widget_text
        val remoteViews = RemoteViews(LocalContext.current.packageName, remoteViewLayout)


        val date = HijriDate.todayFormatted(prefsManager)
        remoteViews.setTextViewText(remoteViewId, date)

        remoteViews.setTextViewTextSize(
            remoteViewId,
            TypedValue.COMPLEX_UNIT_SP,
            prefsManager.textSize.value
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && prefsManager.textColorMode.value == ColorMode.Dynamic) {
            remoteViews.setColorAttr(remoteViewId, "setTextColor", android.R.attr.colorPrimary)
        } else {
            remoteViews.setTextColor(remoteViewId, prefsManager.getTextColor(context).toArgb())
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier.fillMaxSize()
                .appWidgetBackground()
                .widgetCornerRadius()
                .background(prefsManager.getBgColor(context))
                .clickable {
                    coroutineScope.launch {
                        HijriWidget.updateAll(context)
                        AlarmReceiver.setup24Periodic(context, prefsManager)
                    }
                }
        ) {
            AndroidRemoteViews(remoteViews)
        }
    }

    companion object {
        suspend fun updateAll(context: Context) {
            WidgetUpdateSignal.value = !WidgetUpdateSignal.value
            HijriWidget().updateAll(context)
        }
    }
}


/** Workaround to force the Widget glance to recompose */
val WidgetUpdateSignal = mutableStateOf(true)
