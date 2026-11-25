package br.com.grafica.calculadora.dados.repositorio

import br.com.grafica.calculadora.dados.local.dao.PedidoDao
import br.com.grafica.calculadora.dados.local.mapeador.paraDominio
import br.com.grafica.calculadora.dados.local.mapeador.paraEntidade
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.dominio.repositorio.ClienteRepositorio
import br.com.grafica.calculadora.dominio.repositorio.PedidoRepositorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PedidoRepositorioImpl(
    private val pedidoDao: PedidoDao,
    private val clienteRepositorio: ClienteRepositorio
) : PedidoRepositorio {

    override fun observarTodosPedidos(): Flow<List<Pedido>> =
        pedidoDao.observarTodos().map { lista -> lista.map { it.paraDominio() } }

    override suspend fun salvarPedidoComCliente(
        clienteIdSelecionado: Long?,
        nomeCliente: String,
        documentoCliente: String,
        telefoneCliente: String,
        pedido: Pedido
    ): Result<Long> = runCatching {
        val clienteIdFinal = clienteRepositorio.salvarOuAtualizarCliente(
            clienteIdSelecionado = clienteIdSelecionado,
            nome = nomeCliente,
            documento = documentoCliente,
            telefone = telefoneCliente
        )

        val entidade = pedido.copy(
            clienteId = clienteIdFinal,
            nomeCliente = nomeCliente.trim(),
            documentoCliente = documentoCliente.trim(),
            telefoneCliente = telefoneCliente.trim()
        ).paraEntidade()

        if (entidade.id > 0L) {
            pedidoDao.atualizar(entidade)
            entidade.id
        } else {
            pedidoDao.inserir(entidade)
        }
    }

    override suspend fun atualizarPedido(pedido: Pedido) {
        if (pedido.nomeCliente.isNotBlank()) {
            clienteRepositorio.salvarOuAtualizarCliente(
                clienteIdSelecionado = pedido.clienteId,
                nome = pedido.nomeCliente,
                documento = pedido.documentoCliente,
                telefone = pedido.telefoneCliente
            )
        }
        pedidoDao.atualizar(pedido.paraEntidade())
    }

    override suspend fun atualizarEtapaPedido(pedidoId: Long, novaEtapa: EtapaPipeline) {
        pedidoDao.atualizarEtapa(pedidoId, novaEtapa)
    }

    override suspend fun avancarEtapaPedido(pedido: Pedido): Boolean {
        val proxima = pedido.etapaPipeline.proximaEtapa() ?: return false
        pedidoDao.atualizarEtapa(pedido.id, proxima)
        return true
    }

    override suspend fun atualizarStatusPagamento(pedidoId: Long, novoStatus: StatusPagamento) {
        pedidoDao.atualizarStatusPagamento(pedidoId, novoStatus)
    }

    override suspend fun excluirPedido(pedido: Pedido) {
        pedidoDao.excluir(pedido.paraEntidade())
    }
}
