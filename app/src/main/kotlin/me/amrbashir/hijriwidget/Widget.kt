package me.amrbashir.hijriwidget

import android.content.Context
import android.icu.util.Calendar
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
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Settings.load(context)
        provideContent {
            GlanceTheme {
                Content(context)
            }
        }
    }

    @Composable
    private fun Content(context: Context) {
        val packageName = LocalContext.current.packageName
        val remoteView = RemoteViews(packageName, R.layout.widget_text_view)

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = "%02d".format(calendar.get(Calendar.MONTH) + 1)
        val currentDay = "%02d".format(calendar.get(Calendar.DAY_OF_MONTH))

        val date = "$currentDay-$currentMonth-$currentYear"
        Settings.loadDate(context, date)

        remoteView.setTextViewText(R.id.widget_text_view, Settings.date.value)

        AndroidRemoteViews(
            remoteViews = remoteView,
            modifier = GlanceModifier.padding(8.dp)
        )
    }
}

