package me.amrbashir.hijriwidget.e2e

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetTest {
	@get:Rule
	val widgets = WidgetHostRule()

	@Test
	fun newWidgetShowsHijriDate() {
		val text = widgets.awaitText(widgets.addWidget())

		assertTrue("Expected a Hijri date with a year, got \"$text\"", HIJRI_YEAR.containsMatchIn(text))
	}

	@Test
	fun multipleWidgetsRender() {
		val views = List(2) { widgets.addWidget() }

		for (view in views) {
			widgets.awaitText(view)
		}
	}
}
