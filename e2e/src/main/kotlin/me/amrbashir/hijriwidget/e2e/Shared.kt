package me.amrbashir.hijriwidget.e2e

import android.content.ComponentName
import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry

/** The app under test, built with the minified `e2e` build type */
const val APP_PACKAGE = "me.amrbashir.hijriwidget.e2e"

/** Kept by its manifest entry, so R8 never renames it */
val WIDGET_PROVIDER =
	ComponentName(APP_PACKAGE, "me.amrbashir.hijriwidget.widget.HijriWidgetReceiver")

const val TIMEOUT_MS = 30_000L

/** Years 1400-1599 in Western or Eastern Arabic digits, depending on the device language */
val HIJRI_YEAR = Regex("""1[45]\d{2}|١[٤٥][٠-٩]{2}""")

fun shell(command: String): String {
	val output = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
	return ParcelFileDescriptor.AutoCloseInputStream(output).bufferedReader().use { it.readText() }
}
