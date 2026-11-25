package br.com.grafica.calculadora.apresentacao.telas.quadro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.repositorio.PedidoRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuadroAtelieViewModel(
    private val pedidoRepositorio: PedidoRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(QuadroAtelieEstadoUi())
    val estado: StateFlow<QuadroAtelieEstadoUi> = _estado.asStateFlow()

    init {
        observarPedidosKanban()
    }

    private fun observarPedidosKanban() {
        viewModelScope.launch {
            pedidoRepositorio.observarTodosPedidos().collect { pedidos ->
                val agrupados = EtapaPipeline.entries.associateWith { etapa ->
                    pedidos.filter { it.etapaPipeline == etapa }
                }
                _estado.value = QuadroAtelieEstadoUi(pedidosPorEtapa = agrupados)
            }
        }
    }

    fun aoReceberEvento(evento: QuadroAtelieEventoUi) {
        when (evento) {
            is QuadroAtelieEventoUi.AvancarEtapa -> {
                viewModelScope.launch {
                    pedidoRepositorio.avancarEtapaPedido(evento.pedido)
                }
            }
        }
    }
}
