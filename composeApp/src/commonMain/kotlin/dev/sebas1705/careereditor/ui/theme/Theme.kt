package dev.sebas1705.careereditor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Folio — "el backstage del portfolio".
 * Tinta índigo (marca) · teal = publicado/ok · ámbar = borrador/en progreso.
 */
private val ink = Color(0xFF4A5AF0)
private val inkContainer = Color(0xFFE2E5FF)
private val onInk = Color(0xFFFFFFFF)
private val onInkContainer = Color(0xFF101B7A)

private val teal = Color(0xFF00A896)
private val tealContainer = Color(0xFFC8F3EE)

private val amber = Color(0xFF9C6F19)
private val amberContainer = Color(0xFFFFE9C3)

private val error = Color(0xFFBA1A1A)
private val errorContainer = Color(0xFFFFDAD6)

private val LightColors = lightColorScheme(
    primary = ink,
    onPrimary = onInk,
    primaryContainer = inkContainer,
    onPrimaryContainer = onInkContainer,
    secondary = teal,
    secondaryContainer = tealContainer,
    onSecondaryContainer = Color(0xFF003732),
    tertiary = amber,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = amberContainer,
    onTertiaryContainer = Color(0xFF4A3400),
    error = error,
    errorContainer = errorContainer,
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF4F5FC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9EAF7),
    onSurfaceVariant = Color(0xFF454859),
    outline = Color(0xFF737695),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFBCC2FF),
    onPrimary = Color(0xFF16217E),
    primaryContainer = Color(0xFF3949CE),
    onPrimaryContainer = Color(0xFFDEE1FF),
    secondary = Color(0xFF6FDCCF),
    secondaryContainer = Color(0xFF00504A),
    onSecondaryContainer = Color(0xFF9FF2E8),
    tertiary = Color(0xFFEFBE4D),
    onTertiary = Color(0xFF402D00),
    tertiaryContainer = Color(0xFF5D4200),
    onTertiaryContainer = Color(0xFFFFDF9E),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0F1017),
    surface = Color(0xFF14151F),
    surfaceVariant = Color(0xFF1F2130),
    onSurface = Color(0xFFE4E3F1),
    onSurfaceVariant = Color(0xFFC6C7DD),
    outline = Color(0xFF8E90A8),
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
