package br.com.grafica.calculadora.nucleo.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoProfundo
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

/**
 * Componente reutilizável CartaoGlass ("Modo estúdio"):
 * - Fundo escuro em degradê vinho profundo (#540C3F para #1F0417)
 * - Superfície translúcida (Color.White.copy(alpha = 0.08f))
 * - Borda sutil (1.dp com gradiente linear de Color.White.copy(alpha = 0.25f) para 0.05f)
 * - Cantos arredondados (24.dp)
 */
@Composable
fun CartaoGlass(
    modifier: Modifier = Modifier,
    raioCanto: Dp = 24.dp,
    conteudo: @Composable BoxScope.() -> Unit
) {
    val forma = RoundedCornerShape(raioCanto)
    val pincelFundo = Brush.linearGradient(
        colors = listOf(CorVinhoPrimaria, CorVinhoProfundo),
        start = Offset(0f, 0f),
        end = Offset(900f, 600f)
    )
    val pincelBorda = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.25f),
            Color.White.copy(alpha = 0.05f)
        ),
        start = Offset(0f, 0f),
        end = Offset(600f, 600f)
    )

    Box(
        modifier = modifier
            .clip(forma)
            .background(pincelFundo)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.08f))
                .border(width = 1.dp, brush = pincelBorda, shape = forma)
                .padding(20.dp),
            content = conteudo
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartaoGlassPreview() {
    TemaAtelie {
        CartaoGlass(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Cartão Glassmorphism Ateliê",
                color = Color.White
            )
        }
    }
}
