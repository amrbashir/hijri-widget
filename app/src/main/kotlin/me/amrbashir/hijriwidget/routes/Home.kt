package me.amrbashir.hijriwidget.routes

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.glance.appwidget.updateAll
import androidx.navigation.NavController
import kotlinx.coroutines.*
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.HijriWidget
import me.zhanghai.compose.preference.preference
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.Settings


@Composable
fun Home(navController: NavController, snackbarHostState: SnackbarHostState) {
    val lang by remember { mutableStateOf(Settings.language.value) }

    val coroutineScope = rememberCoroutineScope()


    var isRefreshing by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        preference(key = "language_page",
            title = { Text("Language") },
            summary = { Text("Choose the widget language ($lang)") },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_translate_24),
                    contentDescription = "Language"
                )
            },
            onClick = {
                navController.navigate("language")
            }
        )
        preference(key = "repopulateDatabase",
            title = { Text("Refresh Database") },
            summary = { Text("Refreshes the hijri database and updates the widget") },
            icon = {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = if (isRefreshing) {
                        Modifier.rotate(angle)
                    } else {
                        Modifier
                    }
                )
            },
            onClick = {
                isRefreshing = true
                coroutineScope.launch {
                    HijriWidget().apply {
                        Settings.load(navController.context)
                        HijriDate.populateDatabase(navController.context)
                        HijriDate.load(navController.context, Settings.language.value)
                        updateAll(navController.context)
                        isRefreshing = false
                        snackbarHostState
                            .showSnackbar(
                                "Success",
                            )

                    }
                }


            }
        )
    }
}