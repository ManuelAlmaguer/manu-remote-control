package com.manu.remotecontrol.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.manu.remotecontrol.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = Indigo40,
    onPrimary = Color.White,
    primaryContainer = Indigo90,
    onPrimaryContainer = Indigo10,
    secondary = Teal40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF042F2E),
    tertiary = Amber40,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF451A03),
    error = Red40,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF450A0A),
    background = SurfaceLight,
    onBackground = Color(0xFF1A1A22),
    surface = SurfaceLight,
    onSurface = Color(0xFF1A1A22),
    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = Color(0xFF4B4B5A),
    outline = Color(0xFF7A7A8C),
    outlineVariant = Color(0xFFD8D6E4)
)

private val DarkColors = darkColorScheme(
    primary = Indigo80,
    onPrimary = Indigo10,
    primaryContainer = Indigo20,
    onPrimaryContainer = Indigo90,
    secondary = Teal80,
    onSecondary = Color(0xFF042F2E),
    secondaryContainer = Color(0xFF115E59),
    onSecondaryContainer = Color(0xFFCCFBF1),
    tertiary = Amber80,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFEF3C7),
    error = Red80,
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    background = SurfaceDark,
    onBackground = Slate90,
    surface = SurfaceDark,
    onSurface = Slate90,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = Color(0xFFB9B8C6),
    outline = Color(0xFF8B8A9B),
    outlineVariant = Color(0xFF33333F)
)

/** Colores semánticos extra que no forman parte de ColorScheme. */
data class ManuExtraColors(
    val powerRed: Color,
    val irLed: Color,
    val simulated: Color,
    val success: Color
)

val LocalManuExtraColors = androidx.compose.runtime.staticCompositionLocalOf {
    ManuExtraColors(
        powerRed = Red40,
        irLed = Indigo40,
        simulated = Amber40,
        success = Teal40
    )
}

@Composable
fun ManuRemoteTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && supportsDynamic ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

        darkTheme -> DarkColors
        else -> LightColors
    }

    val extras = ManuExtraColors(
        powerRed = if (darkTheme) Red80 else Red40,
        irLed = if (darkTheme) Indigo80 else Indigo40,
        simulated = if (darkTheme) Amber80 else Amber40,
        success = if (darkTheme) Teal80 else Teal40
    )

    androidx.compose.runtime.CompositionLocalProvider(LocalManuExtraColors provides extras) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ManuTypography,
            shapes = ManuShapes,
            content = content
        )
    }
}
