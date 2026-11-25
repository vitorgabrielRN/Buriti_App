package br.com.grafica.calculadora.apresentacao.telas.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.repositorio.FornecedorRepositorio
import br.com.grafica.calculadora.dominio.repositorio.PedidoRepositorio
import br.com.grafica.calculadora.dominio.repositorio.ProdutoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroPedidosViewModel(
    private val pedidoRepositorio: PedidoRepositorio,
    private val produtoRepositorio: ProdutoRepositorio,
    private val fornecedorRepositorio: FornecedorRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(RegistroPedidosEstadoUi())
    val estado: StateFlow<RegistroPedidosEstadoUi> = _estado.asStateFlow()

    init {
        observarPedidosECatalogo()
    }

    private fun observarPedidosECatalogo() {
        viewModelScope.launch {
            combine(
                pedidoRepositorio.observarTodosPedidos(),
                produtoRepositorio.observarTodosProdutos(),
                fornecedorRepositorio.observarTodosFornecedores()
            ) { pedidos, produtos, fornecedores ->
                Triple(pedidos, produtos, fornecedores)
            }.collect { (pedidos, produtos, fornecedores) ->
                _estado.update { atual ->
                    recalcularFiltroEOrdenacao(
                        atual.copy(
                            todosPedidos = pedidos,
                            produtosCatalogo = produtos,
                            fornecedores = fornecedores
                        )
                    )
                }
            }
        }
    }

    fun aoReceberEvento(evento: RegistroPedidosEventoUi) {
        when (evento) {
            RegistroPedidosEventoUi.AlternarOrdenacaoPagamento -> {
                _estado.update {
                    recalcularFiltroEOrdenacao(
                        it.copy(ordenarPagamentoCrescente = !it.ordenarPagamentoCrescente)
                    )
                }
            }

            is RegistroPedidosEventoUi.SelecionarFiltroPagamento -> {
                _estado.update {
                    recalcularFiltroEOrdenacao(it.copy(filtroPagamentoSelecionado = evento.filtro))
                }
            }

            is RegistroPedidosEventoUi.AbrirEdicaoPedido -> {
                _estado.update { it.copy(pedidoEmEdicao = evento.pedido) }
            }

            RegistroPedidosEventoUi.FecharEdicaoPedido -> {
                _estado.update { it.copy(pedidoEmEdicao = null) }
            }

            is RegistroPedidosEventoUi.SalvarPedidoEditado -> {
                viewModelScope.launch {
                    pedidoRepositorio.atualizarPedido(evento.pedido)
                    _estado.update {
                        it.copy(
                            pedidoEmEdicao = null,
                            mensagemFeedback = "Pedido ${evento.pedido.codigoFormatado} atualizado com sucesso!"
                        )
                    }
                }
            }

            is RegistroPedidosEventoUi.ExcluirPedido -> {
                viewModelScope.launch {
                    pedidoRepositorio.excluirPedido(evento.pedido)
                    _estado.update {
                        it.copy(
                            pedidoEmEdicao = null,
                            mensagemFeedback = "Pedido ${evento.pedido.codigoFormatado} removido."
                        )
                    }
                }
            }

            RegistroPedidosEventoUi.LimparFeedback -> {
                _estado.update { it.copy(mensagemFeedback = null) }
            }
        }
    }

    private fun recalcularFiltroEOrdenacao(estado: RegistroPedidosEstadoUi): RegistroPedidosEstadoUi {
        val filtrados = estado.todosPedidos.filter { pedido ->
            estado.filtroPagamentoSelecionado == null ||
                pedido.statusPagamento == estado.filtroPagamentoSelecionado
        }
        val ordenados = if (estado.ordenarPagamentoCrescente) {
            filtrados.sortedWith(compareBy<Pedido> { it.statusPagamento.ordemClassificacao }.thenBy { it.id })
        } else {
            filtrados.sortedWith(compareByDescending<Pedido> { it.statusPagamento.ordemClassificacao }.thenByDescending { it.id })
        }
        return estado.copy(
            pedidosFiltrados = ordenados,
            somaTotalFiltrada = ordenados.sumOf { it.valorTotalFinal }
        )
    }
}
