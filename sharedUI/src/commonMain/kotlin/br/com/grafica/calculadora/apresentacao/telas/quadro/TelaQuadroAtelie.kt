package br.com.grafica.calculadora.apresentacao.telas.quadro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.quadro.componentes.ColunaEtapaKanban
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

@Composable
fun TelaKanban(
    estado: QuadroAtelieEstadoUi,
    aoEnviarEvento: (QuadroAtelieEventoUi) -> Unit,
    aoSelecionarPedido: (Pedido) -> Unit,
    preenchimentoConteudo: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(preenchimentoConteudo)
            .padding(top = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Pipeline de produção",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Fila de Demandas",
                style = MaterialTheme.typography.displaySmall,
                color = CorVinhoPrimaria,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(EtapaPipeline.entries, key = { it.name }) { etapa ->
                val pedidosEtapa = estado.pedidosPorEtapa[etapa].orEmpty()
                ColunaEtapaKanban(
                    etapa = etapa,
                    pedidos = pedidosEtapa,
                    aoClicarPedido = aoSelecionarPedido,
                    aoAvancarEtapa = { pedido ->
                        aoEnviarEvento(QuadroAtelieEventoUi.AvancarEtapa(pedido))
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TelaKanbanPreview() {
    TemaAtelie {
        TelaKanban(
            estado = QuadroAtelieEstadoUi(
                pedidosPorEtapa = mapOf(
                    EtapaPipeline.ORCAMENTO to listOf(
                        Pedido(
                            id = 1,
                            nomeCliente = "Cliente A",
                            nomeProduto = "Cartão de Visita",
                            especificacaoMaterial = "Couchê",
                            especificacaoGramatura = "300g",
                            especificacaoFormato = "9x5cm",
                            especificacaoAcabamento = "Colorido",
                            especificacaoFaces = "Frente e verso",
                            valorTotalFinal = 120.0,
                            etapaPipeline = EtapaPipeline.ORCAMENTO
                        )
                    )
                )
            ),
            aoEnviarEvento = {},
            aoSelecionarPedido = {}
        )
    }
}
