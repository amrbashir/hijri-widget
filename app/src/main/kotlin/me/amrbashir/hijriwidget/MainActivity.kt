package me.amrbashir.hijriwidget


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.compose.*
import me.amrbashir.hijriwidget.routes.*
import me.zhanghai.compose.preference.*


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Settings.load(this@MainActivity.baseContext)
        HijriDate.load(this@MainActivity.baseContext, Settings.language.value)
        setContent {
            Content()
        }
    }

    @Composable
    private fun Content() {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }

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
            MaterialTheme {
                ProvidePreferenceLocals {
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
    }

    @Composable
    private fun AppBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .height(200.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Actions()
                PreviewWidget()
                Text("Hijri Widget Settings", color = MaterialTheme.colorScheme.primary)
            }
        }
    }


    @Composable
    private fun Actions() {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { this@MainActivity.finish() }) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(onClick = {
                Settings.save(this@MainActivity.baseContext)
                this@MainActivity.finish()
            }) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

        }
    }

    @Composable
    private fun PreviewWidget() {
        var date by remember { mutableStateOf(HijriDate.today.value) }

        LaunchedEffect(Settings.language.value) {
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
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 7.em,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

