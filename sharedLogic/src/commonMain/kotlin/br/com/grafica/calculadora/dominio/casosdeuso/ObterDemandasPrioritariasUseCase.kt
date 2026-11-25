package br.com.grafica.calculadora.dominio.casosdeuso

import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.repositorio.PedidoRepositorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * UseCase: Retorna um Flow com as demandas prioritárias para exibição na Home.
 *
 * Prioridade:
 *   1. Pedidos em PRODUCAO primeiro (já aprovados, em execução).
 *   2. Pedidos em ORCAMENTO em seguida (aguardam aprovação).
 *   3. Pedidos PRONTO são excluídos da gaveta (já concluídos).
 *   4. Dentro de cada grupo, ordena pelo prazo de entrega (mais próximo primeiro).
 *
 * Limita o resultado a [limite] itens para manter a Gaveta de Demandas compacta.
 */
class ObterDemandasPrioritariasUseCase(
    private val pedidoRepositorio: PedidoRepositorio
) {

    companion object {
        const val LIMITE_PADRAO = 10
    }

    operator fun invoke(limite: Int = LIMITE_PADRAO): Flow<List<Pedido>> =
        pedidoRepositorio.observarTodosPedidos().map { pedidos ->
            pedidos
                .filter { it.etapaPipeline != EtapaPipeline.PRONTO }
                .sortedWith(
                    compareBy<Pedido> { etapaPrioridade(it.etapaPipeline) }
                        .thenBy { it.dataPrazoEntrega }
                )
                .take(limite)
        }

    private fun etapaPrioridade(etapa: EtapaPipeline): Int = when (etapa) {
        EtapaPipeline.PRODUCAO  -> 0
        EtapaPipeline.ORCAMENTO -> 1
        EtapaPipeline.PRONTO    -> 2
    }
}
