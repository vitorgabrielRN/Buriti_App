package br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEstadoUi
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEventoUi
import br.com.grafica.calculadora.dominio.modelo.MetodoEntrega
import br.com.grafica.calculadora.dominio.modelo.PrioridadePrazo
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.nucleo.componentes.CartaoSecaoFormulario
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecaoPrazosETaxas(
    estado: CalculadoraEstadoUi,
    aoEnviarEvento: (CalculadoraEventoUi) -> Unit
) {
    if (estado.produtoSelecionado == null) return
    val orcamento = estado.orcamentoCalculado

    CartaoSecaoFormulario(
        titulo = "4. Prazo, Entrega e Pagamento",
        subtitulo = "A data de entrega acompanha automaticamente o prazo de produção"
    ) {
        Text("Prioridade", style = MaterialTheme.typography.labelLarge, color = CorVinhoPrimaria)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PrioridadePrazo.entries.forEach { modo ->
                FilterChip(
                    selected = estado.prioridadePrazo == modo,
                    onClick = { aoEnviarEvento(CalculadoraEventoUi.AlterarPrioridadePrazo(modo)) },
                    label = { Text(modo.rotulo) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CorVinhoPrimaria,
                        selectedLabelColor = CorBrancoSecundaria
                    )
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = estado.prazoProducaoSolicitadoTexto,
                onValueChange = { aoEnviarEvento(CalculadoraEventoUi.AlterarPrazoProducaoSolicitado(it)) },
                label = { Text("Produção (dias)") },
                placeholder = { Text("${orcamento?.diasProducaoEfetivo ?: 0}") },
                suffix = { Text("dias") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = estado.dataEntregaPrevista,
                onValueChange = { aoEnviarEvento(CalculadoraEventoUi.AlterarDataEntrega(it)) },
                label = { Text("Entrega (dd/MM)") },
                supportingText = {
                    Text(if (estado.dataEntregaEditadaManualmente) "Editada manualmente" else "Automática")
                },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        Text("Modalidade de entrega", style = MaterialTheme.typography.labelLarge, color = CorVinhoPrimaria)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetodoEntrega.entries.forEach { metodo ->
                FilterChip(
                    selected = estado.metodoEntrega == metodo,
                    onClick = { aoEnviarEvento(CalculadoraEventoUi.AlterarMetodoEntrega(metodo)) },
                    label = { Text(metodo.rotulo) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CorVinhoPrimaria,
                        selectedLabelColor = CorBrancoSecundaria
                    )
                )
            }
        }

        Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceContainer, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("🌙 Adicional noturno (após 18h)", style = MaterialTheme.typography.titleSmall, color = CorVinhoPrimaria, fontWeight = FontWeight.Bold)
                    Text("Taxa configurada no Painel", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = estado.aplicarAdicionalNoturno,
                    onCheckedChange = { aoEnviarEvento(CalculadoraEventoUi.AlternarAdicionalNoturno(it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = CorDouradoTerciaria, checkedTrackColor = CorVinhoPrimaria)
                )
            }
        }

        Text("Pagamento", style = MaterialTheme.typography.labelLarge, color = CorVinhoPrimaria)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusPagamento.entries.forEach { pgto ->
                FilterChip(
                    selected = estado.statusPagamento == pgto,
                    onClick = { aoEnviarEvento(CalculadoraEventoUi.AlterarStatusPagamento(pgto)) },
                    label = { Text(pgto.rotulo) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CorDouradoTerciaria,
                        selectedLabelColor = CorVinhoPrimaria
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecaoPrazosETaxasPreview() {
    TemaAtelie {
        val produto = Produto(
            id = 1,
            nome = "Cartão de Visita",
            materialOuPapel = "Couchê",
            gramatura = "300g",
            formatoOuTamanho = "9x5cm",
            acabamentoPadrao = "Colorido",
            opcoesFaces = "Frente e verso"
        )
        SecaoPrazosETaxas(
            estado = CalculadoraEstadoUi(
                produtoSelecionado = produto,
                prazoProducaoSolicitadoTexto = "3",
                dataEntregaPrevista = "20/11"
            ),
            aoEnviarEvento = {}
        )
    }
}
