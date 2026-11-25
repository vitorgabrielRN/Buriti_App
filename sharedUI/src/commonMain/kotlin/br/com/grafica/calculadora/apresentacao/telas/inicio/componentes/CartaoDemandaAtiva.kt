package br.com.grafica.calculadora.apresentacao.telas.inicio.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.nucleo.componentes.EstiloEtapaBadge
import br.com.grafica.calculadora.nucleo.componentes.EstiloPagamentoBadge
import br.com.grafica.calculadora.nucleo.componentes.PilulaEtapaPipeline
import br.com.grafica.calculadora.nucleo.componentes.PilulaStatusPagamento
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoContainer
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@Composable
fun CartaoDemandaAtiva(
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CorVinhoContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛍️", style = MaterialTheme.typography.titleMedium)
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = pedido.codigoFormatado,
                                style = MaterialTheme.typography.labelLarge,
                                color = CorVinhoPrimaria,
                                fontWeight = FontWeight.Bold
                            )
                            PilulaEtapaPipeline(
                                rotulo = pedido.etapaPipeline.rotulo,
                                estilo = pedido.etapaPipeline.paraEstiloBadge()
                            )
                        }
                        Text(
                            text = pedido.nomeProduto,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${pedido.nomeCliente} • ${pedido.quantidade} un",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatarMoedaBr(pedido.valorTotalFinal),
                        style = MaterialTheme.typography.titleLarge,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PilulaStatusPagamento(
                        rotulo = pedido.statusPagamento.rotulo,
                        estilo = pedido.statusPagamento.paraEstiloBadge()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val sufixoFornecedor = pedido.nomeFornecedor?.let { " ($it)" }.orEmpty()
                Text(
                    text = "📅 Prazo: ${pedido.dataPrazoEntrega} • ${pedido.modalidadeProducao.rotulo}$sufixoFornecedor",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (pedido.etapaPipeline.proximaEtapa() != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(CorVinhoPrimaria)
                            .clickable { aoAvancarEtapa() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = pedido.etapaPipeline.rotuloAcaoAvancar(),
                            style = MaterialTheme.typography.labelSmall,
                            color = CorBrancoSecundaria,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

fun EtapaPipeline.paraEstiloBadge(): EstiloEtapaBadge = when (this) {
    EtapaPipeline.ORCAMENTO -> EstiloEtapaBadge.ORCAMENTO
    EtapaPipeline.PRODUCAO -> EstiloEtapaBadge.PRODUCAO
    EtapaPipeline.PRONTO -> EstiloEtapaBadge.PRONTO
}

fun StatusPagamento.paraEstiloBadge(): EstiloPagamentoBadge = when (this) {
    StatusPagamento.PAGO_100 -> EstiloPagamentoBadge.PAGO_100
    StatusPagamento.MEIO_A_MEIO -> EstiloPagamentoBadge.MEIO_A_MEIO
    StatusPagamento.A_COBRAR -> EstiloPagamentoBadge.A_COBRAR
}

@Preview(showBackground = true)
@Composable
private fun CartaoDemandaAtivaPreview() {
    TemaAtelie {
        CartaoDemandaAtiva(
            pedido = Pedido(
                id = 12,
                nomeCliente = "Mariana Costa",
                nomeProduto = "Cartão de Visita",
                especificacaoMaterial = "Couchê",
                especificacaoGramatura = "300g",
                especificacaoFormato = "9x5cm",
                especificacaoAcabamento = "Colorido",
                especificacaoFaces = "Frente e verso",
                quantidade = 500,
                valorTotalFinal = 180.0,
                etapaPipeline = EtapaPipeline.PRODUCAO,
                statusPagamento = StatusPagamento.MEIO_A_MEIO
            ),
            aoClicar = {},
            aoAvancarEtapa = {}
        )
    }
}
