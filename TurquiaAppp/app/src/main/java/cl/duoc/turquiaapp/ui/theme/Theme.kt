package cl.duoc.turquiaapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Verde de éxito para los íconos ✓ de campos válidos. */
val SuccessGreen = Color(0xFF2E7D32)

private val LightColors = lightColorScheme(
    primary = Color(0xFF00838F),            // turquesa
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB2EBF2),
    onPrimaryContainer = Color(0xFF00363C),
    secondary = Color(0xFFE30A17),          // rojo turco
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD6),
    onSecondaryContainer = Color(0xFF410002),
    tertiary = Color(0xFFFFB300),
    background = Color(0xFFF4FBFB),
    surface = Color.White,
    surfaceVariant = Color(0xFFDCEEEF),
    error = Color(0xFFB3261E)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4DD0E1),
    onPrimary = Color(0xFF00363C),
    primaryContainer = Color(0xFF004F57),
    onPrimaryContainer = Color(0xFFB2EBF2),
    secondary = Color(0xFFFFB4AB),
    onSecondary = Color(0xFF690005),
    secondaryContainer = Color(0xFF93000A),
    onSecondaryContainer = Color(0xFFFFDAD6),
    tertiary = Color(0xFFFFCA28),
    background = Color(0xFF0E1415),
    surface = Color(0xFF162022),
    surfaceVariant = Color(0xFF2B3A3C),
    error = Color(0xFFFFB4AB)
)

@Composable
fun TurquiaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
