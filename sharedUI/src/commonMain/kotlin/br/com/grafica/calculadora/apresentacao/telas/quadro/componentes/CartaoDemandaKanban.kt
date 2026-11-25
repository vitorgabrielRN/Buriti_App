package br.com.grafica.calculadora.apresentacao.telas.quadro.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoContainer
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@Composable
fun CartaoDemandaKanban(
    pedido: Pedido,
    aoClicar: () -> Unit,
    aoAvancarEtapa: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { aoClicar() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CorBrancoSecundaria),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CorVinhoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iconeProduto(pedido.nomeProduto),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pedido.codigoFormatado,
                            style = MaterialTheme.typography.titleSmall,
                            color = CorVinhoPrimaria,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = formatarMoedaBr(pedido.valorTotalFinal),
                            style = MaterialTheme.typography.labelLarge,
                            color = CorVinhoPrimaria,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = pedido.nomeProduto,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "📅 ${pedido.dataPrazoEntrega} • ${pedido.statusPagamento.rotulo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (pedido.etapaPipeline.proximaEtapa() != null) {
                Button(
                    onClick = aoAvancarEtapa,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CorVinhoPrimaria,
                        contentColor = CorBrancoSecundaria
                    ),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text(
                        text = pedido.etapaPipeline.rotuloAcaoAvancar(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .background(CorDouradoTerciaria.copy(alpha = 0.25f))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓ Pronto para retirada/entrega",
                        style = MaterialTheme.typography.labelLarge,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun iconeProduto(nomeProduto: String): String = when {
    nomeProduto.contains("Sacola", ignoreCase = true) -> "🛍️"
    nomeProduto.contains("Caderno", ignoreCase = true) ||
        nomeProduto.contains("Apostila", ignoreCase = true) -> "📓"
    nomeProduto.contains("Tag", ignoreCase = true) -> "🏷️"
    nomeProduto.contains("Cartão", ignoreCase = true) -> "🪪"
    nomeProduto.contains("Banner", ignoreCase = true) -> "🖼️"
    nomeProduto.contains("Adesivo", ignoreCase = true) -> "✨"
    else -> "🖨️"
}

@Preview(showBackground = true)
@Composable
private fun CartaoDemandaKanbanPreview() {
    TemaAtelie {
        CartaoDemandaKanban(
            pedido = Pedido(
                id = 8,
                nomeCliente = "Ateliê Sul",
                nomeProduto = "Banner Lona",
                especificacaoMaterial = "Lona 440g",
                especificacaoGramatura = "440g",
                especificacaoFormato = "1x1.5m",
                especificacaoAcabamento = "Colorido",
                especificacaoFaces = "Somente frente",
                valorTotalFinal = 135.0,
                etapaPipeline = EtapaPipeline.ORCAMENTO,
                statusPagamento = StatusPagamento.A_COBRAR
            ),
            aoClicar = {},
            aoAvancarEtapa = {}
        )
    }
}
