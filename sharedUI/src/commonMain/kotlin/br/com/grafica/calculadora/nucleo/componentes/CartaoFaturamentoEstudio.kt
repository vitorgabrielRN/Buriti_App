package br.com.grafica.calculadora.nucleo.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorEsmeraldaClaro
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

/**
 * Card de Faturamento Glass ("Modo estúdio"):
 * Exibe "Faturamento do mês", valor total em destaque dourado (#E3AE01),
 * contagem de pedidos finalizados e montante pendente em destaque verde esmeralda (#34D399).
 */
@Composable
fun CartaoFaturamentoEstudio(
    faturamentoMensal: Double,
    pedidosConcluidos: Int,
    valorAReceber: Double,
    modifier: Modifier = Modifier
) {
    CartaoGlass(modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Faturamento do mês",
                    style = MaterialTheme.typography.titleMedium,
                    color = CorBrancoSecundaria.copy(alpha = 0.85f)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.14f))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.22f),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✨ Modo estúdio",
                        style = MaterialTheme.typography.labelSmall,
                        color = CorDouradoTerciaria,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = formatarMoedaBr(faturamentoMensal),
                style = MaterialTheme.typography.displayMedium,
                color = CorDouradoTerciaria,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CorDouradoTerciaria)
                    )
                    Text(
                        text = "$pedidosConcluidos concluídos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CorBrancoSecundaria.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CorEsmeraldaClaro.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${formatarMoedaBr(valorAReceber)} a receber",
                        style = MaterialTheme.typography.labelLarge,
                        color = CorEsmeraldaClaro,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartaoFaturamentoEstudioPreview() {
    TemaAtelie {
        CartaoFaturamentoEstudio(
            faturamentoMensal = 2450.0,
            pedidosConcluidos = 14,
            valorAReceber = 480.0,
            modifier = Modifier.padding(16.dp)
        )
    }
}
