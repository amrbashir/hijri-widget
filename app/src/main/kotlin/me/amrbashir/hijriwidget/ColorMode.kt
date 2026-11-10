package me.amrbashir.hijriwidget

import android.os.Build
import androidx.annotation.StringRes

enum class ColorMode(
	@param:StringRes val label: Int,
	@param:StringRes val description: Int,
) {
	Dynamic(
		R.string.color_mode_dynamic,
		R.string.color_mode_dynamic_description,
	),
	DynamicPrimary(R.string.dynamic_color_primary, R.string.color_mode_dynamic_description),
	DynamicSecondary(R.string.dynamic_color_secondary, R.string.color_mode_dynamic_description),
	DynamicTertiary(R.string.dynamic_color_tertiary, R.string.color_mode_dynamic_description),
	DynamicPrimaryContainer(
		R.string.dynamic_color_primary_container,
		R.string.color_mode_dynamic_description,
	),
	DynamicSecondaryContainer(
		R.string.dynamic_color_secondary_container,
		R.string.color_mode_dynamic_description,
	),
	DynamicTertiaryContainer(
		R.string.dynamic_color_tertiary_container,
		R.string.color_mode_dynamic_description,
	),
	DeviceTheme(
		R.string.color_mode_device_theme,
		R.string.color_mode_device_theme_description,
	),
	Dark(
		R.string.color_mode_dark,
		R.string.color_mode_dark_description,
	),
	Light(
		R.string.color_mode_light,
		R.string.color_mode_light_description,
	),
	Transparent(
		R.string.color_mode_transparent,
		R.string.color_mode_transparent_description,
	),
	Custom(
		R.string.color_mode_custom,
		R.string.color_mode_custom_description,
	),
	;

	val isDynamic: Boolean
		get() =
			this in
				listOf(
					Dynamic,
					DynamicPrimary,
					DynamicSecondary,
					DynamicTertiary,
					DynamicPrimaryContainer,
					DynamicSecondaryContainer,
					DynamicTertiaryContainer,
				)

	companion object {
		fun all(): List<ColorMode> = dynamicModes(false) + listOf(DeviceTheme, Dark, Light, Custom)

		fun allForBg(): List<ColorMode> =
			listOf(Transparent) + dynamicModes(true) + listOf(DeviceTheme, Dark, Light, Custom)

		private fun dynamicModes(background: Boolean): List<ColorMode> =
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
				emptyList()
			} else if (background) {
				listOf(DynamicPrimaryContainer, DynamicSecondaryContainer, DynamicTertiaryContainer)
			} else {
				listOf(DynamicPrimary, DynamicSecondary, DynamicTertiary)
			}
	}
}
