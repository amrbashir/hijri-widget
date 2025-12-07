package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Brightness6
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.MoreTime
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material.icons.outlined.TextIncrease
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.formatTime
import me.amrbashir.hijriwidget.preference_activity.LocalAnimatedContentScope
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager
import me.amrbashir.hijriwidget.preference_activity.LocalSharedTransitionScope
import me.amrbashir.hijriwidget.preference_activity.LocalWidgetState
import me.amrbashir.hijriwidget.preference_activity.animatedContentComposable
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceTemplate
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ValueSlider

const val PREFERENCES_DESTINATION = "/preferences"
const val PREFERENCES_LIST_DESTINATION = "/preferences/"

fun NavGraphBuilder.preferenceListDestination() {
    animatedContentComposable(
        route = "$PREFERENCES_LIST_DESTINATION?sharedKey={sharedKey}",
        arguments = listOf(
            navArgument("sharedKey") {
                type = NavType.StringType
                nullable = false
            }
        )

    ) {
        val sharedKey = it.arguments?.getString("sharedKey")
        if (sharedKey != null) SharedPreferencesListScreen(sharedKey)
    }
}

fun NavController.navigateToPreferencesList(widgetId: GlanceId, sharedKey: String) {
    navigate(route = "$PREFERENCES_LIST_DESTINATION?sharedKey=$sharedKey")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PreferenceListScreen(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val prefsManager = LocalPreferencesManager.current
    val navController = LocalNavController.current

    PreferenceScreenLayout (
        modifier = modifier,
        onClick = onClick,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            PreferenceGroup(label = stringResource(R.string.preferences_functionality_group_title)) {
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_date_format_title),
                    description = stringResource(R.string.preferences_date_format_description),
                    icon = Icons.Outlined.Translate,
                    onClick = {
                        navController.navigateToDateFormat()
                    }
                )

                PreferenceTemplate(
                    label = stringResource(
                        R.string.preferences_day_start_title,
                        prefsManager.dayStart.value.formatTime() ?: ""
                    ),
                    description = stringResource(R.string.preferences_day_start_description),
                    icon = Icons.Outlined.AccessTime,
                    onClick = {
                        navController.navigateToDayStart()
                    }
                )

                PreferenceTemplate(
                    label = stringResource(R.string.preferences_calendar_calculation_method_title),
                    description = stringResource(R.string.preferences_calendar_calculation_method_description),
                    icon = Icons.Outlined.CalendarMonth,
                    onClick = {
                        navController.navigateToCalendarCalculationMethod()
                    }
                )

                PreferenceTemplate(
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

            PreferenceGroup(label = stringResource(R.string.preferences_appearance_group_title)) {
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_color_title),
                    description = stringResource(R.string.preferences_color_description),
                    icon = Icons.Outlined.ColorLens,
                    onClick = {
                        navController.navigateToColor()
                    }
                )

                PreferenceTemplate(
                    label = stringResource(R.string.preferences_text_size_title),
                    description = stringResource(R.string.preferences_text_size_description),
                    icon = Icons.Outlined.TextIncrease,
                ) {
                    ValueSlider(
                        value = prefsManager.textSize.value,
                        onValueChange = { prefsManager.textSize.value = it },
                        default = prefsManager.textSize.default,
                        valueRange = 1F..50F,
                        steps = 50,
                    )
                }

                PreferenceTemplate(
                    label = stringResource(R.string.preferences_text_shadow_title),
                    description = stringResource(R.string.preferences_text_shadow_description),
                    icon = Icons.Outlined.Brightness6,
                    endContent = {
                        Switch(
                            checked = prefsManager.textShadow.value,
                            onCheckedChange = {
                                prefsManager.textShadow.value = it
                            }
                        )
                    },
                    onClick = {
                        prefsManager.textShadow.value = !prefsManager.textShadow.value
                    }
                )
            }

            PreferenceGroup(label = stringResource(R.string.preferences_misc_group_title)) {
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_restore_defaults_title),
                    description = stringResource(R.string.preferences_restore_defaults_description),
                    icon = Icons.Outlined.SettingsBackupRestore,
                    onClick = {
                        prefsManager.reset()
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedPreferencesListScreen(
    sharedKey: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val sharedAnimatedContentScope = LocalAnimatedContentScope.current

    with(receiver = sharedTransitionScope) {
        PreferenceListScreen(
            onClick = onClick,
            modifier = modifier.sharedElement(
                sharedContentState = rememberSharedContentState(sharedKey),
                animatedVisibilityScope = sharedAnimatedContentScope
            ),
        )
    }
}
