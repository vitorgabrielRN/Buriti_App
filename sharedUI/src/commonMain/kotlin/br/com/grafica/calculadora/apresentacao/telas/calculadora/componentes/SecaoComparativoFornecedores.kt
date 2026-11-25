package br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEstadoUi
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEventoUi
import br.com.grafica.calculadora.dominio.modelo.ModalidadeProducao
import br.com.grafica.calculadora.dominio.modelo.OpcaoComparativoFornecedor
import br.com.grafica.calculadora.nucleo.componentes.CartaoSecaoFormulario
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorContornoSutil
import br.com.grafica.calculadora.nucleo.tema.CorDouradoContainer
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoContainer
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@Composable
fun SecaoComparativoFornecedores(
    estado: CalculadoraEstadoUi,
    aoEnviarEvento: (CalculadoraEventoUi) -> Unit
) {
    // Só faz sentido comparar quando existe mais de uma opção de produção
    if (estado.opcoesComparativo.size < 2) return

    CartaoSecaoFormulario(
        titulo = "5. Onde produzir? (Comparativo)",
        subtitulo = "Opcional: compare produção própria com gráficas parceiras"
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(estado.opcoesComparativo, key = { it.chaveOpcao }) { opcao ->
                val selecionada = estado.chaveOpcaoSelecionada == opcao.chaveOpcao
                CartaoComparativoOpcao(
                    opcao = opcao,
                    selecionada = selecionada,
                    aoSelecionar = {
                        aoEnviarEvento(CalculadoraEventoUi.SelecionarOpcaoComparativo(opcao))
                    }
                )
            }
        }
    }
}

@Composable
private fun CartaoComparativoOpcao(
    opcao: OpcaoComparativoFornecedor,
    selecionada: Boolean,
    aoSelecionar: () -> Unit
) {
    val corBorda = if (selecionada) CorVinhoPrimaria else CorContornoSutil
    val corFundo = if (selecionada) CorVinhoContainer else CorBrancoSecundaria

    Surface(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (selecionada) 2.dp else 1.dp,
                color = corBorda,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { aoSelecionar() },
        shape = RoundedCornerShape(18.dp),
        color = corFundo
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (opcao.modalidade == ModalidadeProducao.PROPRIA) CorVinhoPrimaria
                            else CorDouradoTerciaria
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (opcao.modalidade == ModalidadeProducao.PROPRIA) "Própria" else "Terceirizada",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (opcao.modalidade == ModalidadeProducao.PROPRIA) CorBrancoSecundaria else CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Prazo: ${opcao.diasProducao}d",
                    style = MaterialTheme.typography.labelSmall,
                    color = CorVinhoPrimaria,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = opcao.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = CorVinhoPrimaria,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Subtotal: ${formatarMoedaBr(opcao.subtotalEstimado)}",
                style = MaterialTheme.typography.titleLarge,
                color = CorVinhoPrimaria,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Base: ${formatarMoedaBr(opcao.precoAplicadoPorUnidadeOuLote)} (${opcao.descricaoFaixaAplicada})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Qtd mín.: ${opcao.quantidadeMinima} un • Acab.: ${opcao.acabamento}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (opcao.observacaoDivergenciaMaterial.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CorDouradoContainer)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "ℹ️ ${opcao.observacaoDivergenciaMaterial}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecaoComparativoFornecedoresPreview() {
    TemaAtelie {
        val opcaoPropria = OpcaoComparativoFornecedor(
            chaveOpcao = "PROPRIA",
            modalidade = ModalidadeProducao.PROPRIA,
            fornecedorId = null,
            ofertaFornecedorId = null,
            titulo = "Produção Própria (Ateliê)",
            precoBase = 0.60,
            quantidadeBase = 1,
            quantidadeMinima = 1,
            diasProducao = 2,
            acabamento = "Colorido",
            observacaoDivergenciaMaterial = "",
            precoAplicadoPorUnidadeOuLote = 0.60,
            descricaoFaixaAplicada = "Unitário",
            subtotalEstimado = 60.0,
            atendeQuantidadeMinima = true
        )
        val opcaoParceiro = opcaoPropria.copy(
            chaveOpcao = "FORNECEDOR_1",
            modalidade = ModalidadeProducao.TERCEIRIZADA,
            fornecedorId = 1,
            ofertaFornecedorId = 1,
            titulo = "Gráfica Parceira",
            diasProducao = 3,
            subtotalEstimado = 52.0
        )
        SecaoComparativoFornecedores(
            estado = CalculadoraEstadoUi(
                opcoesComparativo = listOf(opcaoPropria, opcaoParceiro),
                chaveOpcaoSelecionada = "PROPRIA"
            ),
            aoEnviarEvento = {}
        )
    }
}
