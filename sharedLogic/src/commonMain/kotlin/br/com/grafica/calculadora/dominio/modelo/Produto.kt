package br.com.grafica.calculadora.dominio.modelo

data class Produto(
    val id: Long = 0,
    val nome: String,
    val materialOuPapel: String,
    val gramatura: String,
    val formatoOuTamanho: String,
    val acabamentoPadrao: String,
    val opcoesFaces: String,
    val modoPrecificacao: ModoPrecificacao = ModoPrecificacao.UNITARIO_OU_LOTE,
    val aceitaProducaoPropria: Boolean = true,
    val precoBasePropria: Double = 0.0,
    val quantidadeBasePropria: Int = 1,
    val quantidadeMinimaPropria: Int = 1,
    val diasProducaoPropria: Int = 2,
    val acabamentoPropria: String = "",
    val observacoesPropria: String = ""
)
