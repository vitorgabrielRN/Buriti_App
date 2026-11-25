package br.com.grafica.calculadora.apresentacao.telas.cadastros.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.cadastros.CadastrosEstadoUi
import br.com.grafica.calculadora.dominio.modelo.TaxaAdicional
import br.com.grafica.calculadora.dominio.modelo.TipoTaxa
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecaoTaxasEMeta(
    estado: CadastrosEstadoUi,
    aoSalvarTaxa: (TaxaAdicional) -> Unit,
    aoExcluirTaxa: (TaxaAdicional) -> Unit,
    aoAtualizarMeta: (String, Double, String) -> Unit
) {
    var nomeProprietaria by remember(estado.nomeProprietaria) { mutableStateOf(estado.nomeProprietaria) }
    var mesVigente by remember(estado.mesVigente) { mutableStateOf(estado.mesVigente) }
    var metaMensalTexto by remember(estado.metaMensal) { mutableStateOf(estado.metaMensal.toString()) }

    var idTaxaEmEdicao by remember { mutableStateOf(0L) }
    var tipoTaxa by remember { mutableStateOf(TipoTaxa.URGENCIA) }
    var nomeTaxa by remember { mutableStateOf("Taxa de Urgência") }
    var valorTaxaTexto by remember { mutableStateOf("20.0") }
    var emPercentual by remember { mutableStateOf(true) }
    var prazoLimiteTexto by remember { mutableStateOf("2") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CorBrancoSecundaria)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Meta Mensal & Identidade do Dashboard",
                        style = MaterialTheme.typography.titleMedium,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nomeProprietaria,
                            onValueChange = { nomeProprietaria = it },
                            label = { Text("Nome na Saudação") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = mesVigente,
                            onValueChange = { mesVigente = it },
                            label = { Text("Mês Vigente") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = metaMensalTexto,
                        onValueChange = { metaMensalTexto = it.replace(',', '.') },
                        label = { Text("Meta de Faturamento Mensal (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            aoAtualizarMeta(
                                nomeProprietaria,
                                metaMensalTexto.toDoubleOrNull() ?: 4000.0,
                                mesVigente
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CorVinhoPrimaria),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Salvar Meta Mensal")
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CorBrancoSecundaria)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (idTaxaEmEdicao > 0L) "Editar Taxa #$idTaxaEmEdicao"
                        else "Configurar Taxa Adicional (R$ ou %)",
                        style = MaterialTheme.typography.titleMedium,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TipoTaxa.entries.forEach { tipo ->
                            FilterChip(
                                selected = tipoTaxa == tipo,
                                onClick = {
                                    tipoTaxa = tipo
                                    nomeTaxa = tipo.rotulo
                                },
                                label = { Text(tipo.rotulo) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CorDouradoTerciaria,
                                    selectedLabelColor = CorVinhoPrimaria
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nomeTaxa,
                            onValueChange = { nomeTaxa = it },
                            label = { Text("Nome da Taxa") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = valorTaxaTexto,
                            onValueChange = { valorTaxaTexto = it.replace(',', '.') },
                            label = { Text(if (emPercentual) "Percentual (%)" else "Valor Fixo (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Cobrar em Percentual (%) sobre o subtotal?",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Switch(
                            checked = emPercentual,
                            onCheckedChange = { emPercentual = it }
                        )
                    }

                    OutlinedTextField(
                        value = prazoLimiteTexto,
                        onValueChange = { prazoLimiteTexto = it.filter { c -> c.isDigit() } },
                        label = { Text("Prazo-limite em dias (para Urgência / Emergência)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            aoSalvarTaxa(
                                TaxaAdicional(
                                    id = idTaxaEmEdicao,
                                    tipoTaxa = tipoTaxa,
                                    nome = nomeTaxa.trim(),
                                    valor = valorTaxaTexto.toDoubleOrNull() ?: 15.0,
                                    emPercentual = emPercentual,
                                    prazoLimiteDias = prazoLimiteTexto.toIntOrNull(),
                                    aplicarAposHora = if (tipoTaxa == TipoTaxa.ADICIONAL_NOTURNO) 18 else null
                                )
                            )
                            idTaxaEmEdicao = 0L
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CorVinhoPrimaria),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(if (idTaxaEmEdicao > 0L) "Atualizar Regra de Taxa" else "Salvar Regra de Taxa")
                    }
                }
            }
        }

        items(estado.taxasAdicionais, key = { it.id }) { taxa ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CorBrancoSecundaria)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = taxa.nome,
                            style = MaterialTheme.typography.titleSmall,
                            color = CorVinhoPrimaria,
                            fontWeight = FontWeight.Bold
                        )
                        val valorFormatado = if (taxa.emPercentual) {
                            "${taxa.valor}% sobre o subtotal"
                        } else {
                            formatarMoedaBr(taxa.valor)
                        }
                        Text(
                            text = "Cobrança: $valorFormatado${taxa.prazoLimiteDias?.let { " • Prazo <= ${it}d" }.orEmpty()}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row {
                        TextButton(
                            onClick = {
                                idTaxaEmEdicao = taxa.id
                                tipoTaxa = taxa.tipoTaxa
                                nomeTaxa = taxa.nome
                                valorTaxaTexto = taxa.valor.toString()
                                emPercentual = taxa.emPercentual
                                prazoLimiteTexto = taxa.prazoLimiteDias?.toString().orEmpty()
                            }
                        ) {
                            Text("Editar", color = CorVinhoPrimaria, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { aoExcluirTaxa(taxa) }) {
                            Text("Excluir", color = Color(0xFFB91C1C))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecaoTaxasEMetaPreview() {
    TemaAtelie {
        SecaoTaxasEMeta(
            estado = CadastrosEstadoUi(
                nomeProprietaria = "Ateliê",
                mesVigente = "Novembro",
                metaMensal = 4000.0,
                taxasAdicionais = listOf(
                    TaxaAdicional(id = 1, tipoTaxa = TipoTaxa.ENTREGA, nome = "Taxa de Entrega", valor = 15.0)
                )
            ),
            aoSalvarTaxa = {},
            aoExcluirTaxa = {},
            aoAtualizarMeta = { _, _, _ -> }
        )
    }
}
