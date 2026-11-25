package br.com.grafica.calculadora.apresentacao.telas.calculadora

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes.CartaoComprovanteOrcamento
import br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes.DialogoConfirmacaoOrcamento
import br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes.SecaoCatalogoProduto
import br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes.SecaoComparativoFornecedores
import br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes.SecaoDadosCliente
import br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes.SecaoMedidasEFaixas
import br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes.SecaoPrazosETaxas
import br.com.grafica.calculadora.dominio.modelo.ModalidadeProducao
import br.com.grafica.calculadora.dominio.modelo.ModoPrecificacao
import br.com.grafica.calculadora.dominio.modelo.Orcamento
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

@Composable
fun TelaCalculadoraOrcamento(
    estado: CalculadoraEstadoUi,
    aoEnviarEvento: (CalculadoraEventoUi) -> Unit,
    preenchimentoConteudo: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(preenchimentoConteudo)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (estado.pedidoIdEmEdicao != null)
                            "Editando Pedido #${estado.pedidoIdEmEdicao.toString().padStart(4, '0')}"
                        else
                            "Novo Orçamento",
                        style = MaterialTheme.typography.displaySmall,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cliente → Produto → Quantidade → Prazo → Confirmar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(onClick = { aoEnviarEvento(CalculadoraEventoUi.LimparFormulario) }) {
                    Text(if (estado.pedidoIdEmEdicao != null) "Novo" else "Limpar")
                }
            }
        }

        if (estado.mensagemSucesso != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFDFF6E8),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ ${estado.mensagemSucesso}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF116337),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        if (estado.mensagemErro != null) {
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ ${estado.mensagemErro}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        item { SecaoDadosCliente(estado = estado, aoEnviarEvento = aoEnviarEvento) }
        item { SecaoCatalogoProduto(estado = estado, aoEnviarEvento = aoEnviarEvento) }
        item { SecaoMedidasEFaixas(estado = estado, aoEnviarEvento = aoEnviarEvento) }
        item { SecaoPrazosETaxas(estado = estado, aoEnviarEvento = aoEnviarEvento) }
        item { SecaoComparativoFornecedores(estado = estado, aoEnviarEvento = aoEnviarEvento) }

        item {
            estado.orcamentoCalculado?.let { orcamento ->
                CartaoComprovanteOrcamento(
                    orcamento = orcamento,
                    metodoEntrega = estado.metodoEntrega,
                    editandoPedidoExistente = estado.pedidoIdEmEdicao != null,
                    aoSalvar = { aoEnviarEvento(CalculadoraEventoUi.SolicitarConfirmacao) }
                )
            }
        }
    }

    if (estado.exibirConfirmacao) {
        DialogoConfirmacaoOrcamento(
            estado = estado,
            aoConfirmar = { aoEnviarEvento(CalculadoraEventoUi.ConfirmarOrcamento) },
            aoVoltarParaEdicao = { aoEnviarEvento(CalculadoraEventoUi.VoltarParaEdicao) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TelaCalculadoraOrcamentoPreview() {
    TemaAtelie {
        val produto = Produto(
            id = 1,
            nome = "Cartão de Visita",
            materialOuPapel = "Couchê",
            gramatura = "300g",
            formatoOuTamanho = "9x5cm",
            acabamentoPadrao = "Colorido",
            opcoesFaces = "Frente e verso",
            precoBasePropria = 0.50
        )
        TelaCalculadoraOrcamento(
            estado = CalculadoraEstadoUi(
                nomeCliente = "Mariana Costa",
                consultaProduto = "Cartão de Visita",
                produtoSelecionado = produto,
                produtos = listOf(produto),
                quantidadeTexto = "100",
                dataEntregaPrevista = "20/11",
                orcamentoCalculado = Orcamento(
                    nomeProduto = produto.nome,
                    materialEspecificacao = produto.materialOuPapel,
                    gramaturaEspecificacao = produto.gramatura,
                    formatoEspecificacao = produto.formatoOuTamanho,
                    acabamentoEspecificacao = "Colorido",
                    facesEspecificacao = "Frente e verso",
                    modalidadeProducao = ModalidadeProducao.PROPRIA,
                    nomeFornecedor = null,
                    observacaoDivergenciaMaterial = "",
                    modoPrecificacao = ModoPrecificacao.UNITARIO_OU_LOTE,
                    quantidadeSolicitada = 100,
                    quantidadeEfetiva = 100,
                    quantidadeMinimaExigida = 1,
                    atendeQuantidadeMinima = true,
                    larguraMetros = 1.0,
                    alturaMetros = 1.0,
                    areaMetrosQuadrados = 0.0,
                    precoUnitarioAplicado = 0.50,
                    descricaoFaixaAplicada = "Unitário",
                    subtotalItens = 50.0,
                    diasProducaoNormal = 2,
                    diasProducaoEfetivo = 2,
                    dataEntregaPrevista = "20/11",
                    rotuloTaxaUrgencia = null,
                    valorTaxaUrgencia = 0.0,
                    valorTaxaEntrega = 0.0,
                    valorAdicionalNoturno = 0.0,
                    totalTaxasAdicionais = 0.0,
                    valorTotalFinal = 50.0
                )
            ),
            aoEnviarEvento = {}
        )
    }
}
