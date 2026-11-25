package br.com.grafica.calculadora.dominio.modelo

data class Fornecedor(
    val id: Long = 0,
    val nome: String,
    val contatoOuObservacoes: String = "",
    val diasProducaoPadrao: Int = 3
)

data class OfertaFornecedor(
    val id: Long = 0,
    val produtoId: Long,
    val fornecedorId: Long,
    val nomeFornecedor: String,
    val precoBase: Double,
    val quantidadeBase: Int = 1,
    val quantidadeMinima: Int = 1,
    val diasProducao: Int = 3,
    val acabamento: String = "",
    val observacaoDivergenciaMaterial: String = ""
)

data class FaixaPreco(
    val id: Long = 0,
    val produtoId: Long,
    val ofertaFornecedorId: Long? = null,
    val quantidadeMinima: Int,
    val quantidadeMaxima: Int? = null,
    val precoUnitarioOuFaixa: Double,
    val precoPorLoteFechado: Boolean = false
)

data class TaxaAdicional(
    val id: Long = 0,
    val tipoTaxa: TipoTaxa,
    val nome: String,
    val valor: Double,
    val emPercentual: Boolean = false,
    val prazoLimiteDias: Int? = null,
    val aplicarAposHora: Int? = null,
    val descricao: String = ""
)
