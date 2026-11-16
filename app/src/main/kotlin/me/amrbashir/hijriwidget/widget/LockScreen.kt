package me.amrbashir.hijriwidget.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.SizeF

/**
 * Samsung's lock screen tints widgets with the clock color, so the text color is ignored and
 * a background would cover the text, and it sizes each tile at about 123x54 dp.
 */
object LockScreen {
	/** Tile size, in dp, of Samsung's lock screen */
	val tileSize = SizeF(123F, 54F)

	fun isLockScreenWidget(
		context: Context,
		appWidgetId: Int,
	): Boolean {
		val info = AppWidgetManager.getInstance(context).getAppWidgetInfo(appWidgetId)
		return info?.provider?.className == HijriLockScreenWidgetReceiver::class.java.name
	}
}
