package br.com.grafica.calculadora.apresentacao.telas.pedidos.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Fornecedor
import br.com.grafica.calculadora.dominio.modelo.ModalidadeProducao
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.nucleo.componentes.CartaoGlass
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr
import kotlin.math.max

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DialogoEdicaoPedido(
    pedido: Pedido,
    produtos: List<Produto>,
    fornecedores: List<Fornecedor>,
    aoFechar: () -> Unit,
    aoSalvar: (Pedido) -> Unit,
    aoExcluir: (Pedido) -> Unit,
    aoAbrirNaCalculadora: (Pedido) -> Unit
) {
    var nomeCliente by remember(pedido) { mutableStateOf(pedido.nomeCliente) }
    var telefoneCliente by remember(pedido) { mutableStateOf(pedido.telefoneCliente) }
    var nomeProduto by remember(pedido) { mutableStateOf(pedido.nomeProduto) }
    var especificacaoMaterial by remember(pedido) { mutableStateOf(pedido.especificacaoMaterial) }
    var especificacaoAcabamento by remember(pedido) { mutableStateOf(pedido.especificacaoAcabamento) }
    var quantidadeTexto by remember(pedido) { mutableStateOf(pedido.quantidade.toString()) }
    var precoUnitarioTexto by remember(pedido) { mutableStateOf(pedido.precoUnitarioAplicado.toString()) }
    var taxasTexto by remember(pedido) { mutableStateOf(pedido.valorTotalTaxas.toString()) }
    var modalidade by remember(pedido) { mutableStateOf(pedido.modalidadeProducao) }
    var nomeFornecedor by remember(pedido) { mutableStateOf(pedido.nomeFornecedor.orEmpty()) }
    var dataPrazo by remember(pedido) { mutableStateOf(pedido.dataPrazoEntrega) }
    var etapaPipeline by remember(pedido) { mutableStateOf(pedido.etapaPipeline) }
    var statusPagamento by remember(pedido) { mutableStateOf(pedido.statusPagamento) }

    val qtd = max(1, quantidadeTexto.toIntOrNull() ?: 1)
    val precoUnitario = precoUnitarioTexto.replace(',', '.').toDoubleOrNull() ?: 0.0
    val taxas = taxasTexto.replace(',', '.').toDoubleOrNull() ?: 0.0

    val ehLote100 = pedido.descricaoFaixaAplicada.contains("100", ignoreCase = true) &&
        !pedido.descricaoFaixaAplicada.contains("Faixa", ignoreCase = true)
    val subtotalRecalculado = if (ehLote100) {
        (qtd.toDouble() / 100.0) * precoUnitario
    } else {
        qtd * precoUnitario
    }
    val totalRecalculado = subtotalRecalculado + taxas

    Dialog(
        onDismissRequest = aoFechar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = CorBrancoSecundaria,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Editar Pedido ${pedido.codigoFormatado}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = CorVinhoPrimaria,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Alterações recalculam o total automaticamente",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { aoAbrirNaCalculadora(pedido) }) {
                        Text("Calculadora ->", color = CorVinhoPrimaria, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = nomeCliente,
                    onValueChange = { nomeCliente = it },
                    label = { Text("Cliente") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = telefoneCliente,
                        onValueChange = { telefoneCliente = it },
                        label = { Text("Telefone / WhatsApp") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dataPrazo,
                        onValueChange = { dataPrazo = it },
                        label = { Text("Prazo (dd/MM)") },
                        modifier = Modifier.weight(0.8f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = nomeProduto,
                    onValueChange = { nomeProduto = it },
                    label = { Text("Produto / Item do Pedido") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (produtos.isNotEmpty()) {
                    Text(
                        text = "Trocar por item do catálogo:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        produtos.take(6).forEach { prod ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (nomeProduto == prod.nome) CorDouradoTerciaria else Color(0xFFF1EBF0)
                                    )
                                    .clickable {
                                        nomeProduto = prod.nome
                                        especificacaoMaterial = prod.materialOuPapel
                                        especificacaoAcabamento = prod.acabamentoPadrao
                                        if (prod.precoBasePropria > 0) {
                                            precoUnitarioTexto = prod.precoBasePropria.toString()
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = prod.nome,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CorVinhoPrimaria
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantidadeTexto,
                        onValueChange = { quantidadeTexto = it.filter { c -> c.isDigit() } },
                        label = { Text("Qtd") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.9f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = precoUnitarioTexto,
                        onValueChange = { precoUnitarioTexto = it },
                        label = { Text("Valor Unit./Base (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1.2f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = taxasTexto,
                        onValueChange = { taxasTexto = it },
                        label = { Text("Taxas (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text(
                    text = "Modalidade e Fornecedor",
                    style = MaterialTheme.typography.labelLarge,
                    color = CorVinhoPrimaria
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ModalidadeProducao.entries.forEach { modo ->
                        FilterChip(
                            selected = modalidade == modo,
                            onClick = {
                                modalidade = modo
                                if (modo == ModalidadeProducao.PROPRIA) nomeFornecedor = ""
                                else if (nomeFornecedor.isBlank()) {
                                    nomeFornecedor = fornecedores.firstOrNull()?.nome ?: "GM PRINT"
                                }
                            },
                            label = { Text(modo.rotulo) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CorVinhoPrimaria,
                                selectedLabelColor = CorBrancoSecundaria
                            )
                        )
                    }
                }

                if (modalidade == ModalidadeProducao.TERCEIRIZADA) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val nomesFornecedores = fornecedores.map { it.nome }.ifEmpty { listOf("GM PRINT", "BURITI PRINTS") }
                        nomesFornecedores.forEach { nomeForn ->
                            FilterChip(
                                selected = nomeFornecedor.equals(nomeForn, ignoreCase = true),
                                onClick = { nomeFornecedor = nomeForn },
                                label = { Text(nomeForn) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CorDouradoTerciaria,
                                    selectedLabelColor = CorVinhoPrimaria
                                )
                            )
                        }
                    }
                }

                Text(
                    text = "Etapa do Pipeline (Quadro do Ateliê)",
                    style = MaterialTheme.typography.labelLarge,
                    color = CorVinhoPrimaria
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EtapaPipeline.entries.forEach { etapa ->
                        FilterChip(
                            selected = etapaPipeline == etapa,
                            onClick = { etapaPipeline = etapa },
                            label = { Text("${etapa.numeroEtapa}. ${etapa.rotulo}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CorVinhoPrimaria,
                                selectedLabelColor = CorBrancoSecundaria
                            )
                        )
                    }
                }

                Text(
                    text = "Status Financeiro (Pagamento)",
                    style = MaterialTheme.typography.labelLarge,
                    color = CorVinhoPrimaria
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusPagamento.entries.forEach { pgto ->
                        FilterChip(
                            selected = statusPagamento == pgto,
                            onClick = { statusPagamento = pgto },
                            label = { Text(pgto.rotulo) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CorDouradoTerciaria,
                                selectedLabelColor = CorVinhoPrimaria
                            )
                        )
                    }
                }

                CartaoGlass(raioCanto = 18.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Recalculado",
                                style = MaterialTheme.typography.labelMedium,
                                color = CorBrancoSecundaria.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Itens: ${formatarMoedaBr(subtotalRecalculado)} + Taxas: ${formatarMoedaBr(taxas)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = CorBrancoSecundaria.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = formatarMoedaBr(totalRecalculado),
                            style = MaterialTheme.typography.headlineMedium,
                            color = CorDouradoTerciaria,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { aoExcluir(pedido) }) {
                        Text("Excluir", color = Color(0xFFB91C1C), fontWeight = FontWeight.SemiBold)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = aoFechar) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                aoSalvar(
                                    pedido.copy(
                                        nomeCliente = nomeCliente.trim().ifBlank { pedido.nomeCliente },
                                        telefoneCliente = telefoneCliente.trim(),
                                        nomeProduto = nomeProduto.trim().ifBlank { pedido.nomeProduto },
                                        especificacaoMaterial = especificacaoMaterial.trim(),
                                        especificacaoAcabamento = especificacaoAcabamento.trim(),
                                        quantidade = qtd,
                                        precoUnitarioAplicado = precoUnitario,
                                        subtotalItens = subtotalRecalculado,
                                        valorTotalTaxas = taxas,
                                        valorTotalFinal = totalRecalculado,
                                        modalidadeProducao = modalidade,
                                        nomeFornecedor = if (modalidade == ModalidadeProducao.TERCEIRIZADA) nomeFornecedor else null,
                                        dataPrazoEntrega = dataPrazo.trim().ifBlank { "20/11" },
                                        etapaPipeline = etapaPipeline,
                                        statusPagamento = statusPagamento
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CorVinhoPrimaria,
                                contentColor = CorBrancoSecundaria
                            )
                        ) {
                            Text("Salvar Alterações")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogoEdicaoPedidoPreview() {
    TemaAtelie {
        DialogoEdicaoPedido(
            pedido = Pedido(
                id = 10,
                nomeCliente = "Mariana Costa",
                nomeProduto = "Caderno A5",
                especificacaoMaterial = "Sulfite",
                especificacaoGramatura = "90g",
                especificacaoFormato = "A5",
                especificacaoAcabamento = "Colorido",
                especificacaoFaces = "Frente e verso",
                quantidade = 10,
                precoUnitarioAplicado = 25.0,
                subtotalItens = 250.0,
                valorTotalFinal = 250.0
            ),
            produtos = emptyList(),
            fornecedores = emptyList(),
            aoFechar = {},
            aoSalvar = {},
            aoExcluir = {},
            aoAbrirNaCalculadora = {}
        )
    }
}
