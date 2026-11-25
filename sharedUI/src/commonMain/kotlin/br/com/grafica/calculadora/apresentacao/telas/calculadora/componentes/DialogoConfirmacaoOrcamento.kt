package br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEstadoUi
import br.com.grafica.calculadora.dominio.modelo.ModalidadeProducao
import br.com.grafica.calculadora.dominio.modelo.ModoPrecificacao
import br.com.grafica.calculadora.dominio.modelo.Orcamento
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

/**
 * "Deseja confirmar este orçamento?"
 * Confirmar -> salva no banco (pipeline inicia em ORCAMENTO).
 * Voltar e editar -> fecha o diálogo mantendo todos os campos preenchidos.
 */
@Composable
fun DialogoConfirmacaoOrcamento(
    estado: CalculadoraEstadoUi,
    aoConfirmar: () -> Unit,
    aoVoltarParaEdicao: () -> Unit
) {
    val orcamento = estado.orcamentoCalculado ?: return

    AlertDialog(
        onDismissRequest = { if (!estado.salvando) aoVoltarParaEdicao() },
        title = {
            Text(
                text = "Deseja confirmar este orçamento?",
                color = CorVinhoPrimaria,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LinhaConfirmacao("Cliente", estado.nomeCliente)
                LinhaConfirmacao("Produto", orcamento.nomeProduto)
                LinhaConfirmacao(
                    "Quantidade",
                    "${orcamento.quantidadeEfetiva} un × ${formatarMoedaBr(orcamento.precoUnitarioAplicado)}"
                )
                LinhaConfirmacao("Impressão", "${estado.rotuloAcabamento} • ${estado.rotuloFaces}")
                LinhaConfirmacao(
                    "Prazo",
                    "${orcamento.diasProducaoEfetivo} dia(s) • ${estado.metodoEntrega.rotulo} em ${orcamento.dataEntregaPrevista}"
                )
                LinhaConfirmacao("Pagamento", estado.statusPagamento.rotulo)
                if (orcamento.totalTaxasAdicionais > 0.0) {
                    LinhaConfirmacao("Taxas", "+ ${formatarMoedaBr(orcamento.totalTaxasAdicionais)}")
                }
                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CorVinhoPrimaria)
                    Text(
                        formatarMoedaBr(orcamento.valorTotalFinal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = CorVinhoPrimaria
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = aoConfirmar,
                enabled = !estado.salvando,
                colors = ButtonDefaults.buttonColors(containerColor = CorVinhoPrimaria, contentColor = CorBrancoSecundaria)
            ) {
                if (estado.salvando) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = CorDouradoTerciaria, strokeWidth = 2.dp)
                } else {
                    Text("Sim, confirmar", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = aoVoltarParaEdicao, enabled = !estado.salvando) {
                Text("Não, voltar e editar", color = CorVinhoPrimaria)
            }
        }
    )
}

@Composable
private fun LinhaConfirmacao(rotulo: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(rotulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.35f))
        Text(valor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.65f))
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogoConfirmacaoOrcamentoPreview() {
    TemaAtelie {
        DialogoConfirmacaoOrcamento(
            estado = CalculadoraEstadoUi(
                nomeCliente = "Mariana Costa",
                dataEntregaPrevista = "20/11",
                orcamentoCalculado = Orcamento(
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
                    valorTaxaEntrega = 0.0,
                    valorAdicionalNoturno = 0.0,
                    totalTaxasAdicionais = 0.0,
                    valorTotalFinal = 50.0
                )
            ),
            aoConfirmar = {},
            aoVoltarParaEdicao = {}
        )
    }
}
