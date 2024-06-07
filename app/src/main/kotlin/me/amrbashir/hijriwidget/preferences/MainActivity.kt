package me.amrbashir.hijriwidget.preferences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.PreferencesTheme
import me.amrbashir.hijriwidget.preferences.routes.Home
import me.amrbashir.hijriwidget.preferences.routes.Language
import me.amrbashir.hijriwidget.preferences.routes.TextSize
import me.amrbashir.hijriwidget.preferences.routes.ThemeAndColor
import me.amrbashir.hijriwidget.widget.HijriWidget


object Route {
    const val HOME = "/"
    const val LANGUAGE = "Language"
    const val THEME_AND_COLOR = "ThemeAndColor"
    const val TEXT_SIZE = "TextSize"
}

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("CompositionLocal LocalNavController not present")
}

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("CompositionLocal LocalNavController not present")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        Preferences.load(this.baseContext)
//        runBlocking { HijriDate.syncDatabaseIfNot(this@MainActivity.baseContext) }
        HijriDate.load(this.baseContext, Preferences.language.value)

        setContent {
            Content()
        }
    }

    override fun onDestroy() {
        // TODO: find a better way to change icon when app is first installed
        HijriWidgetLauncherIconWorker.changeLauncherIcon(this.baseContext)
        super.onDestroy()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content() {
        val navController = rememberNavController()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        val topAppBarScrollBehavior =
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

        PreferencesTheme {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    LargeTopAppBar(
                        title = { Text("Hijri Widget") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
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
                                Preferences.save(this@MainActivity.baseContext)
                                runBlocking { HijriWidget.update(this@MainActivity.baseContext) }
                                this@MainActivity.finish()
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
                Column(modifier = Modifier.padding(padding)) {
                    CompositionLocalProvider(
                        LocalNavController provides navController,
                        LocalSnackbarHostState provides snackbarHostState
                    ) {
                        PreviewWidget()

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
                            composable(Route.LANGUAGE) { Language() }
                            composable(Route.THEME_AND_COLOR) { ThemeAndColor() }
                            composable(Route.TEXT_SIZE) { TextSize() }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PreviewWidget() {
        var date by remember {
            mutableStateOf(
                HijriDate.todayForLang(
                    this.baseContext,
                    Preferences.language.value
                )
            )
        }

        LaunchedEffect(Preferences.language.value, Preferences.color.value, HijriDate.today.value) {
            date = HijriDate.todayForLang(this@MainActivity.baseContext, Preferences.language.value)
        }


        Box(Modifier.padding(all = 16.dp)) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {

                Text(
                    date,
                    color = Color(Preferences.color.value),
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        shadow = if (Preferences.shadow.value) Shadow(
                            color = Color(0, 0, 0, 128),
                            offset = Offset(x = 1f, y = 1f),
                            blurRadius = 1f,
                        ) else null
                    ),
                    fontSize = if (Preferences.isCustomTextSize.value) Preferences.customTextSize.value.sp
                    else TextUnit.Unspecified,
                )
            }
        }
    }
}


