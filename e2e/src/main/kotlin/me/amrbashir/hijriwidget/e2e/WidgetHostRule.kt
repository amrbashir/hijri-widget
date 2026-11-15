package me.amrbashir.hijriwidget.e2e

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.ExternalResource

/**
 * Hosts the app's widgets in the test process, like a launcher would, without depending on
 * a specific launcher or touching the device's home screen.
 */
class WidgetHostRule : ExternalResource() {
	private val instrumentation = InstrumentationRegistry.getInstrumentation()
	private val context = instrumentation.context
	private val manager = AppWidgetManager.getInstance(context)
	private val host = AppWidgetHost(context, HOST_ID)

	override fun before() {
		shell("appwidget grantbind --package ${context.packageName}")
		// Widgets left behind by an interrupted run
		host.deleteHost()
		instrumentation.runOnMainSync { host.startListening() }
	}

	override fun after() {
		instrumentation.runOnMainSync { host.stopListening() }
		host.deleteHost()
		shell("appwidget revokebind --package ${context.packageName}")
	}

	/** Binds a new widget, which makes the system send it its first update */
	fun addWidget(): AppWidgetHostView {
		val id = host.allocateAppWidgetId()
		check(manager.bindAppWidgetIdIfAllowed(id, WIDGET_PROVIDER)) {
			"Could not bind a widget for $WIDGET_PROVIDER, is the app installed?"
		}
		val info = checkNotNull(manager.getAppWidgetInfo(id)) { "No provider info for widget $id" }

		lateinit var view: AppWidgetHostView
		instrumentation.runOnMainSync { view = host.createView(context, id, info) }
		return view
	}

	/** Waits until the widget shows text, failing if it stays on Glance's loading layout */
	fun awaitText(view: AppWidgetHostView): String {
		val deadline = SystemClock.uptimeMillis() + TIMEOUT_MS
		var loading = false
		while (SystemClock.uptimeMillis() < deadline) {
			var text: String? = null
			instrumentation.runOnMainSync {
				text = view.texts().firstOrNull { it.isNotBlank() }
				loading = view.any { it is ProgressBar }
			}
			text?.let { return it }
			SystemClock.sleep(POLL_MS)
		}

		val state = if (loading) "is still on the loading layout" else "never showed any text"
		throw AssertionError("Widget ${view.appWidgetId} $state after ${TIMEOUT_MS}ms")
	}

	private fun View.texts(): List<String> =
		when (this) {
			is TextView -> listOf(text.toString())
			is ViewGroup -> (0 until childCount).flatMap { getChildAt(it).texts() }
			else -> emptyList()
		}

	private fun View.any(predicate: (View) -> Boolean): Boolean =
		predicate(this) ||
			(this is ViewGroup && (0 until childCount).any { getChildAt(it).any(predicate) })

	private companion object {
		const val HOST_ID = 0x4877
		const val POLL_MS = 250L
	}
}
