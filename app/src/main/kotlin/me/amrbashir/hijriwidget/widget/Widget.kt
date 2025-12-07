package me.amrbashir.hijriwidget.widget

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.ColorMode
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.PreferencesManagerV2
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.widgetCornerRadius

class HijriWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HijriWidget()
}

class HijriWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

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
            }
        }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()


        val prefs = currentState<Preferences>()
        val prefsManager = PreferencesManagerV2(context, prefs)

        val textShadow = prefsManager.textShadow.value
        val textSize = prefsManager.textSize.value
        val textColorMode = prefsManager.textColorMode.value
        val bgColor = prefsManager.getBgColor()
        val textColor = prefsManager.getTextColor()

        val remoteViewId =
            if (textShadow) R.id.widget_text_shadow else R.id.widget_text
        val remoteViewLayout =
            if (textShadow) R.layout.widget_text_shadow else R.layout.widget_text
        val remoteViews = RemoteViews(context.packageName, remoteViewLayout)


        val date = HijriDate.todayFormatted(prefsManager)
        remoteViews.setTextViewText(remoteViewId, date)

        remoteViews.setTextViewTextSize(
            remoteViewId,
            TypedValue.COMPLEX_UNIT_SP,
            textSize
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && textColorMode == ColorMode.Dynamic) {
            remoteViews.setColorAttr(remoteViewId, "setTextColor", android.R.attr.colorPrimary)
        } else {
            remoteViews.setTextColor(remoteViewId, textColor.toArgb())
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier.fillMaxSize()
                .appWidgetBackground()
                .widgetCornerRadius()
                .background(bgColor)
                .clickable {
                    coroutineScope.launch {
                        HijriWidget().updateAll(context)
                        AlarmReceiver.setup24Periodic(context, prefsManager)
                    }
                }
        ) {
            AndroidRemoteViews(remoteViews)
        }
    }

    companion object {
        /**
         * Returns the Preferences of the first widget, or an empty Preferences if no widgets exist
         *
         * This is useful for getting default settings when no widgets are present
         */
        suspend fun firstWidgetOrEmptyPrefs(context: Context): Preferences {
            val appManager = GlanceAppWidgetManager(context)

            return appManager
                .getGlanceIds(HijriWidget::class.java)
                .firstOrNull()
                ?.let { id ->
                    getAppWidgetState(
                        context = context,
                        definition = PreferencesGlanceStateDefinition,
                        glanceId = id
                    )
                }.let {
                    it ?: PreferenceDataStoreFactory.create {
                        context.preferencesDataStoreFile("_empty")
                    } as Preferences
                }
        }
    }
}
