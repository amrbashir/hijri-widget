package me.amrbashir.hijriwidget

import android.view.Gravity
import androidx.compose.ui.text.style.TextAlign

enum class WidgetHorizontalAlignment(
	val gravity: Int,
	val bias: Float,
	val textAlign: TextAlign,
) {
	Start(Gravity.START, -1F, TextAlign.Start),
	Center(Gravity.CENTER_HORIZONTAL, 0F, TextAlign.Center),
	End(Gravity.END, 1F, TextAlign.End),
}

enum class WidgetVerticalAlignment(
	val gravity: Int,
	val bias: Float,
) {
	Top(Gravity.TOP, -1F),
	Center(Gravity.CENTER_VERTICAL, 0F),
	Bottom(Gravity.BOTTOM, 1F),
}
