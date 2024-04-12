package me.amrbashir.hijriwidget.routes

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import me.zhanghai.compose.preference.preference
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.Settings


@Composable
fun Home(navController: NavController) {
    val lang by remember { mutableStateOf(Settings.language.value) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        preference(key = "language_page",
            title = { Text("Language") },
            summary = { Text("Choose the widget language. ($lang)") },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_translate_24),
                    contentDescription = "Language"
                )
            },
            onClick = {
                navController.navigate("language")
            })
    }
}