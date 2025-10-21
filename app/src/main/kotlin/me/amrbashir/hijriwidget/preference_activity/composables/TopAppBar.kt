package me.amrbashir.hijriwidget.preference_activity.composables

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.PREFERENCES_LIST_DESTINATION
import me.amrbashir.hijriwidget.widget.HijriWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onSave: suspend () -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val darkColor = MaterialTheme.colorScheme.surfaceContainer
    val lightColor = MaterialTheme.colorScheme.surfaceContainerLow
    val containerColor = if (isDark) darkColor else lightColor

    LargeTopAppBar(
        title = { Text(LocalAppBarTitle.current.value) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor,
        ),
        scrollBehavior = scrollBehavior,
        navigationIcon = { GoBackButton() },
        actions = {
            SaveButton(
                onSave = onSave,
            )
        }
    )
}


@Composable
private fun GoBackButton() {
    val navController = LocalNavController.current
    val activity = LocalActivity.current

    val goBackAction: () -> Unit = {
        if (navController.currentDestination?.route == PREFERENCES_LIST_DESTINATION) {
            activity?.finish()
        } else {
            navController.navigateUp()
        }
    }

    IconButton(onClick = goBackAction) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = null
        )
    }
}

@Composable
private fun SaveButton(
    onSave: suspend () -> Unit,
) {
    val prefsManager = LocalPreferencesManager.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val saveAction: () -> Unit = {
        prefsManager.save(context)
        coroutineScope.launch {
            HijriWidget.updateAll(context)
            AlarmReceiver.setup24Periodic(context, prefsManager)
            onSave()
        }
    }

    IconButton(onClick = saveAction) {
        Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
        )
    }
}
