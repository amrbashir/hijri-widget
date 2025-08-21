package me.amrbashir.hijriwidget

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


val primaryLight = Color(0xFF65558F)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFEADDFF)
val onPrimaryContainerLight = Color(0xFF21005D)
val secondaryLight = Color(0xFF625B71)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFE8DEF8)
val onSecondaryContainerLight = Color(0xFF1D192B)
val tertiaryLight = Color(0xFF7D5260)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFD8E4)
val onTertiaryContainerLight = Color(0xFF31111D)
val errorLight = Color(0xFFB3261E)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFF9DEDC)
val onErrorContainerLight = Color(0xFF410E0B)
val backgroundLight = Color(0xFFFEF7FF)
val onBackgroundLight = Color(0xFF1D1B20)
val surfaceLight = Color(0xFFFEF7FF)
val onSurfaceLight = Color(0xFF1D1B20)
val surfaceVariantLight = Color(0xFFE7E0EC)
val onSurfaceVariantLight = Color(0xFF49454F)
val outlineLight = Color(0xFF79747E)
val outlineVariantLight = Color(0xFFCAC4D0)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF322F35)
val inverseOnSurfaceLight = Color(0xFFF5EFF7)
val inversePrimaryLight = Color(0xFFD0BCFF)
val surfaceDimLight = Color(0xFFDED8E1)
val surfaceBrightLight = Color(0xFFFEF7FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF7F2FA)
val surfaceContainerLight = Color(0xFFF3EDF7)
val surfaceContainerHighLight = Color(0xFFECE6F0)
val surfaceContainerHighestLight = Color(0xFFE6E0E9)

val primaryDark = Color(0xFFD0BCFE)
val onPrimaryDark = Color(0xFF381E72)
val primaryContainerDark = Color(0xFF4F378B)
val onPrimaryContainerDark = Color(0xFFEADDFF)
val secondaryDark = Color(0xFFCCC2DC)
val onSecondaryDark = Color(0xFF332D41)
val secondaryContainerDark = Color(0xFF4A4458)
val onSecondaryContainerDark = Color(0xFFE8DEF8)
val tertiaryDark = Color(0xFFEFB8C8)
val onTertiaryDark = Color(0xFF492532)
val tertiaryContainerDark = Color(0xFF633B48)
val onTertiaryContainerDark = Color(0xFFFFD8E4)
val errorDark = Color(0xFFF2B8B5)
val onErrorDark = Color(0xFF601410)
val errorContainerDark = Color(0xFF8C1D18)
val onErrorContainerDark = Color(0xFFF9DEDC)
val backgroundDark = Color(0xFF141218)
val onBackgroundDark = Color(0xFFE6E0E9)
val surfaceDark = Color(0xFF141218)
val onSurfaceDark = Color(0xFFE6E0E9)
val surfaceVariantDark = Color(0xFF49454F)
val onSurfaceVariantDark = Color(0xFFCAC4D0)
val outlineDark = Color(0xFF938F99)
val outlineVariantDark = Color(0xFF49454F)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE6E0E9)
val inverseOnSurfaceDark = Color(0xFF322F35)
val inversePrimaryDark = Color(0xFF6750A4)
val surfaceDimDark = Color(0xFF141218)
val surfaceBrightDark = Color(0xFF3B383E)
val surfaceContainerLowestDark = Color(0xFF0F0D13)
val surfaceContainerLowDark = Color(0xFF1D1B20)
val surfaceContainerDark = Color(0xFF211F26)
val surfaceContainerHighDark = Color(0xFF2B2930)
val surfaceContainerHighestDark = Color(0xFF36343B)

val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun PreferencesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

enum class SupportedTheme(val prettyName: String, val description: String) {
    Dynamic("Material You", "Dynamic color based on your wallpaper"),
    System("System", "Light or dark color based on device settings"),
    Dark("Dark", "Dark color, usually black"),
    Light("Light", "Light color, usually white"),
    Transparent("Transparent", "Transparent color"),
    Custom("Custom", "Pick a custom color");

    companion object {
        fun all(): MutableList<SupportedTheme> {
            val supportedThemes = mutableListOf(System, Dark, Light, Custom)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                supportedThemes.add(0, Dynamic)
            }
            return supportedThemes
        }

        fun allForBg(): MutableList<SupportedTheme> {
            val supportedThemes = this.all()
            val indexToAdd = if (supportedThemes[0] === Dynamic) 1 else 0
            supportedThemes.add(indexToAdd, Transparent)
            return supportedThemes
        }
    }
}

