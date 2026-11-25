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
import br.com.grafica.calculadora.dominio.modelo.FaixaPreco
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@Composable
fun SecaoCadastroFaixasPreco(
    produtos: List<Produto>,
    faixas: List<FaixaPreco>,
    aoSalvarFaixa: (FaixaPreco) -> Unit,
    aoExcluirFaixa: (FaixaPreco) -> Unit
) {
    var produtoSelecionado by remember(produtos) { mutableStateOf(produtos.firstOrNull()) }
    var qtdMinTexto by remember { mutableStateOf("1") }
    var qtdMaxTexto by remember { mutableStateOf("50") }
    var precoFaixaTexto by remember { mutableStateOf("1.20") }

    val faixasFiltradas = faixas.filter { it.produtoId == produtoSelecionado?.id }

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
                        text = "Faixas de Quantidade (Tiered Pricing)",
                        style = MaterialTheme.typography.titleMedium,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ex: Flyer A5 (1 a 50, 51 a 100, acima de 100) ou Adesivo A4 (1 a 5, 6 a 10, acima de 10)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                            value = qtdMinTexto,
                            onValueChange = { qtdMinTexto = it.filter { c -> c.isDigit() } },
                            label = { Text("Qtd Inicial") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = qtdMaxTexto,
                            onValueChange = { qtdMaxTexto = it.filter { c -> c.isDigit() } },
                            label = { Text("Qtd Final (vazio = acima)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = precoFaixaTexto,
                            onValueChange = { precoFaixaTexto = it.replace(',', '.') },
                            label = { Text("Valor Unit. (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            val prod = produtoSelecionado
                            if (prod != null) {
                                aoSalvarFaixa(
                                    FaixaPreco(
                                        produtoId = prod.id,
                                        quantidadeMinima = qtdMinTexto.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                                        quantidadeMaxima = qtdMaxTexto.toIntOrNull(),
                                        precoUnitarioOuFaixa = precoFaixaTexto.toDoubleOrNull() ?: 1.0
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CorVinhoPrimaria),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Adicionar Faixa")
                    }
                }
            }
        }

        items(faixasFiltradas, key = { it.id }) { faixa ->
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
                    val maxQtd = faixa.quantidadeMaxima
                    val textoFaixa = if (maxQtd != null) {
                        "De ${faixa.quantidadeMinima} até $maxQtd unidades"
                    } else {
                        "Acima de ${faixa.quantidadeMinima - 1} unidades"
                    }
                    Column {
                        Text(textoFaixa, fontWeight = FontWeight.Bold, color = CorVinhoPrimaria)
                        Text("Preço na faixa: ${formatarMoedaBr(faixa.precoUnitarioOuFaixa)} / un")
                    }
                    TextButton(onClick = { aoExcluirFaixa(faixa) }) {
                        Text("Remover", color = Color(0xFFB91C1C))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecaoCadastroFaixasPrecoPreview() {
    TemaAtelie {
        val produto = Produto(
            id = 1,
            nome = "Adesivo Vinil",
            materialOuPapel = "Vinil Brilho",
            gramatura = "120g",
            formatoOuTamanho = "5x5cm",
            acabamentoPadrao = "Meio corte",
            opcoesFaces = "Somente frente",
            precoBasePropria = 1.50
        )
        SecaoCadastroFaixasPreco(
            produtos = listOf(produto),
            faixas = listOf(
                FaixaPreco(id = 1, produtoId = 1, quantidadeMinima = 1, quantidadeMaxima = 50, precoUnitarioOuFaixa = 1.50),
                FaixaPreco(id = 2, produtoId = 1, quantidadeMinima = 51, quantidadeMaxima = null, precoUnitarioOuFaixa = 1.10)
            ),
            aoSalvarFaixa = {},
            aoExcluirFaixa = {}
        )
    }
}
