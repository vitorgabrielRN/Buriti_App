package br.com.grafica.calculadora.nucleo.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.nucleo.tema.CorPagamento100Fundo
import br.com.grafica.calculadora.nucleo.tema.CorPagamento100Texto
import br.com.grafica.calculadora.nucleo.tema.CorPagamento50Fundo
import br.com.grafica.calculadora.nucleo.tema.CorPagamento50Texto
import br.com.grafica.calculadora.nucleo.tema.CorPagamentoPendenteFundo
import br.com.grafica.calculadora.nucleo.tema.CorPagamentoPendenteTexto
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

enum class EstiloPagamentoBadge {
    PAGO_100,
    MEIO_A_MEIO,
    A_COBRAR
}

enum class EstiloEtapaBadge {
    ORCAMENTO,
    ARTE,
    PRODUCAO,
    PRONTO
}

@Composable
fun PilulaStatusPagamento(
    rotulo: String,
    estilo: EstiloPagamentoBadge,
    modifier: Modifier = Modifier
) {
    val (corFundo, corTexto) = when (estilo) {
        EstiloPagamentoBadge.PAGO_100 -> CorPagamento100Fundo to CorPagamento100Texto
        EstiloPagamentoBadge.MEIO_A_MEIO -> CorPagamento50Fundo to CorPagamento50Texto
        EstiloPagamentoBadge.A_COBRAR -> CorPagamentoPendenteFundo to CorPagamentoPendenteTexto
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(corFundo)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.labelSmall,
            color = corTexto,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PilulaEtapaPipeline(
    rotulo: String,
    estilo: EstiloEtapaBadge,
    modifier: Modifier = Modifier
) {
    val (corFundo, corTexto) = when (estilo) {
        EstiloEtapaBadge.ORCAMENTO -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        EstiloEtapaBadge.ARTE -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        EstiloEtapaBadge.PRODUCAO -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        EstiloEtapaBadge.PRONTO -> Color(0xFFDFF6E8) to Color(0xFF116337)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(corFundo)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.labelSmall,
            color = corTexto,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PilulasStatusPreview() {
    TemaAtelie {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PilulaStatusPagamento(rotulo = "100% pago", estilo = EstiloPagamentoBadge.PAGO_100)
            PilulaStatusPagamento(rotulo = "50% / 50%", estilo = EstiloPagamentoBadge.MEIO_A_MEIO)
            PilulaEtapaPipeline(rotulo = "Produção", estilo = EstiloEtapaBadge.PRODUCAO)
        }
    }
}
