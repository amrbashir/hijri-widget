package me.amrbashir.hijriwidget.preferences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.PreferencesTheme
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.isDark
import me.amrbashir.hijriwidget.preferences.composables.WidgetPreview
import me.amrbashir.hijriwidget.preferences.routes.CalendarCalculation
import me.amrbashir.hijriwidget.preferences.routes.Color
import me.amrbashir.hijriwidget.preferences.routes.Format
import me.amrbashir.hijriwidget.preferences.routes.Home
import me.amrbashir.hijriwidget.widget.HijriWidget

object Route {
    const val HOME = "/"
    const val CALENDAR_CALCULATION_METHOD = "CalendarCalculationMethod"
    const val DATE_FORMAT = "DateFormat"
    const val COLOR_MODE = "ColorMode"
}

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("CompositionLocal LocalNavController not present")
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content() {
        val navController = rememberNavController()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()

        val snackBarHostState = remember { SnackbarHostState() }

        val coroutineScope = rememberCoroutineScope()

        val state = rememberTopAppBarState()
        val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state)

        PreferencesTheme {
            val containerColor =
                if (navController.context.isDark()) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surfaceContainerLow

            Scaffold(
                containerColor = containerColor,
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                topBar = {
                    LargeTopAppBar(
                        title = { Text("Hijri Widget") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = containerColor,
                            scrolledContainerColor = containerColor,
                        ),
                        scrollBehavior = topAppBarScrollBehavior,
                        navigationIcon = {
                            if (currentBackStackEntry?.destination?.route != Route.HOME) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        actions = {
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
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    )
                },
                modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            ) { padding ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .consumeWindowInsets(padding)
                        .padding(padding)
                ) {
                    WidgetPreview(navController.context)

                    CompositionLocalProvider(
                        LocalNavController provides navController,
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Route.HOME,
                            enterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(500)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(500)
                                )
                            },
                            popEnterTransition = {
                                slideIntoContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(500)
                                )
                            },
                            popExitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(500)
                                )
                            }
                        ) {
                            composable(Route.HOME) { Home() }
                            composable(Route.CALENDAR_CALCULATION_METHOD) { CalendarCalculation() }
                            composable(Route.DATE_FORMAT) { Format() }
                            composable(Route.COLOR_MODE) { Color() }
                        }
                    }
                }
            }
        }
    }


}
