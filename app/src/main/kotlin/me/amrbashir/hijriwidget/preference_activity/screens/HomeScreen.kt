package me.amrbashir.hijriwidget.preference_activity.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.navigation.NavGraphBuilder
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalWidgetState
import me.amrbashir.hijriwidget.preference_activity.WidgetState
import me.amrbashir.hijriwidget.preference_activity.animatedContentComposable
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceTemplate
import me.amrbashir.hijriwidget.preference_activity.screenPadding
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.SharedPreferencesListScreen
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.navigateToPreferencesList
import me.amrbashir.hijriwidget.widget.HijriWidget

const val HOME_DESTINATION = "/"

fun NavGraphBuilder.homeDestination() {
    animatedContentComposable(route = HOME_DESTINATION) { HomeScreen() }
}

@Composable
private fun HomeScreen() {
    LocalAppBarTitle.current.value = stringResource(R.string.app_name)

    val navController = LocalNavController.current
    val context = LocalContext.current

    val appWidgetManager = GlanceAppWidgetManager(context)
    val widgetsStates = remember { mutableStateListOf<WidgetState>() }

    LaunchedEffect(Unit) {
        appWidgetManager.getGlanceIds(HijriWidget::class.java).forEach { id ->
            val state = getAppWidgetState(
                context = context,
                definition = PreferencesGlanceStateDefinition,
                glanceId = id,
            )
            widgetsStates.add(WidgetState(id, state))
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.screenPadding()
    ) {
        if (!widgetsStates.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (state in widgetsStates) {
                    val sharedKey = "preference_list_${state.id}"

                    SharedPreferencesListScreen(
                        sharedKey = sharedKey,
                        modifier = Modifier.height(150.dp),
                        onClick = {
                            navController.navigateToPreferencesList( state.id, sharedKey)
                        },
                    )
                }
            }
        }

        PreferenceGroup {
            PreferenceTemplate(
                label = stringResource(R.string.preferences_about_title),
                description = stringResource(R.string.preferences_about_description),
                icon = Icons.Outlined.Info,
                onClick = {
                    navController.navigateToAbout()
                }
            )
        }
    }
}
