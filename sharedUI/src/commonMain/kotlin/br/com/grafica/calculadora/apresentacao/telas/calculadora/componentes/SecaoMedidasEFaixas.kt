package br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEstadoUi
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEventoUi
import br.com.grafica.calculadora.dominio.modelo.ModoPrecificacao
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.nucleo.componentes.CartaoSecaoFormulario
import br.com.grafica.calculadora.nucleo.tema.CorDouradoContainer
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoContainer
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecaoMedidasEFaixas(
    estado: CalculadoraEstadoUi,
    aoEnviarEvento: (CalculadoraEventoUi) -> Unit
) {
    val produto = estado.produtoSelecionado ?: return
    val orcamento = estado.orcamentoCalculado
    val faixasProduto = remember(estado.faixasPreco, produto) {
        estado.faixasPreco.filter { it.produtoId == produto.id && it.ofertaFornecedorId == null }
    }
    val qtdMinima = produto.quantidadeMinimaPropria.coerceAtLeast(1)
    val atalhos = remember(qtdMinima) {
        listOf(1, 10, 50, 100, 250, 500, 1000).filter { it >= qtdMinima }.ifEmpty { listOf(qtdMinima) }
    }
    val passo = if (produto.quantidadeBasePropria > 1) produto.quantidadeBasePropria else 1

    CartaoSecaoFormulario(
        titulo = "3. Quantidade e Impressão",
        subtitulo = produto.modoPrecificacao.rotulo
    ) {
        // ---------- Medidas (m²) ----------
        if (produto.modoPrecificacao == ModoPrecificacao.AREA_M2) {
            Surface(shape = RoundedCornerShape(12.dp), color = CorDouradoContainer, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "📐 Medidas (Largura × Altura em metros)",
                        style = MaterialTheme.typography.labelLarge,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = estado.larguraMetrosTexto,
                            onValueChange = { aoEnviarEvento(CalculadoraEventoUi.AlterarLarguraMetros(it)) },
                            label = { Text("Largura (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = estado.alturaMetrosTexto,
                            onValueChange = { aoEnviarEvento(CalculadoraEventoUi.AlterarAlturaMetros(it)) },
                            label = { Text("Altura (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // ---------- Quantidade com stepper ----------
        Text("Quantidade (unidades)", style = MaterialTheme.typography.labelLarge, color = CorVinhoPrimaria)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BotaoStepper("−") { aoEnviarEvento(CalculadoraEventoUi.IncrementarQuantidade(-passo)) }
            OutlinedTextField(
                value = estado.quantidadeTexto,
                onValueChange = { aoEnviarEvento(CalculadoraEventoUi.AlterarQuantidade(it)) },
                suffix = { Text("un") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            BotaoStepper("+") { aoEnviarEvento(CalculadoraEventoUi.IncrementarQuantidade(passo)) }
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            atalhos.forEach { q ->
                FilterChip(
                    selected = estado.quantidadeTexto == q.toString(),
                    onClick = { aoEnviarEvento(CalculadoraEventoUi.SelecionarQuantidadeRapida(q)) },
                    label = { Text("$q un") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CorDouradoTerciaria,
                        selectedLabelColor = CorVinhoPrimaria
                    )
                )
            }
        }

        if (qtdMinima > 1 || passo > 1) {
            Text(
                text = buildString {
                    if (qtdMinima > 1) append("Mínimo: $qtdMinima un")
                    if (qtdMinima > 1 && passo > 1) append(" • ")
                    if (passo > 1) append("Vendido em lotes de $passo un")
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // ---------- Quantidade × Valor = Subtotal ----------
        if (orcamento != null) {
            Surface(shape = RoundedCornerShape(14.dp), color = CorVinhoPrimaria, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${orcamento.quantidadeEfetiva} un × ${formatarMoedaBr(orcamento.precoUnitarioAplicado)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = orcamento.descricaoFaixaAplicada,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                    Text(
                        text = "= ${formatarMoedaBr(orcamento.subtotalItens)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = CorDouradoTerciaria,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            if (!orcamento.atendeQuantidadeMinima) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ Quantidade abaixo do mínimo (${orcamento.quantidadeMinimaExigida} un).",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // ---------- Faixas de quantidade ----------
        if (faixasProduto.isNotEmpty()) {
            val qtdAtual = estado.quantidadeTexto.toIntOrNull() ?: 1
            Surface(shape = RoundedCornerShape(12.dp), color = CorVinhoContainer, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Preço por faixa", style = MaterialTheme.typography.labelLarge, color = CorVinhoPrimaria, fontWeight = FontWeight.Bold)
                    faixasProduto.forEach { faixa ->
                        val maxQtd = faixa.quantidadeMaxima
                        val ativa = qtdAtual >= faixa.quantidadeMinima && (maxQtd == null || qtdAtual <= maxQtd)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (ativa) CorDouradoTerciaria else Color.Transparent)
                                .clickable { aoEnviarEvento(CalculadoraEventoUi.SelecionarQuantidadeRapida(faixa.quantidadeMinima)) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (maxQtd != null) "${faixa.quantidadeMinima} – $maxQtd un" else "${faixa.quantidadeMinima}+ un",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (ativa) FontWeight.Bold else FontWeight.Normal,
                                color = CorVinhoPrimaria
                            )
                            Text(
                                text = formatarMoedaBr(faixa.precoUnitarioOuFaixa) + if (faixa.precoPorLoteFechado) " (lote)" else " / un",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = CorVinhoPrimaria
                            )
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = estado.precoPersonalizadoTexto,
            onValueChange = { aoEnviarEvento(CalculadoraEventoUi.AlterarPrecoPersonalizado(it)) },
            label = { Text("Valor unitário manual (opcional)") },
            placeholder = { Text("Automático: ${formatarMoedaBr(orcamento?.precoUnitarioAplicado ?: 0.0)}") },
            prefix = { Text("R$ ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // ---------- Impressão: Colorido (switch) + Frente e verso (checkbox) ----------
        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFAF6F9), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Impressão colorida", style = MaterialTheme.typography.titleSmall, color = CorVinhoPrimaria, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (estado.impressaoColorida) "Colorido" else "Preto e branco",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = estado.impressaoColorida,
                        onCheckedChange = { aoEnviarEvento(CalculadoraEventoUi.AlternarColorido(it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = CorDouradoTerciaria, checkedTrackColor = CorVinhoPrimaria)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { aoEnviarEvento(CalculadoraEventoUi.AlternarFrenteEVerso(!estado.frenteEVerso)) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = estado.frenteEVerso,
                        onCheckedChange = { aoEnviarEvento(CalculadoraEventoUi.AlternarFrenteEVerso(it)) },
                        colors = CheckboxDefaults.colors(checkedColor = CorVinhoPrimaria)
                    )
                    Text("Frente e verso", style = MaterialTheme.typography.bodyMedium, color = CorVinhoPrimaria, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun BotaoStepper(rotulo: String, aoClicar: () -> Unit) {
    FilledTonalButton(
        onClick = aoClicar,
        modifier = Modifier.size(56.dp),
        shape = RoundedCornerShape(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(rotulo, style = MaterialTheme.typography.titleLarge, color = CorVinhoPrimaria, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecaoMedidasEFaixasPreview() {
    TemaAtelie {
        val produto = Produto(
            id = 1,
            nome = "Flyer A5",
            materialOuPapel = "Couchê",
            gramatura = "115g",
            formatoOuTamanho = "A5",
            acabamentoPadrao = "Colorido",
            opcoesFaces = "Frente e verso",
            precoBasePropria = 0.60
        )
        SecaoMedidasEFaixas(
            estado = CalculadoraEstadoUi(
                produtoSelecionado = produto,
                quantidadeTexto = "100",
                impressaoColorida = true,
                frenteEVerso = true
            ),
            aoEnviarEvento = {}
        )
    }
}
