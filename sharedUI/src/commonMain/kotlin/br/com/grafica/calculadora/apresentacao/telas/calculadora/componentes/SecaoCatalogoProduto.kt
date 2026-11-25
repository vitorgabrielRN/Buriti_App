package br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEstadoUi
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEventoUi
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.nucleo.componentes.CartaoSecaoFormulario
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoContainer
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@Composable
fun SecaoCatalogoProduto(
    estado: CalculadoraEstadoUi,
    aoEnviarEvento: (CalculadoraEventoUi) -> Unit
) {
    val sugestoes = remember(estado.produtos, estado.consultaProduto) {
        filtrarProdutosPorIniciais(estado.produtos, estado.consultaProduto)
    }

    CartaoSecaoFormulario(
        titulo = "2. Produto",
        subtitulo = "Digite as iniciais para buscar no catálogo"
    ) {
        if (estado.produtos.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CorVinhoContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Nenhum produto cadastrado ainda. Cadastre seus produtos na aba “Painel” para começar a orçar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CorVinhoPrimaria,
                    modifier = Modifier.padding(12.dp)
                )
            }
            return@CartaoSecaoFormulario
        }

        OutlinedTextField(
            value = estado.consultaProduto,
            onValueChange = { aoEnviarEvento(CalculadoraEventoUi.AlterarBuscaProduto(it)) },
            label = { Text("Produto *") },
            placeholder = { Text("Ex: car, fly, ban, ade…") },
            trailingIcon = {
                TextButton(onClick = { aoEnviarEvento(CalculadoraEventoUi.AlternarListaProdutos) }) {
                    Text(if (estado.exibirSugestoesProdutos) "▲" else "▼", color = CorVinhoPrimaria)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (estado.exibirSugestoesProdutos) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CorBrancoSecundaria,
                shadowElevation = 4.dp,
                tonalElevation = 2.dp
            ) {
                if (sugestoes.isEmpty()) {
                    Text(
                        text = "Nenhum produto encontrado para “${estado.consultaProduto}”.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 280.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        sugestoes.forEachIndexed { indice, produto ->
                            ItemSugestaoProduto(
                                produto = produto,
                                selecionado = estado.produtoSelecionado?.id == produto.id,
                                aoSelecionar = { aoEnviarEvento(CalculadoraEventoUi.SelecionarProduto(produto)) }
                            )
                            if (indice < sugestoes.lastIndex) {
                                HorizontalDivider(color = Color(0x14000000))
                            }
                        }
                    }
                }
            }
        }

        estado.produtoSelecionado?.let { prod ->
            if (!estado.exibirSugestoesProdutos) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CorVinhoContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "✓ ${prod.nome}",
                            style = MaterialTheme.typography.titleSmall,
                            color = CorVinhoPrimaria,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = listOf(prod.materialOuPapel, prod.gramatura, prod.formatoOuTamanho)
                                .filter { it.isNotBlank() }
                                .joinToString(" • "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemSugestaoProduto(
    produto: Produto,
    selecionado: Boolean,
    aoSelecionar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { aoSelecionar() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = if (selecionado) "✓ ${produto.nome}" else produto.nome,
            style = MaterialTheme.typography.bodyLarge,
            color = CorVinhoPrimaria,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${produto.materialOuPapel} • ${produto.formatoOuTamanho} • a partir de ${formatarMoedaBr(produto.precoBasePropria)}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Busca responsiva por iniciais:
 * 1º nome começa com o texto, 2º alguma palavra começa com o texto,
 * 3º contém o texto, 4º as iniciais das palavras batem (ex.: "cv" -> "Cartão de Visita").
 */
private fun filtrarProdutosPorIniciais(produtos: List<Produto>, consulta: String): List<Produto> {
    val termo = consulta.trim().lowercase()
    if (termo.isEmpty()) return produtos.sortedBy { it.nome.lowercase() }

    return produtos.mapNotNull { produto ->
        val nome = produto.nome.lowercase()
        val palavras = nome.split(' ', '-', '/').filter { it.isNotBlank() }
        val iniciais = palavras.joinToString("") { it.first().toString() }
        val rank = when {
            nome.startsWith(termo) -> 0
            palavras.any { it.startsWith(termo) } -> 1
            nome.contains(termo) || produto.materialOuPapel.lowercase().contains(termo) -> 2
            ehSubsequencia(termo, iniciais) -> 3
            else -> null
        }
        rank?.let { it to produto }
    }.sortedWith(compareBy({ it.first }, { it.second.nome.lowercase() }))
        .map { it.second }
}

private fun ehSubsequencia(termo: String, alvo: String): Boolean {
    var i = 0
    for (c in alvo) {
        if (i < termo.length && termo[i] == c) i++
    }
    return i == termo.length && termo.isNotEmpty()
}

@Preview(showBackground = true)
@Composable
private fun SecaoCatalogoProdutoPreview() {
    TemaAtelie {
        val produto = Produto(
            id = 1,
            nome = "Cartão de Visita",
            materialOuPapel = "Couchê",
            gramatura = "300g",
            formatoOuTamanho = "9x5cm",
            acabamentoPadrao = "Colorido",
            opcoesFaces = "Frente e verso",
            precoBasePropria = 65.0
        )
        SecaoCatalogoProduto(
            estado = CalculadoraEstadoUi(
                produtos = listOf(produto),
                consultaProduto = "Car",
                exibirSugestoesProdutos = true,
                produtoSelecionado = produto
            ),
            aoEnviarEvento = {}
        )
    }
}
