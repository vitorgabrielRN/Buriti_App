package br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.dominio.modelo.MetodoEntrega
import br.com.grafica.calculadora.dominio.modelo.ModalidadeProducao
import br.com.grafica.calculadora.dominio.modelo.ModoPrecificacao
import br.com.grafica.calculadora.dominio.modelo.Orcamento
import br.com.grafica.calculadora.nucleo.componentes.CartaoGlass
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorEsmeraldaClaro
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@Composable
fun CartaoComprovanteOrcamento(
    orcamento: Orcamento,
    metodoEntrega: MetodoEntrega,
    editandoPedidoExistente: Boolean,
    aoSalvar: () -> Unit
) {
    CartaoGlass(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resumo & Comprovante do Orçamento",
                    style = MaterialTheme.typography.titleLarge,
                    color = CorDouradoTerciaria,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = orcamento.modalidadeProducao.rotulo,
                    style = MaterialTheme.typography.labelMedium,
                    color = CorBrancoSecundaria
                )
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

            LinhaResumoOrcamento("Produto:", orcamento.nomeProduto)
            LinhaResumoOrcamento(
                "Material / Gramatura:",
                "${orcamento.materialEspecificacao} • ${orcamento.gramaturaEspecificacao}"
            )
            LinhaResumoOrcamento(
                "Formato / Acabamento:",
                "${orcamento.formatoEspecificacao} • ${orcamento.acabamentoEspecificacao} (${orcamento.facesEspecificacao})"
            )
            LinhaResumoOrcamento(
                "Quantidade & Faixa:",
                "${orcamento.quantidadeEfetiva} un (${orcamento.descricaoFaixaAplicada} @ ${formatarMoedaBr(orcamento.precoUnitarioAplicado)})"
            )
            LinhaResumoOrcamento("Modalidade:", orcamento.modalidadeProducao.rotulo)

            val fornecedorEscolhido = orcamento.nomeFornecedor
            if (fornecedorEscolhido != null) {
                LinhaResumoOrcamento("Fornecedor Escolhido:", fornecedorEscolhido)
            }
            if (orcamento.observacaoDivergenciaMaterial.isNotBlank()) {
                LinhaResumoOrcamento(
                    "⚠️ Obs. Material:",
                    orcamento.observacaoDivergenciaMaterial,
                    corDestaque = CorDouradoTerciaria
                )
            }

            LinhaResumoOrcamento(
                "Prazo de Produção / Entrega:",
                "Produção: ${orcamento.diasProducaoEfetivo} dia(s) • Entrega: ${orcamento.dataEntregaPrevista} (${metodoEntrega.rotulo})"
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

            LinhaResumoOrcamento("Subtotal dos Itens:", formatarMoedaBr(orcamento.subtotalItens))

            if (orcamento.valorTaxaUrgencia > 0.0) {
                LinhaResumoOrcamento(
                    "${orcamento.rotuloTaxaUrgencia ?: "Taxa de Urgência/Emergência"}:",
                    "+ ${formatarMoedaBr(orcamento.valorTaxaUrgencia)}",
                    corDestaque = CorDouradoTerciaria
                )
            }
            if (orcamento.valorTaxaEntrega > 0.0) {
                LinhaResumoOrcamento(
                    "Taxa de Entrega:",
                    "+ ${formatarMoedaBr(orcamento.valorTaxaEntrega)}",
                    corDestaque = CorDouradoTerciaria
                )
            }
            if (orcamento.valorAdicionalNoturno > 0.0) {
                LinhaResumoOrcamento(
                    "Adicional Noturno (>= 18h):",
                    "+ ${formatarMoedaBr(orcamento.valorAdicionalNoturno)}",
                    corDestaque = CorDouradoTerciaria
                )
            }

            LinhaResumoOrcamento(
                "Soma das Taxas Adicionais:",
                formatarMoedaBr(orcamento.totalTaxasAdicionais),
                corDestaque = CorEsmeraldaClaro
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VALOR TOTAL FINAL",
                    style = MaterialTheme.typography.titleMedium,
                    color = CorBrancoSecundaria,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatarMoedaBr(orcamento.valorTotalFinal),
                    style = MaterialTheme.typography.displaySmall,
                    color = CorDouradoTerciaria,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = aoSalvar,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CorDouradoTerciaria,
                    contentColor = CorVinhoPrimaria
                ),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = if (editandoPedidoExistente)
                        "Revisar alterações do pedido"
                    else
                        "Revisar e confirmar orçamento",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun LinhaResumoOrcamento(
    rotulo: String,
    valor: String,
    corDestaque: Color = CorBrancoSecundaria
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.bodySmall,
            color = CorBrancoSecundaria.copy(alpha = 0.78f),
            modifier = Modifier.weight(0.45f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            color = corDestaque,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.55f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CartaoComprovanteOrcamentoPreview() {
    TemaAtelie {
        CartaoComprovanteOrcamento(
            orcamento = Orcamento(
                nomeProduto = "Cartão de Visita",
                materialEspecificacao = "Couchê",
                gramaturaEspecificacao = "300g",
                formatoEspecificacao = "9x5cm",
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
                valorTaxaEntrega = 15.0,
                valorAdicionalNoturno = 0.0,
                totalTaxasAdicionais = 15.0,
                valorTotalFinal = 65.0
            ),
            metodoEntrega = MetodoEntrega.ENTREGA,
            editandoPedidoExistente = false,
            aoSalvar = {}
        )
    }
}
