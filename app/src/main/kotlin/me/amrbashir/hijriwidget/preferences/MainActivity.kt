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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.PreferencesTheme
import me.amrbashir.hijriwidget.isDark
import me.amrbashir.hijriwidget.preferences.routes.Home
import me.amrbashir.hijriwidget.preferences.routes.Language
import me.amrbashir.hijriwidget.preferences.routes.TextSize
import me.amrbashir.hijriwidget.preferences.routes.TextColor
import me.amrbashir.hijriwidget.widget.HijriWidget

object Route {
    const val HOME = "/"
    const val LANGUAGE = "Language"
    const val TEXT_COLOR = "TextColor"
    const val TEXT_SIZE = "TextSize"
}

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("CompositionLocal LocalNavController not present")
}

class MainActivity: WidgetConfiguration(false)

open class WidgetConfiguration(private val autoClose: Boolean = true) : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Preferences.load(this.baseContext)
        HijriDate.load(Preferences.language.value)

        setContent {
            Content()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content() {
        val navController = rememberNavController()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        val coroutineScope = rememberCoroutineScope()

        val state =rememberTopAppBarState()
        val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state)

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
                                Preferences.save(this@WidgetConfiguration.baseContext)
                                coroutineScope.launch {
                                    HijriWidget.update(this@WidgetConfiguration.baseContext)
                                    if (this@WidgetConfiguration.autoClose) {
                                      this@WidgetConfiguration.finish()
                                    } else {
                                      snackbarHostState.showSnackbar("Widget updated!")
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
                Column(modifier = Modifier.padding(padding)) {
                    CompositionLocalProvider(
                        LocalNavController provides navController,
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
                            composable(Route.TEXT_COLOR) { TextColor() }
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
                HijriDate.todayForLang(Preferences.language.value)
            )
        }

        val textSize = if (Preferences.isCustomTextSize.value) Preferences.customTextSize.value.sp else 22.sp

        val textColor = Preferences.color.value

        val isDark = this@WidgetConfiguration.baseContext.isDark()
        val isEnoughLuminance = if (isDark) {
            ColorUtils.calculateLuminance(textColor) < 0.1
        } else {
            ColorUtils.calculateLuminance(textColor) > 0.5
        }
        val cardColor = if (isEnoughLuminance)
            MaterialTheme.colorScheme.surfaceTint
        else
            MaterialTheme.colorScheme.surfaceContainerHigh

        LaunchedEffect(Preferences.language.value, Preferences.color.value, HijriDate.today.value) {
            date = HijriDate.todayForLang(Preferences.language.value)
        }

        Box(Modifier.padding(all = 16.dp)) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors =  CardDefaults.elevatedCardColors(containerColor = cardColor),
            ) {
                Text(
                    date,
                    color = Color(textColor),
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        lineHeight = textSize,
                        shadow = if (Preferences.shadow.value) Shadow(
                            color = Color(0, 0, 0, 128),
                            offset = Offset(x = 1f, y = 1f),
                            blurRadius = 1f,
                        ) else null
                    ),
                    fontSize = textSize,
                )
            }
        }
    }
}
