package me.amrbashir.hijriwidget.settings

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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

val primaryLightMediumContrast = Color(0xFF493971)
val onPrimaryLightMediumContrast = Color(0xFFFFFFFF)
val primaryContainerLightMediumContrast = Color(0xFF7B6BA7)
val onPrimaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val secondaryLightMediumContrast = Color(0xFF493971)
val onSecondaryLightMediumContrast = Color(0xFFFFFFFF)
val secondaryContainerLightMediumContrast = Color(0xFF7C6BA7)
val onSecondaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryLightMediumContrast = Color(0xFF6B2F45)
val onTertiaryLightMediumContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightMediumContrast = Color(0xFFA55F77)
val onTertiaryContainerLightMediumContrast = Color(0xFFFFFFFF)
val errorLightMediumContrast = Color(0xFF6E2F2C)
val onErrorLightMediumContrast = Color(0xFFFFFFFF)
val errorContainerLightMediumContrast = Color(0xFFAA5F5A)
val onErrorContainerLightMediumContrast = Color(0xFFFFFFFF)
val backgroundLightMediumContrast = Color(0xFFFDF7FF)
val onBackgroundLightMediumContrast = Color(0xFF1D1B20)
val surfaceLightMediumContrast = Color(0xFFFDF7FF)
val onSurfaceLightMediumContrast = Color(0xFF1D1B20)
val surfaceVariantLightMediumContrast = Color(0xFFE7E0EB)
val onSurfaceVariantLightMediumContrast = Color(0xFF45414A)
val outlineLightMediumContrast = Color(0xFF615D67)
val outlineVariantLightMediumContrast = Color(0xFF7D7983)
val scrimLightMediumContrast = Color(0xFF000000)
val inverseSurfaceLightMediumContrast = Color(0xFF322F35)
val inverseOnSurfaceLightMediumContrast = Color(0xFFF5EFF7)
val inversePrimaryLightMediumContrast = Color(0xFFCFBDFE)
val surfaceDimLightMediumContrast = Color(0xFFDED8E0)
val surfaceBrightLightMediumContrast = Color(0xFFFDF7FF)
val surfaceContainerLowestLightMediumContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightMediumContrast = Color(0xFFF8F2FA)
val surfaceContainerLightMediumContrast = Color(0xFFF2ECF4)
val surfaceContainerHighLightMediumContrast = Color(0xFFECE6EE)
val surfaceContainerHighestLightMediumContrast = Color(0xFFE6E0E9)

val primaryLightHighContrast = Color(0xFF27174E)
val onPrimaryLightHighContrast = Color(0xFFFFFFFF)
val primaryContainerLightHighContrast = Color(0xFF493971)
val onPrimaryContainerLightHighContrast = Color(0xFFFFFFFF)
val secondaryLightHighContrast = Color(0xFF27174E)
val onSecondaryLightHighContrast = Color(0xFFFFFFFF)
val secondaryContainerLightHighContrast = Color(0xFF493971)
val onSecondaryContainerLightHighContrast = Color(0xFFFFFFFF)
val tertiaryLightHighContrast = Color(0xFF420E24)
val onTertiaryLightHighContrast = Color(0xFFFFFFFF)
val tertiaryContainerLightHighContrast = Color(0xFF6B2F45)
val onTertiaryContainerLightHighContrast = Color(0xFFFFFFFF)
val errorLightHighContrast = Color(0xFF440F0E)
val onErrorLightHighContrast = Color(0xFFFFFFFF)
val errorContainerLightHighContrast = Color(0xFF6E2F2C)
val onErrorContainerLightHighContrast = Color(0xFFFFFFFF)
val backgroundLightHighContrast = Color(0xFFFDF7FF)
val onBackgroundLightHighContrast = Color(0xFF1D1B20)
val surfaceLightHighContrast = Color(0xFFFDF7FF)
val onSurfaceLightHighContrast = Color(0xFF000000)
val surfaceVariantLightHighContrast = Color(0xFFE7E0EB)
val onSurfaceVariantLightHighContrast = Color(0xFF25232B)
val outlineLightHighContrast = Color(0xFF45414A)
val outlineVariantLightHighContrast = Color(0xFF45414A)
val scrimLightHighContrast = Color(0xFF000000)
val inverseSurfaceLightHighContrast = Color(0xFF322F35)
val inverseOnSurfaceLightHighContrast = Color(0xFFFFFFFF)
val inversePrimaryLightHighContrast = Color(0xFFF1E8FF)
val surfaceDimLightHighContrast = Color(0xFFDED8E0)
val surfaceBrightLightHighContrast = Color(0xFFFDF7FF)
val surfaceContainerLowestLightHighContrast = Color(0xFFFFFFFF)
val surfaceContainerLowLightHighContrast = Color(0xFFF8F2FA)
val surfaceContainerLightHighContrast = Color(0xFFF2ECF4)
val surfaceContainerHighLightHighContrast = Color(0xFFECE6EE)
val surfaceContainerHighestLightHighContrast = Color(0xFFE6E0E9)

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

