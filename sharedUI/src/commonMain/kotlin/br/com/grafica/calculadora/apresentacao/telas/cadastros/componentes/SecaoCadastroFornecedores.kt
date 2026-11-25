package br.com.grafica.calculadora.apresentacao.telas.cadastros.componentes

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import br.com.grafica.calculadora.dominio.modelo.Fornecedor
import br.com.grafica.calculadora.dominio.modelo.OfertaFornecedor
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@Composable
fun SecaoCadastroFornecedores(
    produtos: List<Produto>,
    fornecedores: List<Fornecedor>,
    ofertas: List<OfertaFornecedor>,
    aoSalvarFornecedor: (Fornecedor) -> Unit,
    aoExcluirFornecedor: (Fornecedor) -> Unit,
    aoSalvarOferta: (OfertaFornecedor) -> Unit,
    aoExcluirOferta: (OfertaFornecedor) -> Unit
) {
    var nomeFornecedor by remember { mutableStateOf("") }
    var contatoFornecedor by remember { mutableStateOf("") }

    var produtoSelecionado by remember(produtos) { mutableStateOf(produtos.firstOrNull()) }
    var fornecedorSelecionado by remember(fornecedores) { mutableStateOf(fornecedores.firstOrNull()) }
    var idOfertaEmEdicao by remember { mutableStateOf(0L) }
    var precoOferta by remember { mutableStateOf("10.00") }
    var qtdBaseOferta by remember { mutableStateOf("1") }
    var qtdMinOferta by remember { mutableStateOf("1") }
    var diasOferta by remember { mutableStateOf("2") }
    var acabamentoOferta by remember { mutableStateOf("Padrão") }
    var divergenciaOferta by remember { mutableStateOf("") }

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
                        text = "Gráficas Parceiras (Terceirização)",
                        style = MaterialTheme.typography.titleMedium,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nomeFornecedor,
                            onValueChange = { nomeFornecedor = it },
                            label = { Text("Nome da Gráfica (ex: GM PRINT)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = contatoFornecedor,
                            onValueChange = { contatoFornecedor = it },
                            label = { Text("Contato / Obs") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Button(
                        onClick = {
                            if (nomeFornecedor.isNotBlank()) {
                                aoSalvarFornecedor(
                                    Fornecedor(
                                        nome = nomeFornecedor.trim().uppercase(),
                                        contatoOuObservacoes = contatoFornecedor.trim()
                                    )
                                )
                                nomeFornecedor = ""
                                contatoFornecedor = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CorVinhoPrimaria),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Adicionar Fornecedor")
                    }

                    HorizontalDivider()

                    fornecedores.forEach { forn ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(forn.nome, fontWeight = FontWeight.Bold, color = CorVinhoPrimaria)
                                if (forn.contatoOuObservacoes.isNotBlank()) {
                                    Text(forn.contatoOuObservacoes, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            TextButton(onClick = { aoExcluirFornecedor(forn) }) {
                                Text("Remover", color = Color(0xFFB91C1C))
                            }
                        }
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
                        text = if (idOfertaEmEdicao > 0L) "Editar Vínculo #$idOfertaEmEdicao"
                        else "Vincular Preço de Fornecedor ao Produto",
                        style = MaterialTheme.typography.titleMedium,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )

                    Text("1. Selecione o Fornecedor:", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(fornecedores, key = { it.id }) { forn ->
                            FilterChip(
                                selected = fornecedorSelecionado?.id == forn.id,
                                onClick = { fornecedorSelecionado = forn },
                                label = { Text(forn.nome) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CorDouradoTerciaria,
                                    selectedLabelColor = CorVinhoPrimaria
                                )
                            )
                        }
                    }

                    Text("2. Selecione o Produto do Catálogo:", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(produtos, key = { it.id }) { prod ->
                            FilterChip(
                                selected = produtoSelecionado?.id == prod.id,
                                onClick = { produtoSelecionado = prod },
                                label = { Text(prod.nome) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CorVinhoPrimaria,
                                    selectedLabelColor = CorBrancoSecundaria
                                )
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = precoOferta,
                            onValueChange = { precoOferta = it.replace(',', '.') },
                            label = { Text("Valor (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = qtdBaseOferta,
                            onValueChange = { qtdBaseOferta = it.filter { c -> c.isDigit() } },
                            label = { Text("Qtd Base") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = qtdMinOferta,
                            onValueChange = { qtdMinOferta = it.filter { c -> c.isDigit() } },
                            label = { Text("Qtd Mín") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = acabamentoOferta,
                            onValueChange = { acabamentoOferta = it },
                            label = { Text("Acabamento no Fornecedor") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = diasOferta,
                            onValueChange = { diasOferta = it.filter { c -> c.isDigit() } },
                            label = { Text("Prazo (d)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.6f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = divergenciaOferta,
                        onValueChange = { divergenciaOferta = it },
                        label = { Text("Divergência de Material / Observação (ex: Valor 1 folha / 7x5cm)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val prod = produtoSelecionado
                            val forn = fornecedorSelecionado
                            if (prod != null && forn != null) {
                                aoSalvarOferta(
                                    OfertaFornecedor(
                                        id = idOfertaEmEdicao,
                                        produtoId = prod.id,
                                        fornecedorId = forn.id,
                                        nomeFornecedor = forn.nome,
                                        precoBase = precoOferta.toDoubleOrNull() ?: 0.0,
                                        quantidadeBase = qtdBaseOferta.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                                        quantidadeMinima = qtdMinOferta.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                                        diasProducao = diasOferta.toIntOrNull()?.coerceAtLeast(1) ?: 2,
                                        acabamento = acabamentoOferta.trim(),
                                        observacaoDivergenciaMaterial = divergenciaOferta.trim()
                                    )
                                )
                                idOfertaEmEdicao = 0L
                                divergenciaOferta = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CorVinhoPrimaria),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(if (idOfertaEmEdicao > 0L) "Atualizar Oferta" else "Salvar Vínculo de Fornecedor")
                    }
                }
            }
        }

        items(ofertas, key = { it.id }) { oferta ->
            val nomeProd = produtos.find { it.id == oferta.produtoId }?.nome ?: "Produto #${oferta.produtoId}"
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
                            text = "${oferta.nomeFornecedor} -> $nomeProd",
                            style = MaterialTheme.typography.titleSmall,
                            color = CorVinhoPrimaria,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Valor: ${formatarMoedaBr(oferta.precoBase)} (Qtd base ${oferta.quantidadeBase} | Mín ${oferta.quantidadeMinima}) • Prazo: ${oferta.diasProducao}d",
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (oferta.observacaoDivergenciaMaterial.isNotBlank()) {
                            Text(
                                text = "Obs: ${oferta.observacaoDivergenciaMaterial}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF855700)
                            )
                        }
                    }
                    Row {
                        TextButton(
                            onClick = {
                                idOfertaEmEdicao = oferta.id
                                produtoSelecionado = produtos.find { it.id == oferta.produtoId }
                                fornecedorSelecionado = fornecedores.find { it.id == oferta.fornecedorId }
                                precoOferta = oferta.precoBase.toString()
                                qtdBaseOferta = oferta.quantidadeBase.toString()
                                qtdMinOferta = oferta.quantidadeMinima.toString()
                                diasOferta = oferta.diasProducao.toString()
                                acabamentoOferta = oferta.acabamento
                                divergenciaOferta = oferta.observacaoDivergenciaMaterial
                            }
                        ) {
                            Text("Editar", color = CorVinhoPrimaria, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { aoExcluirOferta(oferta) }) {
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
private fun SecaoCadastroFornecedoresPreview() {
    TemaAtelie {
        SecaoCadastroFornecedores(
            produtos = emptyList(),
            fornecedores = listOf(
                Fornecedor(id = 1, nome = "Gráfica Parceira", contatoOuObservacoes = "WhatsApp (11) 99999-0000", diasProducaoPadrao = 2)
            ),
            ofertas = emptyList(),
            aoSalvarFornecedor = {},
            aoExcluirFornecedor = {},
            aoSalvarOferta = {},
            aoExcluirOferta = {}
        )
    }
}
