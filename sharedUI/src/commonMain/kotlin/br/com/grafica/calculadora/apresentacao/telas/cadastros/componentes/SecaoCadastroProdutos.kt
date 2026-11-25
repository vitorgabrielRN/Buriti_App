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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import br.com.grafica.calculadora.dominio.modelo.ModoPrecificacao
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecaoCadastroProdutos(
    produtos: List<Produto>,
    aoSalvarProduto: (Produto) -> Unit,
    aoExcluirProduto: (Produto) -> Unit
) {
    var idEmEdicao by remember { mutableStateOf(0L) }
    var nome by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("Couchê") }
    var gramatura by remember { mutableStateOf("150g") }
    var formato by remember { mutableStateOf("A4") }
    var acabamento by remember { mutableStateOf("Padrão") }
    var faces by remember { mutableStateOf("Frente ou frente e verso") }
    var modoPrecificacao by remember { mutableStateOf(ModoPrecificacao.UNITARIO_OU_LOTE) }
    var precoPropria by remember { mutableStateOf("10.00") }
    var qtdBasePropria by remember { mutableStateOf("1") }
    var diasPropria by remember { mutableStateOf("2") }

    fun carregarParaEdicao(p: Produto) {
        idEmEdicao = p.id
        nome = p.nome
        material = p.materialOuPapel
        gramatura = p.gramatura
        formato = p.formatoOuTamanho
        acabamento = p.acabamentoPadrao
        faces = p.opcoesFaces
        modoPrecificacao = p.modoPrecificacao
        precoPropria = p.precoBasePropria.toString()
        qtdBasePropria = p.quantidadeBasePropria.toString()
        diasPropria = p.diasProducaoPropria.toString()
    }

    fun limpar() {
        idEmEdicao = 0L
        nome = ""
        material = "Couchê"
        gramatura = "150g"
        formato = "A4"
        acabamento = "Padrão"
        faces = "Frente ou frente e verso"
        modoPrecificacao = ModoPrecificacao.UNITARIO_OU_LOTE
        precoPropria = "10.00"
        qtdBasePropria = "1"
        diasPropria = "2"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CorBrancoSecundaria),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (idEmEdicao > 0L) "Editar Produto #$idEmEdicao" else "Cadastrar Produto Unificado",
                        style = MaterialTheme.typography.titleMedium,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome do Produto (ex: Marcador de livro, Cartão couchê)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = material,
                            onValueChange = { material = it },
                            label = { Text("Papel / Material") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = gramatura,
                            onValueChange = { gramatura = it },
                            label = { Text("Gramatura") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = formato,
                            onValueChange = { formato = it },
                            label = { Text("Formato (ex: A4, 9x5cm)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = acabamento,
                            onValueChange = { acabamento = it },
                            label = { Text("Acabamento") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ModoPrecificacao.entries.forEach { modo ->
                            FilterChip(
                                selected = modoPrecificacao == modo,
                                onClick = { modoPrecificacao = modo },
                                label = { Text(modo.rotulo) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CorDouradoTerciaria,
                                    selectedLabelColor = CorVinhoPrimaria
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = precoPropria,
                            onValueChange = { precoPropria = it.replace(',', '.') },
                            label = { Text("Preço Própria (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = qtdBasePropria,
                            onValueChange = { qtdBasePropria = it.filter { c -> c.isDigit() } },
                            label = { Text("Qtd Base") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = diasPropria,
                            onValueChange = { diasPropria = it.filter { c -> c.isDigit() } },
                            label = { Text("Prazo (d)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (idEmEdicao > 0L) {
                            OutlinedButton(
                                onClick = { limpar() },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Cancelar")
                            }
                        }
                        Button(
                            onClick = {
                                if (nome.isNotBlank()) {
                                    aoSalvarProduto(
                                        Produto(
                                            id = idEmEdicao,
                                            nome = nome.trim(),
                                            materialOuPapel = material.trim(),
                                            gramatura = gramatura.trim(),
                                            formatoOuTamanho = formato.trim(),
                                            acabamentoPadrao = acabamento.trim(),
                                            opcoesFaces = faces.trim(),
                                            modoPrecificacao = modoPrecificacao,
                                            aceitaProducaoPropria = true,
                                            precoBasePropria = precoPropria.toDoubleOrNull() ?: 0.0,
                                            quantidadeBasePropria = qtdBasePropria.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                                            quantidadeMinimaPropria = 1,
                                            diasProducaoPropria = diasPropria.toIntOrNull()?.coerceAtLeast(1) ?: 2,
                                            acabamentoPropria = acabamento.trim()
                                        )
                                    )
                                    limpar()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CorVinhoPrimaria)
                        ) {
                            Text(if (idEmEdicao > 0L) "Atualizar Produto" else "Adicionar ao Catálogo")
                        }
                    }
                }
            }
        }

        items(produtos, key = { it.id }) { prod ->
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
                            text = prod.nome,
                            style = MaterialTheme.typography.titleSmall,
                            color = CorVinhoPrimaria,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${prod.materialOuPapel} • ${prod.gramatura} • ${prod.formatoOuTamanho}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Própria: ${formatarMoedaBr(prod.precoBasePropria)} (Qtd base ${prod.quantidadeBasePropria}) • ${prod.modoPrecificacao.rotulo}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CorVinhoPrimaria
                        )
                    }
                    Row {
                        TextButton(onClick = { carregarParaEdicao(prod) }) {
                            Text("Editar", color = CorVinhoPrimaria, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { aoExcluirProduto(prod) }) {
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
private fun SecaoCadastroProdutosPreview() {
    TemaAtelie {
        SecaoCadastroProdutos(
            produtos = listOf(
                Produto(
                    id = 1,
                    nome = "Cartão de Visita",
                    materialOuPapel = "Couchê",
                    gramatura = "300g",
                    formatoOuTamanho = "9x5cm",
                    acabamentoPadrao = "Colorido",
                    opcoesFaces = "Frente e verso",
                    precoBasePropria = 65.0,
                    quantidadeBasePropria = 100
                )
            ),
            aoSalvarProduto = {},
            aoExcluirProduto = {}
        )
    }
}
