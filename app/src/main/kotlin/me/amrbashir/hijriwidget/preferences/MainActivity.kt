package me.amrbashir.hijriwidget.preferences

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.PreferencesTheme
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.isDark
import me.amrbashir.hijriwidget.preferences.routes.preferences.PREFERENCES_INDEX_ROUTE
import me.amrbashir.hijriwidget.widget.HijriWidget


val LocalNavController = compositionLocalOf<NavHostController> {
    error("CompositionLocal LocalNavController not present")
}

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("CompositionLocal LocalSnackbarHostState not present")
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

class MainActivity : WidgetConfiguration(false)

open class WidgetConfiguration(private val autoClose: Boolean = true) : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Preferences.load(this.baseContext)
        HijriDate.load()

        setContent {
            Content()
        }
    }

    override fun onDestroy() {
        changeLauncherIcon(this.baseContext)
        super.onDestroy()
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
    @Composable
    private fun Content() {
        val appBarTitle = remember { mutableStateOf("Hijri Widget") }
        val navController = rememberNavController()
        val snackBarHostState = remember { SnackbarHostState() }
        val topAppBarState = rememberTopAppBarState()
        val topAppBarScrollBehavior =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)


        PreferencesTheme {
            val isDark = navController.context.isDark()
            val darkColor = MaterialTheme.colorScheme.surfaceContainer
            val lightColor = MaterialTheme.colorScheme.surfaceContainerLow
            val containerColor = if (isDark) darkColor else lightColor

            CompositionLocalProvider(
                LocalNavController provides navController,
                LocalSnackbarHostState provides snackBarHostState,
                LocalAppBarTitle provides appBarTitle,
            ) {
                Scaffold(
                    containerColor = containerColor,
                    snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                    topBar = {
                        LargeTopAppBar(
                            title = { Text(appBarTitle.value) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = containerColor,
                                scrolledContainerColor = containerColor,
                            ),
                            scrollBehavior = topAppBarScrollBehavior,
                            navigationIcon = { GoBackButton() },
                            actions = { SaveButton() }
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
                            Router()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun GoBackButton() {
        val navController = LocalNavController.current

        IconButton(onClick = {
            if (navController.currentDestination?.route == PREFERENCES_INDEX_ROUTE) {
                this@WidgetConfiguration.finish()
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
    private fun SaveButton() {
        val coroutineScope = rememberCoroutineScope()

        val snackBarHostState = LocalSnackbarHostState.current

        IconButton(onClick = {
            Preferences.save(this@WidgetConfiguration.baseContext)
            coroutineScope.launch {
                HijriWidget.update(this@WidgetConfiguration.baseContext)

                AlarmReceiver.setup24Periodic(this@WidgetConfiguration.baseContext)

                if (this@WidgetConfiguration.autoClose) {
                    this@WidgetConfiguration.finish()
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
}
