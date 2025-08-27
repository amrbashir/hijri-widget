package me.amrbashir.hijriwidget.preferences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.Preferences
import me.amrbashir.hijriwidget.PreferencesTheme
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.isDark
import me.amrbashir.hijriwidget.preferences.composables.WidgetPreview
import me.amrbashir.hijriwidget.widget.HijriWidget


val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("CompositionLocal LocalNavController not present")
}

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("CompositionLocal LocalSnackbarHostState not present")
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
        val navBackStackEntry by navController.currentBackStackEntryAsState()
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
                LocalSnackbarHostState provides snackBarHostState
            ) {

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
                            navigationIcon = { GoBackButton() },
                            actions = { SaveButton() }
                        )
                    },
                    modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .consumeWindowInsets(it)
                            .padding(it)
                    ) {
                        val preferencesRoute =
                            "me.amrbashir.hijriwidget.preferences.routes.preferences"
                        if (navBackStackEntry?.destination?.route?.startsWith(preferencesRoute)
                                ?: false
                        ) {
                            WidgetPreview()
                        }

                        Router()
                    }
                }
            }
        }
    }

    @Composable
    private fun GoBackButton() {
        val homeRoute =
            "me.amrbashir.hijriwidget.preferences.routes.preferences.PreferencesIndexRoute"
        val navController = LocalNavController.current

        IconButton(onClick = {
            if (navController.currentDestination?.route == homeRoute) {
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
