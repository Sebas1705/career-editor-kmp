package dev.sebas1705.careereditor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand palette — indigo / teal matching the portfolio
private val brand = Color(0xFF5B6CF9)       // primary
private val brandContainer = Color(0xFFE0E3FF)
private val onBrand = Color(0xFFFFFFFF)
private val onBrandContainer = Color(0xFF0E1578)

private val teal = Color(0xFF00B8A9)
private val tealContainer = Color(0xFFCCF5F2)

private val error = Color(0xFFBA1A1A)
private val errorContainer = Color(0xFFFFDAD6)

private val surfaceDark = Color(0xFF12131A)
private val surfaceVariantDark = Color(0xFF1E1F2E)
private val onSurfaceDark = Color(0xFFE3E2F0)

private val LightColors = lightColorScheme(
    primary = brand,
    onPrimary = onBrand,
    primaryContainer = brandContainer,
    onPrimaryContainer = onBrandContainer,
    secondary = teal,
    secondaryContainer = tealContainer,
    onSecondaryContainer = Color(0xFF003733),
    error = error,
    errorContainer = errorContainer,
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF6F7FF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE8E9F8),
    onSurfaceVariant = Color(0xFF44475A),
    outline = Color(0xFF767899),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFBCC2FF),
    onPrimary = Color(0xFF1A237E),
    primaryContainer = Color(0xFF3344C0),
    onPrimaryContainer = Color(0xFFDDE0FF),
    secondary = Color(0xFF7DD8CE),
    secondaryContainer = Color(0xFF004F4A),
    onSecondaryContainer = Color(0xFFA0F3EA),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = surfaceDark,
    surface = surfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurface = onSurfaceDark,
    onSurfaceVariant = Color(0xFFC5C6DC),
    outline = Color(0xFF8F90A7),
)

@Composable
fun CareerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = CareerTypography,
        shapes = CareerShapes,
        content = content
    )
}
