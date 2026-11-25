package br.com.grafica.calculadora.apresentacao.telas.inicio

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.inicio.componentes.CartaoAtalhosProducao
import br.com.grafica.calculadora.apresentacao.telas.inicio.componentes.GavetaDemandas
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.nucleo.componentes.CartaoFaturamentoEstudio
import br.com.grafica.calculadora.nucleo.componentes.CartaoMetaMensal
import br.com.grafica.calculadora.nucleo.componentes.TelaComDraggableSheet
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoContainer
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria

@Composable
fun TelaInicio(
    estado: InicioEstadoUi,
    aoEnviarEvento: (InicioEventoUi) -> Unit,
    aoIrParaCalculadora: () -> Unit,
    aoIrParaQuadro: () -> Unit,
    aoIrParaPedidos: () -> Unit,
    aoIrParaCadastros: () -> Unit,
    aoSelecionarPedido: (Pedido) -> Unit,
    preenchimentoConteudo: PaddingValues = PaddingValues(0.dp)
) {
    GavetaDemandas(
        demandas = estado.demandasPrioritarias,
        aoVerQuadro = aoIrParaQuadro,
        aoClicarDemanda = aoSelecionarPedido,
        aoAvancarEtapa = { pedido -> aoEnviarEvento(InicioEventoUi.AvancarEtapaPedido(pedido)) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(preenchimentoConteudo)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                CabecalhoInicio(
                    mesVigente = estado.mesVigente,
                    saudacao = estado.saudacao,
                    aoAlternarModoEstudio = { aoEnviarEvento(InicioEventoUi.AlternarModoEstudio) },
                    aoSair = { aoEnviarEvento(InicioEventoUi.SairSessao) }
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CartaoFaturamentoEstudio(
                        faturamentoMensal = estado.faturamentoMensal,
                        pedidosConcluidos = estado.pedidosConcluidos,
                        valorAReceber = estado.valorAReceber
                    )

                    CartaoMetaMensal(
                        faturamentoAcumulado = estado.faturamentoMensal,
                        metaMensal = estado.metaMensal
                    )
                }
            }

            item {
                CartaoAtalhosProducao(
                    aoIrParaCalculadora = aoIrParaCalculadora,
                    aoIrParaQuadro = aoIrParaQuadro,
                    aoIrParaPedidos = aoIrParaPedidos,
                    aoIrParaCadastros = aoIrParaCadastros
                )
            }

            //Gaveta demanda

            item {
            }
       

        }
    }
}

@Composable
private fun CabecalhoInicio(
    mesVigente: String,
    saudacao: String,
    aoAlternarModoEstudio: () -> Unit,
    aoSair: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(CorVinhoContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = mesVigente.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(CorVinhoPrimaria)
                        .clickable { aoAlternarModoEstudio() }
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
                text = saudacao,
                style = MaterialTheme.typography.displaySmall,
                color = CorVinhoPrimaria,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CorBrancoSecundaria,
            shadowElevation = 2.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { aoSair() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Sair",
                    style = MaterialTheme.typography.labelMedium,
                    color = CorVinhoPrimaria,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
