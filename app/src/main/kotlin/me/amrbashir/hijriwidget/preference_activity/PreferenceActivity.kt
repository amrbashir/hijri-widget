package me.amrbashir.hijriwidget.preference_activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.isDark
import me.amrbashir.hijriwidget.preference_activity.composables.TopAppBar


val LocalNavController = compositionLocalOf<NavHostController> {
    error("CompositionLocal LocalNavController not present")
}

val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> {
    error("CompositionLocal LocalSnackBarHostState not present")
}

@OptIn(ExperimentalMaterial3Api::class)
val LocalAppBarTitle = compositionLocalOf<MutableState<String>> {
    error("CompositionLocal LocalAppBarTitle not present")
}

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    error("CompositionLocal LocalSharedTransitionScope not present")
}

val LocalAnimatedContentScope = compositionLocalOf<AnimatedContentScope> {
    error("CompositionLocal LocalAnimatedContentScope not present")
}

val LocalPreferencesManager = compositionLocalOf<PreferencesManager> {
    error("CompositionLocal LocalPreferencesManager not present")
}

class PreferenceActivity() :
    ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PreferenceActivityContent()
        }
    }

    override fun onDestroy() {
        val prefsManager = PreferencesManager.load(this.baseContext)
        changeLauncherIcon(this.baseContext, prefsManager)
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PreferenceActivityContent(
    onSave: (suspend () -> Unit)? = null,
) {
    val navController = rememberNavController()

    val prefsManager = PreferencesManager.load(navController.context)
    val appBarTitle = remember { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }
    val topAppBarState = rememberTopAppBarState()
    val topAppBarScrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

    val widgetUpdatedMessage = stringResource(R.string.widget_updated)
    val defaultSaveAction: suspend () -> Unit = {
        snackBarHostState.showSnackbar(widgetUpdatedMessage)
    }

    PreferenceActivityTheme {
        val isDark = navController.context.isDark()
        val darkColor = MaterialTheme.colorScheme.surfaceContainer
        val lightColor = MaterialTheme.colorScheme.surfaceContainerLow

        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalSnackBarHostState provides snackBarHostState,
            LocalAppBarTitle provides appBarTitle,
            LocalPreferencesManager provides prefsManager,
        ) {
            Scaffold(
                containerColor = if (isDark) darkColor else lightColor,
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                topBar = {
                    TopAppBar(
                        scrollBehavior = topAppBarScrollBehavior,
                        onSave = onSave ?: defaultSaveAction
                    )
                },
                modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            ) {
                SharedTransitionLayout(
                    modifier = Modifier
                        .consumeWindowInsets(it)
                        .padding(it)
                ) {
                    CompositionLocalProvider(
                        LocalSharedTransitionScope provides this
                    ) {
                        Navigation()
                    }
                }
            }
        }
    }
}
