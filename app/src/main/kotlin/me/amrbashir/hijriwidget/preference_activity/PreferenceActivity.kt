package me.amrbashir.hijriwidget.preference_activity

import android.graphics.PixelFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.amrbashir.hijriwidget.PreferencesManager
import me.amrbashir.hijriwidget.android.HijriWidgetApp
import me.amrbashir.hijriwidget.preference_activity.composables.MainNavigationBar
import me.amrbashir.hijriwidget.preference_activity.composables.TopAppBar
import me.amrbashir.hijriwidget.preference_activity.composables.mainNavigationBlur
import me.amrbashir.hijriwidget.preference_activity.screens.HOME_DESTINATION
import me.amrbashir.hijriwidget.preference_activity.screens.preferences.navigateToPreferences

class PreferenceActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		// Widget previews clear their pixels to show the wallpaper, an opaque window would show black instead
		window.setFormat(PixelFormat.TRANSLUCENT)

		setContent {
			PreferenceActivityContent()
		}
	}

	override fun onDestroy() {
		super.onDestroy()

		val context = applicationContext

		// Loading preferences is disk I/O, so don't block the main thread on it
		HijriWidgetApp.scope.launch {
			changeLauncherIcon(context, PreferencesManager.loadFirstOrDefault(context))
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreferenceActivityContent() {
	PreferenceActivityTheme {
		PreferencesProvider(
			appWidgetId = null,
			loading = { LoadingBackground() },
		) {
			val topAppBarState = rememberTopAppBarState()
			val topAppBarScrollBehavior =
				TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

			PreferencesScaffold(
				startDestination = HOME_DESTINATION,
				modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
				topBar = {
					TopAppBar(
						scrollBehavior = topAppBarScrollBehavior,
					)
				},
			)
		}
	}
}

/** An empty background shown while the selected widget is loaded */
@Composable
private fun LoadingBackground() {
	Box(
		modifier =
			Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.darkLightContainerColor),
	)
}

/**
 * Provides the state shared by the preferences screens to [content],
 * showing [loading] instead until the selected widget is loaded, as the screens require it.
 */
@Composable
fun PreferencesProvider(
	appWidgetId: Int?,
	loading: @Composable () -> Unit,
	content: @Composable () -> Unit,
) {
	val navController = rememberNavController()
	val context = LocalContext.current

	// Only the id is saved, so the selected widget survives activity recreation
	val selectedWidgetId = rememberSaveable { mutableStateOf(appWidgetId) }
	val selectedWidget = remember { mutableStateOf<WidgetInstance?>(null) }

	// Load the selected widget if only its id is known, e.g. when opened from a widget or activity recreated
	LaunchedEffect(selectedWidgetId.value) {
		val id = selectedWidgetId.value ?: return@LaunchedEffect
		if (selectedWidget.value?.appWidgetId != id) {
			selectedWidget.value = WidgetInstance.load(context, id)
		}
	}

	val openWidgetPrefs: (WidgetInstance) -> Unit = {
		selectedWidget.value = it
		selectedWidgetId.value = it.appWidgetId
		navController.navigateToPreferences()
	}

	val widget = selectedWidget.value
	if (selectedWidgetId.value != null && widget == null) {
		loading()
		return
	}

	val appBarTitle = remember { mutableStateOf("") }
	val snackBarHostState = remember { SnackbarHostState() }

	CompositionLocalProvider(
		LocalNavController provides navController,
		LocalSnackBarHostState provides snackBarHostState,
		LocalAppBarTitle provides appBarTitle,
		LocalOpenWidgetPrefs provides openWidgetPrefs,
		LocalSelectedWidget provides widget,
		content = content,
	)
}

/** The preferences screens, starting at [startDestination] */
@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScaffold(
	startDestination: String,
	modifier: Modifier = Modifier,
	showMainNavigation: Boolean = true,
	topBar: @Composable () -> Unit = {},
) {
	Scaffold(
		containerColor = MaterialTheme.colorScheme.darkLightContainerColor,
		snackbarHost = { SnackbarHost(hostState = LocalSnackBarHostState.current) },
		topBar = {
			if (showMainNavigation) {
				topBar()
			}
		},
		modifier = modifier,
	) { insets ->
		val layoutDirection = LocalLayoutDirection.current
		val contentInsets =
			PaddingValues(
				start = insets.calculateStartPadding(layoutDirection),
				top = insets.calculateTopPadding(),
				end = insets.calculateEndPadding(layoutDirection),
				bottom = if (showMainNavigation) 0.dp else insets.calculateBottomPadding(),
			)
		SharedTransitionLayout(
			modifier =
				Modifier
					.consumeWindowInsets(contentInsets)
					.padding(contentInsets),
		) {
			CompositionLocalProvider(
				LocalSharedTransitionScope provides this,
				LocalShowMainNavigation provides showMainNavigation,
			) {
				Box {
					Navigation(
						startDestination = startDestination,
						modifier =
							Modifier
								.fillMaxSize()
								// Src preserves preview holes that reveal the wallpaper through the blur layer.
								// Match the Scaffold background so opaque areas keep the same appearance.
								.graphicsLayer { blendMode = BlendMode.Src }
								.mainNavigationBlur(enabled = showMainNavigation)
								.background(MaterialTheme.colorScheme.darkLightContainerColor),
					)

					if (showMainNavigation) {
						MainNavigationBar(
							modifier =
								Modifier
									.align(
										Alignment.BottomCenter,
									).fillMaxWidth(),
						)
					}
				}
			}
		}
	}
}
