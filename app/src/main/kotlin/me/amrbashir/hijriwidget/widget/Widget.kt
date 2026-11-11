package me.amrbashir.hijriwidget.widget

import android.content.Context
import android.os.Build
import android.text.SpannableString
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
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
import androidx.glance.appwidget.state.updateAppWidgetState
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
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.addIfNotNull
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.applyFontWeight
import me.amrbashir.hijriwidget.applyHorizontalAlignment
import me.amrbashir.hijriwidget.logException
import me.amrbashir.hijriwidget.widgetCornerRadius
import kotlin.math.roundToInt

class HijriWidgetReceiver : GlanceAppWidgetReceiver() {
	override val glanceAppWidget: GlanceAppWidget = HijriWidget()
}

class HijriWidget : GlanceAppWidget() {
	override val sizeMode: SizeMode = SizeMode.Single
	override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

	override fun onCompositionError(
		context: Context,
		glanceId: GlanceId,
		appWidgetId: Int,
		throwable: Throwable,
	) {
		logException(throwable)
		super.onCompositionError(context, glanceId, appWidgetId, throwable)
	}

	override suspend fun provideGlance(
		context: Context,
		id: GlanceId,
	) {
		// Stamped once, when the widget is first shown, to order widgets by when they were added
		updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
			if (prefs[ADDED_AT] != null) {
				prefs
			} else {
				prefs.toMutablePreferences().apply { this[ADDED_AT] = System.currentTimeMillis() }
			}
		}

		provideContent {
			val prefs = currentState<Preferences>()
			val prefsManager = PreferencesManager.fromPreferences(prefs)

			GlanceTheme {
				Content(prefsManager, id)
			}
		}
	}

	override suspend fun providePreview(
		context: Context,
		widgetCategory: Int,
	) {
		provideContent {
			GlanceTheme {
				Content(PreferencesManager())
			}
		}
	}

	@Composable
	private fun Content(
		prefsManager: PreferencesManager,
		id: GlanceId? = null,
	) {
		val context = LocalContext.current
		val coroutineScope = rememberCoroutineScope()

		val date = HijriDate.todayFormatted(prefsManager)

		val textShadow = prefsManager.textShadow.value

		val remoteViewId = if (textShadow) R.id.widget_text_shadow else R.id.widget_text
		val layout = if (textShadow) R.layout.widget_text_shadow else R.layout.widget_text
		val remoteViews = RemoteViews(context.packageName, layout)

		val isDeviceRTL = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
		val text =
			SpannableString(date).apply {
				applyFontWeight(context, prefsManager.textFontFamily.value, prefsManager.getFontWeight())
				applyHorizontalAlignment(prefsManager.horizontalAlignment.value, isDeviceRTL)
			}
		remoteViews.setTextViewText(remoteViewId, text)

		val horizontalAlignment = prefsManager.horizontalAlignment.value
		val verticalAlignment = prefsManager.verticalAlignment.value
		val gravity = horizontalAlignment.gravity or verticalAlignment.gravity
		remoteViews.setInt(R.id.widget_text_container, "setGravity", gravity)

		val density = context.resources.displayMetrics.density
		val start = (prefsManager.paddingStart.value * density).roundToInt()
		val end = (prefsManager.paddingEnd.value * density).roundToInt()
		remoteViews.setViewPadding(
			R.id.widget_text_container,
			if (isDeviceRTL) end else start,
			(prefsManager.paddingTop.value * density).roundToInt(),
			if (isDeviceRTL) start else end,
			(prefsManager.paddingBottom.value * density).roundToInt(),
		)

		remoteViews.setTextViewTextSize(
			remoteViewId,
			TypedValue.COMPLEX_UNIT_SP,
			prefsManager.fontSize.value,
		)

		val textColorMode = prefsManager.textColorMode.value
		val textColor = prefsManager.getTextColor(context)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && textColorMode == ColorMode.Dynamic) {
			remoteViews.setColorAttr(remoteViewId, "setTextColor", android.R.attr.colorPrimary)
		} else {
			remoteViews.setTextColor(remoteViewId, textColor.toArgb())
		}

		val bgColor = prefsManager.getBgColor(context)

		Box(
			contentAlignment = Alignment.Center,
			modifier =
				GlanceModifier
					.fillMaxSize()
					.appWidgetBackground()
					.widgetCornerRadius()
					.background(bgColor)
					// Previews have no id, so nothing to refresh
					.addIfNotNull(id) { id ->
						clickable {
							coroutineScope.launch {
								refresh(context, id)
								AlarmReceiver.scheduleNextUpdate(context)
							}
						}
					},
		) {
			AndroidRemoteViews(remoteViews, modifier = GlanceModifier.fillMaxSize())
		}
	}

	companion object {
		/**
		 * The date is not part of the widget state, so a running widget won't recompose
		 * when the day changes, bumping this key in its state forces it to.
		 */
		private val LAST_REFRESH = longPreferencesKey("LAST_REFRESH")

		/** When the widget was added, used to order widgets, see [glanceIds] */
		val ADDED_AT = longPreferencesKey("ADDED_AT")

		suspend fun refresh(
			context: Context,
			id: GlanceId,
		) {
			updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
				prefs.toMutablePreferences().apply { this[LAST_REFRESH] = System.currentTimeMillis() }
			}
			HijriWidget().update(context, id)
		}

		/** Ids of all placed widgets, ordered by when they were added, ties broken by `appWidgetId` */
		suspend fun glanceIds(context: Context): List<GlanceId> {
			val manager = GlanceAppWidgetManager(context)
			return manager
				.getGlanceIds(HijriWidget::class.java)
				.map { id ->
					// Not shown yet, so it's the newest
					val addedAt =
						getAppWidgetState(context, PreferencesGlanceStateDefinition, id)[ADDED_AT] ?: Long.MAX_VALUE
					Triple(id, addedAt, manager.getAppWidgetId(id))
				}.sortedWith(compareBy({ it.second }, { it.third }))
				.map { it.first }
		}

		suspend fun refreshAll(context: Context) {
			for (id in glanceIds(context)) {
				refresh(context, id)
			}
		}
	}
}
