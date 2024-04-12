package me.amrbashir.hijriwidget


import android.content.Context
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
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale


val HijriMonths = arrayOf(
    "Muharram",
    "Safar",
    "Rabi Al-Awwal",
    "Rabi Al-Thani",
    "Jumada Al-Awwal",
    "Jumada Al-Thani",
    "Rajab",
    "Shaban",
    "Ramadan",
    "Shawwal",
    "Du al-Qadah",
    "Du al-Hijjah"
)
val HijriMonthsAr = arrayOf(
    "محرم",
    "سفر",
    "ربيع الأول",
    "ربيع الثانى",
    "جمادى الأول",
    "جمادى الثانى",
    "رجب",
    "شعبان",
    "رمضان",
    "شوال",
    "ذو القعدة",
    "ذو الحجة"
)


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Settings.load(this.getPreferences(Context.MODE_PRIVATE))
        setContent {
            Content()
        }
    }

    @Composable
    private fun Content() {
        val navController = rememberNavController()

        Scaffold(
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
                            composable("home") { Home(navController) }
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
                Settings.save(this@MainActivity.getPreferences(Context.MODE_PRIVATE))
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
        val d = 3
        val y = 1445
        val m = 8


        var day by remember { mutableStateOf("$d") }
        var month by remember { mutableStateOf(HijriMonthsAr[m]) }
        var year by remember { mutableStateOf("$y") }

        when (Settings.language.value) {
            "English" -> {
                month = HijriMonths[m]
                day = "$d"
                year = "$y"
            }

            else -> {
                month = HijriMonthsAr[m]
                day = day.convertNumbersToAr()
                year = year.convertNumbersToAr()
            }
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
                    "$day $month $year",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 7.em,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


fun String.convertNumbersToAr(): String {
    return this.replace("0", "٠")
        .replace("1", "١")
        .replace("2", "٢")
        .replace("3", "٣")
        .replace("4", "٤")
        .replace("5", "٥")
        .replace("6", "٦")
        .replace("7", "٧")
        .replace("8", "٨")
        .replace("9", "٩")
}
