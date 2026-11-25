package br.com.grafica.calculadora.apresentacao.telas.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.pedidos.componentes.CabecalhoColunasTabelaPedidos
import br.com.grafica.calculadora.apresentacao.telas.pedidos.componentes.ChipFiltroPagamento
import br.com.grafica.calculadora.apresentacao.telas.pedidos.componentes.LinhaTabelaPedidoEditavel
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorContornoSutil
import br.com.grafica.calculadora.nucleo.tema.CorVinhoContainer
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@Composable
fun TelaRegistroPedidos(
    estado: RegistroPedidosEstadoUi,
    aoEnviarEvento: (RegistroPedidosEventoUi) -> Unit,
    aoClicarPedido: (Pedido) -> Unit,
    preenchimentoConteudo: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(preenchimentoConteudo)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                Text(
                    text = "Pedidos",
                    style = MaterialTheme.typography.displaySmall,
                    color = CorVinhoPrimaria,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(CorVinhoContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = estado.pedidosFiltrados.size.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { aoEnviarEvento(RegistroPedidosEventoUi.AlternarOrdenacaoPagamento) },
                shape = RoundedCornerShape(50),
                color = CorBrancoSecundaria,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .border(1.dp, CorContornoSutil, RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "↓↑ Pagamento",
                        style = MaterialTheme.typography.labelLarge,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Text(
            text = "Toque em qualquer registro da tabela para editar todos os dados",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(20.dp),
            color = CorBrancoSecundaria,
            shadowElevation = 3.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                CabecalhoColunasTabelaPedidos()
                HorizontalDivider(color = CorContornoSutil.copy(alpha = 0.6f))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(estado.pedidosFiltrados, key = { it.id }) { pedido ->
                        LinhaTabelaPedidoEditavel(
                            pedido = pedido,
                            aoClicar = { aoClicarPedido(pedido) }
                        )
                        HorizontalDivider(color = CorContornoSutil.copy(alpha = 0.35f))
                    }
                }

                HorizontalDivider(color = CorContornoSutil)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAF7F9))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formatarMoedaBr(estado.somaTotalFiltrada),
                        style = MaterialTheme.typography.titleLarge,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ChipFiltroPagamento(
                    rotulo = "Todos",
                    selecionado = estado.filtroPagamentoSelecionado == null,
                    aoClicar = {
                        aoEnviarEvento(RegistroPedidosEventoUi.SelecionarFiltroPagamento(null))
                    }
                )
            }
            item {
                ChipFiltroPagamento(
                    rotulo = StatusPagamento.PAGO_100.rotulo,
                    selecionado = estado.filtroPagamentoSelecionado == StatusPagamento.PAGO_100,
                    aoClicar = {
                        aoEnviarEvento(
                            RegistroPedidosEventoUi.SelecionarFiltroPagamento(StatusPagamento.PAGO_100)
                        )
                    }
                )
            }
            item {
                ChipFiltroPagamento(
                    rotulo = StatusPagamento.MEIO_A_MEIO.rotulo,
                    selecionado = estado.filtroPagamentoSelecionado == StatusPagamento.MEIO_A_MEIO,
                    aoClicar = {
                        aoEnviarEvento(
                            RegistroPedidosEventoUi.SelecionarFiltroPagamento(StatusPagamento.MEIO_A_MEIO)
                        )
                    }
                )
            }
            item {
                ChipFiltroPagamento(
                    rotulo = StatusPagamento.A_COBRAR.rotulo,
                    selecionado = estado.filtroPagamentoSelecionado == StatusPagamento.A_COBRAR,
                    aoClicar = {
                        aoEnviarEvento(
                            RegistroPedidosEventoUi.SelecionarFiltroPagamento(StatusPagamento.A_COBRAR)
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TelaRegistroPedidosPreview() {
    TemaAtelie {
        val pedidoExemplo = Pedido(
            id = 21,
            nomeCliente = "Mariana Costa",
            nomeProduto = "Cartão de Visita",
            especificacaoMaterial = "Couchê",
            especificacaoGramatura = "300g",
            especificacaoFormato = "9x5cm",
            especificacaoAcabamento = "Colorido",
            especificacaoFaces = "Frente e verso",
            valorTotalFinal = 180.0,
            etapaPipeline = EtapaPipeline.PRODUCAO,
            statusPagamento = StatusPagamento.PAGO_100
        )
        TelaRegistroPedidos(
            estado = RegistroPedidosEstadoUi(
                pedidosFiltrados = listOf(pedidoExemplo),
                somaTotalFiltrada = 180.0
            ),
            aoEnviarEvento = {},
            aoClicarPedido = {}
        )
    }
}
