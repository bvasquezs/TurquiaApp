package cl.duoc.turquiaapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Verde de éxito para los íconos ✓ de campos válidos. */
val SuccessGreen = Color(0xFF06D6A0)

private val LightColors = lightColorScheme(
    primary = Color(0xFF00B4D8),            // Turquesa brillante Capadocia
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCAF0F8),
    onPrimaryContainer = Color(0xFF003547),
    secondary = Color(0xFFFF595E),          // Rojo coral alegre
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE5E6),
    onSecondaryContainer = Color(0xFF5C0006),
    tertiary = Color(0xFFFFCA3A),           // Amarillo estrella gamificada
    onTertiary = Color(0xFF3D2C00),
    tertiaryContainer = Color(0xFFFFF3B0),
    onTertiaryContainer = Color(0xFF3D2C00),
    background = Color(0xFFFFFDF7),         // Crema cálido
    onBackground = Color(0xFF2B2D42),
    surface = Color.White,
    onSurface = Color(0xFF2B2D42),
    surfaceVariant = Color(0xFFE8F6F8),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = Color(0xFF90E0EF),
    error = Color(0xFFFF4D6D)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF48CAE4),
    onPrimary = Color(0xFF003547),
    primaryContainer = Color(0xFF0077B6),
    onPrimaryContainer = Color(0xFFCAF0F8),
    secondary = Color(0xFFFF758F),
    onSecondary = Color(0xFF590D22),
    secondaryContainer = Color(0xFFA4133C),
    onSecondaryContainer = Color(0xFFFFCCD5),
    tertiary = Color(0xFFFFD166),
    onTertiary = Color(0xFF3D2C00),
    background = Color(0xFF141B29),
    onBackground = Color(0xFFF8F9FA),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8F9FA),
    surfaceVariant = Color(0xFF2B3A4E),
    error = Color(0xFFFF758F)
)

private val KidsShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun TurquiaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = KidsShapes,
        content = content
    )
}

/** Modificador reutilizable estilo botón/tarjeta 3D infantil (inspirado en Duolingo) */
fun Modifier.tarjetaInfantil3D(
    colorFondo: Color,
    colorBordeSombra: Color,
    radio: Dp = 24.dp,
    grosorSombra: Dp = 6.dp
): Modifier = this
    .drawBehind {
        drawRoundRect(
            color = colorBordeSombra,
            topLeft = Offset(0f, grosorSombra.toPx()),
            size = size,
            cornerRadius = CornerRadius(radio.toPx(), radio.toPx())
        )
    }
    .clip(RoundedCornerShape(radio))
    .background(colorFondo)
    .border(2.dp, colorBordeSombra, RoundedCornerShape(radio))
    .padding(bottom = grosorSombra)