package me.amrbashir.hijriwidget.settings


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.runBlocking
import me.amrbashir.hijriwidget.HijriDate
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.Settings
import me.amrbashir.hijriwidget.settings.routes.Home
import me.amrbashir.hijriwidget.settings.routes.Language
import me.amrbashir.hijriwidget.widget.HijriWidget


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()

        Settings.load(this.baseContext)
        runBlocking { HijriDate.syncDatabaseIfNot(this@MainActivity.baseContext) }
        HijriDate.load(this.baseContext, Settings.language.value)

        setContent {
            Content()
        }
    }

    @Composable
    private fun Content() {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }

        AppTheme {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                topBar = {
                    TopAppBar(
                        modifier = Modifier.height(
                            TopAppBarDefaults.windowInsets.asPaddingValues().calculateTopPadding()
                        ),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                        title = { }
                    )
                }
            ) {
                Column(modifier = Modifier.padding(it)) {
                    AppBar()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { Home(navController, snackbarHostState) }
                        composable("language") { Language(navController) }
                    }
                }

            }
        }
    }

    @Composable
    private fun AppBar() {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .height(200.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Actions()
                PreviewWidget()
            }
        }
    }


    @Composable
    private fun Actions() {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { this@MainActivity.finish() }) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Text("Hijri Widget Settings", color = MaterialTheme.colorScheme.onSurface)

            IconButton(onClick = {
                Settings.save(this@MainActivity.baseContext)
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
    }

    @Composable
    private fun PreviewWidget() {
        var date by remember {
            mutableStateOf(
                HijriDate.todayForLang(
                    this.baseContext,
                    Settings.language.value
                )
            )
        }

        LaunchedEffect(Settings.language.value, HijriDate.today.value) {
            date = HijriDate.todayForLang(this@MainActivity.baseContext, Settings.language.value)
        }

        Box(Modifier.padding(16.dp)) {
            Box(modifier = Modifier.clip(RoundedCornerShape(5.dp))) {
                Image(
                    painter = painterResource(R.drawable.bg),
                    contentDescription = "example background",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = 6.0F
                            scaleY = 6.0F
                        }
                )

                Text(
                    date,
                    color = Color.White,
                    fontSize = 7.em,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}