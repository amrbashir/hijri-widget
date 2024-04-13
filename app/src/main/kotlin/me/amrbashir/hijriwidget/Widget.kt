package me.amrbashir.hijriwidget

import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.*
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*

class HijriWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HijriWidget()
}

class HijriWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Settings.load(context)
        provideContent {
            GlanceTheme {
                Content()
            }
        }
    }

    @Composable
    private fun Content() {
        val packageName = LocalContext.current.packageName
        val remoteView = RemoteViews(packageName, R.layout.widget_text_view);
        remoteView.setTextViewText(R.id.widget_text_view, Settings.language.value)


        AndroidRemoteViews(
            remoteViews = remoteView,
            modifier = GlanceModifier.padding(8.dp)
        )
    }
}

