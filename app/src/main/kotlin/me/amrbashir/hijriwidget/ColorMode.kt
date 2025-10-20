package me.amrbashir.hijriwidget


import android.os.Build
import androidx.annotation.StringRes

enum class ColorMode(@param:StringRes val label: Int, @param:StringRes val description: Int) {
    Dynamic(
        R.string.color_mode_dynamic,
        R.string.color_mode_dynamic_description
    ),
    System(
        R.string.color_mode_system,
        R.string.color_mode_system_description
    ),
    Dark(
        R.string.color_mode_dark,
        R.string.color_mode_dark_description
    ),
    Light(
        R.string.color_mode_light,
        R.string.color_mode_light_description
    ),
    Transparent(
        R.string.color_mode_transparent,
        R.string.color_mode_transparent_description
    ),
    Custom(
        R.string.color_mode_custom,
        R.string.color_mode_custom_description
    );

    companion object {
        fun all(): List<ColorMode> {
            return listOfNotNull(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Dynamic else null,
                System, Dark, Light, Custom,
            )
        }

        fun allForBg(): List<ColorMode> = listOf(Transparent) + this.all()
    }
}
