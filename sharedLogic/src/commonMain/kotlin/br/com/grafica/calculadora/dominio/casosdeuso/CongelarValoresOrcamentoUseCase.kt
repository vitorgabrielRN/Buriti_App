package br.com.grafica.calculadora.dominio.casosdeuso

import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.repositorio.PedidoRepositorio

/**
 * UseCase: Congela os valores unitários de um orçamento no momento do fechamento/aprovação.
 *
 * Quando um pedido é Fechado/Aprovado, o snapshot dos valores de custo de produção
 * vigentes é gravado no pedido para garantir que alterações futuras na tabela de preços
 * padrão não alterem orçamentos já fechados.
 *
 * Estratégia: o campo [Pedido.precoUnitarioAplicado] e [Pedido.descricaoFaixaAplicada]
 * já representam o snapshot do momento do orçamento. Este UseCase avança a etapa
 * do pipeline para PRODUCAO (aprovação) e garante que esses valores não sejam
 * sobrescritos por recálculos automáticos posteriores.
 */
class CongelarValoresOrcamentoUseCase(
    private val pedidoRepositorio: PedidoRepositorio
) {

    /**
     * @param pedido O pedido a ser aprovado/fechado.
     * @return [Result.success] com o pedido congelado, ou [Result.failure] com a causa.
     */
    suspend operator fun invoke(pedido: Pedido): Result<Pedido> = runCatching {
        require(pedido.etapaPipeline == EtapaPipeline.ORCAMENTO) {
            "Apenas pedidos em orçamento podem ser aprovados/congelados."
        }

        val pedidoCongelado = pedido.copy(
            etapaPipeline = EtapaPipeline.PRODUCAO
            // precoUnitarioAplicado e descricaoFaixaAplicada permanecem inalterados —
            // são o snapshot do momento do fechamento.
        )

        pedidoRepositorio.atualizarPedido(pedidoCongelado)
        pedidoCongelado
    }
}
