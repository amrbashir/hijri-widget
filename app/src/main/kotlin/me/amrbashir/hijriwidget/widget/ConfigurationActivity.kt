package me.amrbashir.hijriwidget.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.preference_activity.PreferenceActivityTheme
import me.amrbashir.hijriwidget.preference_activity.PreferencesProvider
import me.amrbashir.hijriwidget.preference_activity.PreferencesScaffold
import me.amrbashir.hijriwidget.preference_activity.changeLauncherIcon
import me.amrbashir.hijriwidget.preference_activity.composables.WidgetSheet
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.PREFERENCES_DESTINATION

class WidgetConfigurationActivity : ComponentActivity() {
	// Get the App Widget ID from the intent that launched the activity
	private val appWidgetId: Int by lazy {
		intent?.extras?.getInt(
			AppWidgetManager.EXTRA_APPWIDGET_ID,
			AppWidgetManager.INVALID_APPWIDGET_ID,
		) ?: AppWidgetManager.INVALID_APPWIDGET_ID
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		// Set the activity result to RESULT_CANCELED.
		//
		// This way, if the user backs out of the activity before reaching the end,
		// the system notifies the app widget host that the configuration is canceled
		// and the host doesn't add the widget:
		val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
		setResult(RESULT_CANCELED, resultValue)

		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish()
			return
		}

		setContent {
			PreferenceActivityTheme {
				// The sheet floats over the home screen, so nothing is shown until it slides in and we don't need a loading state
				PreferencesProvider(appWidgetId = appWidgetId, loading = {}) {
					WidgetSheet(
						// Dismissing it cancels like backing out
						onDismiss = { finish() },
						onSave = {
							// Unlike the main activity, nothing else updates the launcher icon after this one
							val context = this@WidgetConfigurationActivity
							changeLauncherIcon(context, PreferencesManager.loadFirstOrDefault(context))

							// Create the return intent and set it with the activity result,
							// the sheet then slides out and finishes the activity
							val res = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
							setResult(RESULT_OK, res)
						},
					) {
						// Opened from a widget goes straight to its preferences
						PreferencesScaffold(startDestination = PREFERENCES_DESTINATION)
					}
				}
			}
		}
	}
}
