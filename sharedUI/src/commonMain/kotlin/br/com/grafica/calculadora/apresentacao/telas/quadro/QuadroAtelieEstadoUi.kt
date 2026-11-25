package br.com.grafica.calculadora.apresentacao.telas.quadro

import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Pedido

data class QuadroAtelieEstadoUi(
    val pedidosPorEtapa: Map<EtapaPipeline, List<Pedido>> = emptyMap()
)

sealed interface QuadroAtelieEventoUi {
    data class AvancarEtapa(val pedido: Pedido) : QuadroAtelieEventoUi
}
