package me.amrbashir.hijriwidget.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.android.AlarmReceiver

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

        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier.fillMaxSize()
        ) {
            Row(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Preferences.getBgColor(context))
                    .clickable {
                        runBlocking {
                            update(context)
                            AlarmReceiver.setup24Periodic(context)
                        }
                    }
            ) {
                Text(
                    HijriDate.today.value,
                    style = TextStyle(
                        color = Preferences.getColor(context),
                        fontSize = Preferences.textSize.value.sp
                    )
                )
            }
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
