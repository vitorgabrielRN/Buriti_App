package br.com.grafica.calculadora.apresentacao.telas.cadastros

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.cadastros.componentes.SecaoCadastroFaixasPreco
import br.com.grafica.calculadora.apresentacao.telas.cadastros.componentes.SecaoCadastroFornecedores
import br.com.grafica.calculadora.apresentacao.telas.cadastros.componentes.SecaoCadastroProdutos
import br.com.grafica.calculadora.apresentacao.telas.cadastros.componentes.SecaoTaxasEMeta
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoProfundo
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

@Composable
fun TelaCadastros(
    estado: CadastrosEstadoUi,
    aoEnviarEvento: (CadastrosEventoUi) -> Unit,
    preenchimentoConteudo: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(preenchimentoConteudo)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column {
            Text(
                text = "Autonomia Total da Administradora",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Painel Gerencial (CRUD)",
                style = MaterialTheme.typography.displaySmall,
                color = CorVinhoPrimaria,
                fontWeight = FontWeight.Bold
            )
        }

        if (estado.mensagemFeedback != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDFF6E8),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "✓ ${estado.mensagemFeedback}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF116337),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(SubAbaCadastros.entries) { subAba ->
                val selecionada = estado.subAbaSelecionada == subAba
                FilterChip(
                    selected = selecionada,
                    onClick = { aoEnviarEvento(CadastrosEventoUi.SelecionarSubAba(subAba)) },
                    label = { Text(subAba.rotulo) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CorVinhoPrimaria,
                        selectedLabelColor = CorBrancoSecundaria
                    )
                )
            }
        }

        when (estado.subAbaSelecionada) {
            SubAbaCadastros.PRODUTOS -> SecaoCadastroProdutos(
                produtos = estado.produtos,
                aoSalvarProduto = { aoEnviarEvento(CadastrosEventoUi.SalvarProduto(it)) },
                aoExcluirProduto = { aoEnviarEvento(CadastrosEventoUi.ExcluirProduto(it)) }
            )

            SubAbaCadastros.FORNECEDORES -> SecaoCadastroFornecedores(
                produtos = estado.produtos,
                fornecedores = estado.fornecedores,
                ofertas = estado.ofertasFornecedores,
                aoSalvarFornecedor = { aoEnviarEvento(CadastrosEventoUi.SalvarFornecedor(it)) },
                aoExcluirFornecedor = { aoEnviarEvento(CadastrosEventoUi.ExcluirFornecedor(it)) },
                aoSalvarOferta = { aoEnviarEvento(CadastrosEventoUi.SalvarOfertaFornecedor(it)) },
                aoExcluirOferta = { aoEnviarEvento(CadastrosEventoUi.ExcluirOfertaFornecedor(it)) }
            )

            SubAbaCadastros.FAIXAS -> SecaoCadastroFaixasPreco(
                produtos = estado.produtos,
                faixas = estado.faixasPreco,
                aoSalvarFaixa = { aoEnviarEvento(CadastrosEventoUi.SalvarFaixaPreco(it)) },
                aoExcluirFaixa = { aoEnviarEvento(CadastrosEventoUi.ExcluirFaixaPreco(it)) }
            )

            SubAbaCadastros.TAXAS_E_META -> SecaoTaxasEMeta(
                estado = estado,
                aoSalvarTaxa = { aoEnviarEvento(CadastrosEventoUi.SalvarTaxaAdicional(it)) },
                aoExcluirTaxa = { aoEnviarEvento(CadastrosEventoUi.ExcluirTaxaAdicional(it)) },
                aoAtualizarMeta = { nome, meta, mes ->
                    aoEnviarEvento(CadastrosEventoUi.AtualizarMetaEstudio(nome, meta, mes))
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TelaCadastrosPreview() {
    TemaAtelie {
        TelaCadastros(
            estado = CadastrosEstadoUi(
                nomeProprietaria = "Ateliê",
                mesVigente = "Novembro",
                metaMensal = 4000.0
            ),
            aoEnviarEvento = {}
        )
    }
}
