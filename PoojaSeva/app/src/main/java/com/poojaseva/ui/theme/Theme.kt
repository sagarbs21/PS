package com.poojaseva.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Saffron,
    onPrimary = Color.White,
    primaryContainer = IvorySoft,
    onPrimaryContainer = DeepIndigo,
    secondary = DeepIndigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE5E2FA),
    onSecondaryContainer = DeepIndigo,
    tertiary = Gold,
    onTertiary = Color.White,
    background = Ivory,
    onBackground = Charcoal,
    surface = Color.White,
    onSurface = Charcoal,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceMuted,
    outline = Color(0xFFD8C9A8),
    error = Maroon,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Saffron,
    onPrimary = Charcoal,
    primaryContainer = SaffronDark,
    onPrimaryContainer = Ivory,
    secondary = Color(0xFFB9B5F2),
    onSecondary = DeepIndigo,
    tertiary = Gold,
    onTertiary = Charcoal,
    background = SurfaceDark,
    onBackground = Ivory,
    surface = Color(0xFF1F1C40),
    onSurface = Ivory,
    surfaceVariant = Color(0xFF2A2755),
    onSurfaceVariant = Color(0xFFCFCBE8),
    outline = Color(0xFF4A4677),
    error = Color(0xFFFFB4AB),
    onError = Charcoal,
)

@Composable
fun PoojaSevaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = PoojaTypography,
        shapes = PoojaShapes,
        content = content
    )
}
