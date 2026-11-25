package br.com.grafica.calculadora.nucleo.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val EsquemaCoresAtelieClaro = lightColorScheme(
    primary = CorVinhoPrimaria,
    onPrimary = CorBrancoSecundaria,
    primaryContainer = CorVinhoContainer,
    onPrimaryContainer = CorSobreVinhoContainer,
    secondary = Color(0xFF6E4E62),
    onSecondary = CorBrancoSecundaria,
    secondaryContainer = Color(0xFFF4DCEB),
    onSecondaryContainer = Color(0xFF281320),
    tertiary = CorDouradoTerciaria,
    onTertiary = CorVinhoProfundo,
    tertiaryContainer = CorDouradoContainer,
    onTertiaryContainer = CorSobreDouradoContainer,
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = CorFundoSuperficie,
    onBackground = CorTextoEscuro,
    surface = CorFundoSuperficie,
    onSurface = CorTextoEscuro,
    surfaceVariant = Color(0xFFEDE0E6),
    onSurfaceVariant = Color(0xFF4E4449),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAF4F7),
    surfaceContainer = Color(0xFFF4ECF0),
    surfaceContainerHigh = Color(0xFFEEE4E9),
    surfaceContainerHighest = Color(0xFFE8DCE2),
    outline = Color(0xFF80747A),
    outlineVariant = CorContornoSutil
)

private val EsquemaCoresAtelieEscuro = darkColorScheme(
    primary = Color(0xFFFFB0DA),
    onPrimary = Color(0xFF540C3F),
    primaryContainer = Color(0xFF6F1F56),
    onPrimaryContainer = Color(0xFFFFD8EC),
    secondary = Color(0xFFD9BFCF),
    onSecondary = Color(0xFF3D2937),
    secondaryContainer = Color(0xFF553F4D),
    onSecondaryContainer = Color(0xFFF4DCEB),
    tertiary = CorDouradoTerciaria,
    onTertiary = Color(0xFF3E2E00),
    tertiaryContainer = Color(0xFF594400),
    onTertiaryContainer = Color(0xFFFFE08D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF181115),
    onBackground = Color(0xFFEDE0E4),
    surface = Color(0xFF181115),
    onSurface = Color(0xFFEDE0E4),
    surfaceVariant = Color(0xFF4E4449),
    onSurfaceVariant = Color(0xFFD1C3CA),
    surfaceContainerLowest = Color(0xFF120C10),
    surfaceContainerLow = Color(0xFF21191E),
    surfaceContainer = Color(0xFF251D22),
    surfaceContainerHigh = Color(0xFF30272C),
    surfaceContainerHighest = Color(0xFF3B3237),
    outline = Color(0xFF9A8D94),
    outlineVariant = Color(0xFF4E4449)
)

val FormasAtelie = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun TemaAtelie(
    temaEscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (temaEscuro) EsquemaCoresAtelieEscuro else EsquemaCoresAtelieClaro,
        typography = tipografiaAtelie(),
        shapes = FormasAtelie,
        content = content
    )
}
