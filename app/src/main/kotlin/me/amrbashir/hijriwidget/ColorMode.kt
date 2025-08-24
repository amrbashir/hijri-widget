package me.amrbashir.hijriwidget


import android.os.Build

enum class ColorMode(val prettyName: String, val description: String) {
    Dynamic("Material You", "Dynamic color based on your wallpaper"),
    System("System", "Light or dark color based on device settings"),
    Dark("Dark", "Dark color, usually black"),
    Light("Light", "Light color, usually white"),
    Transparent("Transparent", "Transparent color"),
    Custom("Custom", "Pick a custom color");

    companion object {
        fun all(): ArrayList<ColorMode> {
            val modes = arrayListOf(System, Dark, Light, Custom)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                modes.add(0, Dynamic)
            }
            return modes
        }

        fun allForBg(): ArrayList<ColorMode> {
            val modes = this.all()
            modes.add(modes.count() - 1, Transparent)
            return modes
        }
    }
}
