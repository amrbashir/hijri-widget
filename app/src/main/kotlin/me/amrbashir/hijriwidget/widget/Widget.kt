package me.amrbashir.hijriwidget.widget

import android.annotation.SuppressLint
import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
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
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.SupportedTheme
import me.amrbashir.hijriwidget.addIf
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

    @SuppressLint("RestrictedApi")
    @Composable
    private fun Content() {
        val context = LocalContext.current

        Row(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(12.dp)
                .addIf(Preferences.bgTheme.value == SupportedTheme.Dynamic) {
                    background(GlanceTheme.colors.widgetBackground)
                }
                .addIf(Preferences.bgTheme.value != SupportedTheme.Dynamic) {
                    background(Preferences.getBgColor(context))
                }
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
                    color = if (Preferences.theme.value == SupportedTheme.Dynamic) GlanceTheme.colors.primary else ColorProvider(
                        Preferences.getColor(context)
                    ),
                    fontSize = Preferences.textSize.value.sp
                )
            )
        }
    }


    @Composable
    private fun getView(): RemoteViews {
        val layout = if (Preferences.shadow.value) R.layout.widget_text_view
        else R.layout.widget_text_view_no_shadow
        val view = RemoteViews(LocalContext.current.packageName, layout)
        return view
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
