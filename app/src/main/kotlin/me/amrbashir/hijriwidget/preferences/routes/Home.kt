package me.amrbashir.hijriwidget.preferences.routes

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.preferences.LocalNavController
import me.amrbashir.hijriwidget.preferences.LocalSnackbarHostState
import me.amrbashir.hijriwidget.preferences.Route
import me.amrbashir.hijriwidget.preferences.composables.PreferenceCategory
import me.amrbashir.hijriwidget.widget.HijriWidget

@Composable
fun Home() {
    val navController = LocalNavController.current
    val snackbarHostState = LocalSnackbarHostState.current

    val coroutineScope = rememberCoroutineScope()

    var isSyncing by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = WindowInsets.safeContent
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            PreferenceCategory(
                label = "Language",
                description = "Choose the widget language",
                icon = ImageVector.vectorResource(R.drawable.baseline_translate_24),
                onClick = {
                    navController.navigate(Route.LANGUAGE)
                }
            )

            PreferenceCategory(
                label = "Theme and Color",
                description = "Choose the widget theme and color",
                icon = ImageVector.vectorResource(R.drawable.baseline_color_lens_24),
                onClick = {
                    navController.navigate(Route.THEME_AND_COLOR)
                }
            )

            PreferenceCategory(
                label = "Shadow",
                description = "Enable or disable the widget shadow",
                icon = ImageVector.vectorResource(R.drawable.baseline_brightness_6_24),
                rightContent = {
                    Switch(
                        checked = Preferences.shadow.value,
                        onCheckedChange = {
                            Preferences.shadow.value = it
                        }
                    )
                },
                onClick = {
                    Preferences.shadow.value = !Preferences.shadow.value
                }
            )
        }

        PreferenceCategory(
            label = "Sync Database",
            description = "Synchronize database and update the widget",
            icon = Icons.Default.Refresh,
            iconModifier = Modifier.rotate(if (isSyncing) angle else 0f),
            enabled = !isSyncing,
            onClick = {
                isSyncing = true
                coroutineScope.launch {
                    HijriDate.syncDatabase(navController.context)
                    HijriWidget.update(navController.context)
                    isSyncing = false
                    snackbarHostState.showSnackbar("Success", withDismissAction = true)
                }
            }
        )
    }
}