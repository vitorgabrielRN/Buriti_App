package br.com.grafica.calculadora.apresentacao.telas.cadastros

import br.com.grafica.calculadora.dominio.modelo.FaixaPreco
import br.com.grafica.calculadora.dominio.modelo.Fornecedor
import br.com.grafica.calculadora.dominio.modelo.OfertaFornecedor
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.dominio.modelo.TaxaAdicional

enum class SubAbaCadastros(val rotulo: String) {
    PRODUTOS("Insumos & Papéis"),
    FORNECEDORES("Fornecedores"),
    FAIXAS("Faixas de Qtd"),
    TAXAS_E_META("Taxas & Meta")
}

data class CadastrosEstadoUi(
    val subAbaSelecionada: SubAbaCadastros = SubAbaCadastros.PRODUTOS,
    val produtos: List<Produto> = emptyList(),
    val fornecedores: List<Fornecedor> = emptyList(),
    val ofertasFornecedores: List<OfertaFornecedor> = emptyList(),
    val faixasPreco: List<FaixaPreco> = emptyList(),
    val taxasAdicionais: List<TaxaAdicional> = emptyList(),
    val nomeProprietaria: String = "",
    val mesVigente: String = "Mês Atual",
    val metaMensal: Double = 0.0,
    val modoEstudioAtivo: Boolean = true,
    val mensagemFeedback: String? = null
)

sealed interface CadastrosEventoUi {
    data class SelecionarSubAba(val subAba: SubAbaCadastros) : CadastrosEventoUi
    data class SalvarProduto(val produto: Produto) : CadastrosEventoUi
    data class ExcluirProduto(val produto: Produto) : CadastrosEventoUi
    data class SalvarFornecedor(val fornecedor: Fornecedor) : CadastrosEventoUi
    data class ExcluirFornecedor(val fornecedor: Fornecedor) : CadastrosEventoUi
    data class SalvarOfertaFornecedor(val oferta: OfertaFornecedor) : CadastrosEventoUi
    data class ExcluirOfertaFornecedor(val oferta: OfertaFornecedor) : CadastrosEventoUi
    data class SalvarFaixaPreco(val faixa: FaixaPreco) : CadastrosEventoUi
    data class ExcluirFaixaPreco(val faixa: FaixaPreco) : CadastrosEventoUi
    data class SalvarTaxaAdicional(val taxa: TaxaAdicional) : CadastrosEventoUi
    data class ExcluirTaxaAdicional(val taxa: TaxaAdicional) : CadastrosEventoUi
    data class AtualizarMetaEstudio(
        val nomeProprietaria: String,
        val metaMensal: Double,
        val mesVigente: String
    ) : CadastrosEventoUi
    data object LimparFeedback : CadastrosEventoUi
}