val primaryDarkMediumContrast = Color(0xFFD3C1FF)
val onPrimaryDarkMediumContrast = Color(0xFF1B0942)
val primaryContainerDarkMediumContrast = Color(0xFF9887C5)
val onPrimaryContainerDarkMediumContrast = Color(0xFF000000)
val secondaryDarkMediumContrast = Color(0xFFD3C1FF)
val onSecondaryDarkMediumContrast = Color(0xFF1B0942)
val secondaryContainerDarkMediumContrast = Color(0xFF9887C5)
val onSecondaryContainerDarkMediumContrast = Color(0xFF000000)
val tertiaryDarkMediumContrast = Color(0xFFFFB7CD)
val onTertiaryDarkMediumContrast = Color(0xFF330218)
val tertiaryContainerDarkMediumContrast = Color(0xFFC57B93)
val onTertiaryContainerDarkMediumContrast = Color(0xFF000000)
val errorDarkMediumContrast = Color(0xFFFFB9B3)
val onErrorDarkMediumContrast = Color(0xFF330405)
val errorContainerDarkMediumContrast = Color(0xFFCC7B74)
val onErrorContainerDarkMediumContrast = Color(0xFF000000)
val backgroundDarkMediumContrast = Color(0xFF141218)
val onBackgroundDarkMediumContrast = Color(0xFFE6E0E9)
val surfaceDarkMediumContrast = Color(0xFF141218)
val onSurfaceDarkMediumContrast = Color(0xFFFFF9FF)
val surfaceVariantDarkMediumContrast = Color(0xFF49454E)
val onSurfaceVariantDarkMediumContrast = Color(0xFFCEC8D4)
val outlineDarkMediumContrast = Color(0xFFA6A1AB)
val outlineVariantDarkMediumContrast = Color(0xFF86818B)
val scrimDarkMediumContrast = Color(0xFF000000)
val inverseSurfaceDarkMediumContrast = Color(0xFFE6E0E9)
val inverseOnSurfaceDarkMediumContrast = Color(0xFF2B292F)
val inversePrimaryDarkMediumContrast = Color(0xFF4E3F77)
val surfaceDimDarkMediumContrast = Color(0xFF141218)
val surfaceBrightDarkMediumContrast = Color(0xFF3B383E)
val surfaceContainerLowestDarkMediumContrast = Color(0xFF0F0D13)
val surfaceContainerLowDarkMediumContrast = Color(0xFF1D1B20)
val surfaceContainerDarkMediumContrast = Color(0xFF211F24)
val surfaceContainerHighDarkMediumContrast = Color(0xFF2B292F)
val surfaceContainerHighestDarkMediumContrast = Color(0xFF36343A)

val primaryDarkHighContrast = Color(0xFFFFF9FF)
val onPrimaryDarkHighContrast = Color(0xFF000000)
val primaryContainerDarkHighContrast = Color(0xFFD3C1FF)
val onPrimaryContainerDarkHighContrast = Color(0xFF000000)
val secondaryDarkHighContrast = Color(0xFFFFF9FF)
val onSecondaryDarkHighContrast = Color(0xFF000000)
val secondaryContainerDarkHighContrast = Color(0xFFD3C1FF)
val onSecondaryContainerDarkHighContrast = Color(0xFF000000)
val tertiaryDarkHighContrast = Color(0xFFFFF9F9)
val onTertiaryDarkHighContrast = Color(0xFF000000)
val tertiaryContainerDarkHighContrast = Color(0xFFFFB7CD)
val onTertiaryContainerDarkHighContrast = Color(0xFF000000)
val errorDarkHighContrast = Color(0xFFFFF9F9)
val onErrorDarkHighContrast = Color(0xFF000000)
val errorContainerDarkHighContrast = Color(0xFFFFB9B3)
val onErrorContainerDarkHighContrast = Color(0xFF000000)
val backgroundDarkHighContrast = Color(0xFF141218)
val onBackgroundDarkHighContrast = Color(0xFFE6E0E9)
val surfaceDarkHighContrast = Color(0xFF141218)
val onSurfaceDarkHighContrast = Color(0xFFFFFFFF)
val surfaceVariantDarkHighContrast = Color(0xFF49454E)
val onSurfaceVariantDarkHighContrast = Color(0xFFFFF9FF)
val outlineDarkHighContrast = Color(0xFFCEC8D4)
val outlineVariantDarkHighContrast = Color(0xFFCEC8D4)
val scrimDarkHighContrast = Color(0xFF000000)
val inverseSurfaceDarkHighContrast = Color(0xFFE6E0E9)
val inverseOnSurfaceDarkHighContrast = Color(0xFF000000)
val inversePrimaryDarkHighContrast = Color(0xFF2F2056)
val surfaceDimDarkHighContrast = Color(0xFF141218)
val surfaceBrightDarkHighContrast = Color(0xFF3B383E)
val surfaceContainerLowestDarkHighContrast = Color(0xFF0F0D13)
val surfaceContainerLowDarkHighContrast = Color(0xFF1D1B20)
val surfaceContainerDarkHighContrast = Color(0xFF211F24)
val surfaceContainerHighDarkHighContrast = Color(0xFF2B292F)
val surfaceContainerHighestDarkHighContrast = Color(0xFF36343A)

private val lightScheme = lightColorScheme(
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

private val darkScheme = darkColorScheme(
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

private val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

private val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

private val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

private val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
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
        window.statusBarColor = colorScheme.surface.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

