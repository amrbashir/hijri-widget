package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewQuilt
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Brightness6
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatColorFill
import androidx.compose.material.icons.outlined.MoreTime
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material.icons.outlined.TextIncrease
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.ColorMode
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.formatTime
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalSelectedWidget
import me.amrbashir.hijriwidget.preference_activity.animatedContentComposable
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.ScrollableScreenColumn
import me.amrbashir.hijriwidget.preference_activity.composables.SwatchRow
import me.amrbashir.hijriwidget.preference_activity.composables.ui.Item
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ItemGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ValueSlider
import kotlin.math.roundToInt

const val PREFERENCES_DESTINATION = "/preferences"
const val PREFERENCES_LIST_DESTINATION = "/preferences/"

fun NavGraphBuilder.preferenceListDestination() {
	animatedContentComposable(route = PREFERENCES_LIST_DESTINATION) { PreferenceListScreen() }
}

fun NavController.navigateToPreferences() {
	navigate(route = PREFERENCES_DESTINATION)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PreferenceListScreen() {
	LocalAppBarTitle.current.value = stringResource(R.string.app_name)

	val prefsManager = LocalSelectedWidget.current.prefsManager
	val navController = LocalNavController.current
	val weights = prefsManager.supportedFontWeights()

	PreferenceScreenLayout {
		ScrollableScreenColumn(
			verticalArrangement = Arrangement.spacedBy(16.dp),
			modifier =
				Modifier
					.fillMaxSize()
					.imePadding(),
		) {
			ItemGroup(
				label = stringResource(R.string.preferences_functionality_group_title),
			) {
				Item(
					label = stringResource(R.string.preferences_date_format_title),
					description = stringResource(R.string.preferences_date_format_description),
					icon = Icons.Outlined.Translate,
					onClick = {
						navController.navigateToDateFormat()
					},
				)

				Item(
					label =
						stringResource(
							R.string.preferences_day_start_title,
							prefsManager.dayStart.value.formatTime() ?: "",
						),
					description = stringResource(R.string.preferences_day_start_description),
					icon = Icons.Outlined.AccessTime,
					onClick = {
						navController.navigateToDayStart()
					},
				)

				Item(
					label = stringResource(R.string.preferences_calendar_calculation_method_title),
					description =
						stringResource(
							R.string.preferences_calendar_calculation_method_description,
						),
					icon = Icons.Outlined.CalendarMonth,
					onClick = {
						navController.navigateToCalendarCalculationMethod()
					},
				)

				Item(
					label = stringResource(R.string.preferences_day_offset_title),
					description = stringResource(R.string.preferences_day_offset_description),
					icon = Icons.Outlined.MoreTime,
				) {
					ValueSlider(
						value = prefsManager.dayOffset.value.toFloat(),
						onValueChange = { prefsManager.dayOffset.value = it.toInt() },
						default = prefsManager.dayOffset.default.toFloat(),
						valueRange = -2F..2F,
						steps = 3,
					)
				}
			}

			ItemGroup(label = stringResource(R.string.preferences_appearance_group_title)) {
				val context = LocalContext.current
				Item(
					label = stringResource(R.string.text_color),
					icon = Icons.Outlined.ColorLens,
				) {
					SwatchRow(
						modes = ColorMode.all(),
						selectedMode = prefsManager.textColorMode.value,
						previewColor = { prefsManager.getTextColor(context, it) },
						onModeSelected = {
							prefsManager.textColorMode.value = it
							if (it == ColorMode.Custom) navController.navigateToCustomTextColor()
						},
					)
				}

				Item(
					label = stringResource(R.string.background_color),
					icon = Icons.Outlined.FormatColorFill,
				) {
					SwatchRow(
						modes = ColorMode.allForBg(),
						selectedMode = prefsManager.bgColorMode.value,
						previewColor = { prefsManager.getBgColor(context, it).getColor(context) },
						onModeSelected = {
							prefsManager.bgColorMode.value = it
							if (it == ColorMode.Custom) navController.navigateToCustomBackgroundColor()
						},
					)
				}

				Item(
					label = stringResource(R.string.preferences_text_shadow_title),
					description = stringResource(R.string.preferences_text_shadow_description),
					icon = Icons.Outlined.Brightness6,
					endContent = {
						Switch(
							checked = prefsManager.textShadow.value,
							onCheckedChange = {
								prefsManager.textShadow.value = it
							},
						)
					},
					onClick = {
						prefsManager.textShadow.value = !prefsManager.textShadow.value
					},
				)

				Item(
					label = stringResource(R.string.preferences_font_size_title),
					description = stringResource(R.string.preferences_font_size_description),
					icon = Icons.Outlined.TextIncrease,
				) {
					ValueSlider(
						value = prefsManager.fontSize.value,
						onValueChange = { prefsManager.fontSize.value = it },
						default = prefsManager.fontSize.default,
						valueRange = 1F..50F,
						steps = 50,
					)
				}

				Item(
					label = stringResource(R.string.font_family),
					description =
						prefsManager.textFontFamily.value.ifEmpty {
							stringResource(R.string.font_system_default)
						},
					icon = Icons.Outlined.FontDownload,
					onClick = { navController.navigateToFont() },
				)

				Item(
					label = stringResource(R.string.font_weight),
					description = stringResource(R.string.font_weight_hint),
					icon = Icons.Outlined.FormatBold,
				) {
					if (weights.size == 1) {
						Text(weights.single().toString())
					} else {
						val defaultWeightIndex =
							weights.indices.minBy {
								kotlin.math.abs(weights[it] - prefsManager.textWeight.default)
							}

						ValueSlider(
							value = weights.indexOf(prefsManager.getFontWeight()).toFloat(),
							onValueChange = { value ->
								prefsManager.textWeight.value = weights[value.roundToInt()].toFloat()
							},
							default = defaultWeightIndex.toFloat(),
							valueLabel = prefsManager.getFontWeight().toString(),
							valueRange = 0F..weights.lastIndex.toFloat(),
							steps = weights.size - 2,
						)
					}
				}

				Item(
					label = stringResource(R.string.preferences_layout_title),
					description = stringResource(R.string.preferences_layout_description),
					icon = Icons.AutoMirrored.Outlined.ViewQuilt,
					onClick = { navController.navigateToLayout() },
				)
			}

			ItemGroup(label = stringResource(R.string.preferences_misc_group_title)) {
				Item(
					label = stringResource(R.string.preferences_restore_defaults_title),
					description = stringResource(R.string.preferences_restore_defaults_description),
					icon = Icons.Outlined.SettingsBackupRestore,
					onClick = {
						prefsManager.reset()
					},
				)
			}
		}
	}
}
