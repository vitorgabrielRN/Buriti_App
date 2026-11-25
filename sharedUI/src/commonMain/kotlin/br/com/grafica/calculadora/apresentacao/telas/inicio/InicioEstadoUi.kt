package br.com.grafica.calculadora.apresentacao.telas.inicio

import br.com.grafica.calculadora.dominio.modelo.Pedido

data class InicioEstadoUi(
    val nomeProprietaria: String = "",
    val mesVigente: String = "Mês Atual",
    val saudacao: String = "Olá",
    val modoEstudioAtivo: Boolean = true,
    val metaMensal: Double = 0.0,
    val faturamentoMensal: Double = 0.0,
    val pedidosConcluidos: Int = 0,
    val valorAReceber: Double = 0.0,
    val totalProdutosCatalogo: Int = 0,
    val totalFornecedores: Int = 0,
    val totalPedidosRegistrados: Int = 0,
    val demandasPrioritarias: List<Pedido> = emptyList()
)

sealed interface InicioEventoUi {
    data object AlternarModoEstudio : InicioEventoUi
    data class AvancarEtapaPedido(val pedido: Pedido) : InicioEventoUi
    data object SairSessao : InicioEventoUi
}
