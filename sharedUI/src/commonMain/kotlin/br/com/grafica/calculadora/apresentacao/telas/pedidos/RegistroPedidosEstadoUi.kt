package br.com.grafica.calculadora.apresentacao.telas.pedidos

import br.com.grafica.calculadora.dominio.modelo.Fornecedor
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento

data class RegistroPedidosEstadoUi(
    val todosPedidos: List<Pedido> = emptyList(),
    val pedidosFiltrados: List<Pedido> = emptyList(),
    val filtroPagamentoSelecionado: StatusPagamento? = null,
    val ordenarPagamentoCrescente: Boolean = true,
    val somaTotalFiltrada: Double = 0.0,
    val pedidoEmEdicao: Pedido? = null,
    val produtosCatalogo: List<Produto> = emptyList(),
    val fornecedores: List<Fornecedor> = emptyList(),
    val mensagemFeedback: String? = null
)

sealed interface RegistroPedidosEventoUi {
    data object AlternarOrdenacaoPagamento : RegistroPedidosEventoUi
    data class SelecionarFiltroPagamento(val filtro: StatusPagamento?) : RegistroPedidosEventoUi
    data class AbrirEdicaoPedido(val pedido: Pedido) : RegistroPedidosEventoUi
    data object FecharEdicaoPedido : RegistroPedidosEventoUi
    data class SalvarPedidoEditado(val pedido: Pedido) : RegistroPedidosEventoUi
    data class ExcluirPedido(val pedido: Pedido) : RegistroPedidosEventoUi
    data object LimparFeedback : RegistroPedidosEventoUi
}
