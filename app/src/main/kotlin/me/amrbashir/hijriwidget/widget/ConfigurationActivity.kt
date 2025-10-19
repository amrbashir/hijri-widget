package me.amrbashir.hijriwidget.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import me.amrbashir.hijriwidget.preference_activity.PreferenceActivityContent

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

        setContent {
            PreferenceActivityContent(
                onSave = {
                    // Create the return intent, set it with the activity result, and finish the activity:
                    val res = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    setResult(RESULT_OK, res)
                    finish()
                }
            )
        }
    }
}
