package com.thecode.infotify.designsystem.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.thecode.infotify.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = Ember,
    onPrimary = SurfaceLight,
    primaryContainer = EmberContainerLight,
    onPrimaryContainer = OnEmberContainerLight,
    secondary = Slate,
    onSecondary = SurfaceLight,
    secondaryContainer = SlateContainerLight,
    onSecondaryContainer = InkLight,
    background = PaperLight,
    onBackground = InkLight,
    surface = SurfaceLight,
    onSurface = InkLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = InkMutedLight,
    outline = OutlineLight,
    outlineVariant = OutlineLight,
    error = ErrorLight,
    onError = SurfaceLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = ErrorLight
)

private val DarkColors = darkColorScheme(
    primary = EmberBright,
    onPrimary = Color(0xFF4A1D00),
    primaryContainer = EmberContainerDark,
    onPrimaryContainer = OnEmberContainerDark,
    secondary = SlateBright,
    onSecondary = PaperDark,
    secondaryContainer = SlateContainerDark,
    onSecondaryContainer = InkDark,
    background = PaperDark,
    onBackground = InkDark,
    surface = SurfaceDark,
    onSurface = InkDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = InkMutedDark,
    outline = OutlineDark,
    outlineVariant = OutlineDark,
    error = ErrorDark,
    onError = PaperDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = ErrorDark
)

private val InfotifyShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * [mode] resolves the user's choice; System defers to the OS, which is what the old
 * boolean night-mode flag could never express.
 *
 * Dynamic colour is deliberately not used: on Android 12+ it would replace the brand
 * orange with the user's wallpaper hue, and the orange is the app's identity.
 */
@Composable
fun InfotifyTheme(
    mode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit
) {
    val darkTheme = when (mode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                @Suppress("DEPRECATION")
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                @Suppress("DEPRECATION")
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = InfotifyTypography,
        shapes = InfotifyShapes,
        content = content
    )
}
