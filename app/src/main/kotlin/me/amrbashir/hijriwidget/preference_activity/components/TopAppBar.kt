package me.amrbashir.hijriwidget.preference_activity.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.isDark
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalSnackBarHostState
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.PREFERENCES_LIST_DESTINATION
import me.amrbashir.hijriwidget.widget.HijriWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    onFinish: () -> Unit,
    closeOnSave: Boolean = false,
) {
    val isDark = navController.context.isDark()
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
        navigationIcon = {
            GoBackButton(
                onFinish = onFinish
            )
        },
        actions = {
            SaveButton(
                onFinish = onFinish,
                closeOnSave = closeOnSave
            )
        }
    )
}


@Composable
private fun GoBackButton(
    onFinish: () -> Unit,
) {
    val navController = LocalNavController.current
    val context = navController.context

    IconButton(onClick = {
        if (navController.currentDestination?.route == PREFERENCES_LIST_DESTINATION) {
            onFinish()
        } else {
            navController.navigateUp()
        }
    }) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null
        )
    }
}

@Composable
private fun SaveButton(
    onFinish: () -> Unit,
    closeOnSave: Boolean
) {
    val navController = LocalNavController.current
    val snackBarHostState = LocalSnackBarHostState.current

    val context = navController.context

    val coroutineScope = rememberCoroutineScope()

    IconButton(onClick = {
        Preferences.save(context)
        coroutineScope.launch {
            HijriWidget.update(context)

            AlarmReceiver.setup24Periodic(context)

            if (closeOnSave) {
                onFinish()
            } else {
                snackBarHostState.showSnackbar("Widget updated!")
            }
        }
    }) {
        Icon(
            Icons.Default.Check,
            contentDescription = null,
        )
    }
}
