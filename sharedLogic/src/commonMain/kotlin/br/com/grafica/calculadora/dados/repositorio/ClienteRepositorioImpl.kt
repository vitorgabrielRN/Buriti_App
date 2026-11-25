package br.com.grafica.calculadora.dados.repositorio

import br.com.grafica.calculadora.dados.local.dao.ClienteDao
import br.com.grafica.calculadora.dados.local.entidade.ClienteEntidade
import br.com.grafica.calculadora.dados.local.mapeador.paraDominio
import br.com.grafica.calculadora.dominio.modelo.Cliente
import br.com.grafica.calculadora.dominio.repositorio.ClienteRepositorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClienteRepositorioImpl(
    private val clienteDao: ClienteDao
) : ClienteRepositorio {

    override suspend fun buscarClientesPorNomeOuDocumento(consulta: String): List<Cliente> {
        val limpo = consulta.trim()
        if (limpo.isEmpty()) return emptyList()
        return clienteDao.buscarPorNomeOuDocumento(limpo).map { it.paraDominio() }
    }

    override fun observarTodosClientes(): Flow<List<Cliente>> =
        clienteDao.observarTodos().map { lista -> lista.map { it.paraDominio() } }

    override suspend fun salvarOuAtualizarCliente(
        clienteIdSelecionado: Long?,
        nome: String,
        documento: String,
        telefone: String
    ): Long {
        val nomeLimpo = nome.trim()
        val docLimpo = documento.trim()
        val telLimpo = telefone.trim()

        val clienteExistente = when {
            clienteIdSelecionado != null && clienteIdSelecionado > 0L -> {
                ClienteEntidade(
                    id = clienteIdSelecionado,
                    nome = nomeLimpo,
                    documento = docLimpo,
                    telefone = telLimpo
                )
            }
            docLimpo.isNotEmpty() -> {
                clienteDao.encontrarPorNomeEDocumento(nomeLimpo, docLimpo)
                    ?: clienteDao.encontrarPorNomeExato(nomeLimpo)
            }
            else -> clienteDao.encontrarPorNomeExato(nomeLimpo)
        }

        return if (clienteExistente != null && clienteExistente.id > 0L) {
            val atualizado = clienteExistente.copy(
                nome = nomeLimpo,
                documento = docLimpo,
                telefone = telLimpo
            )
            clienteDao.atualizar(atualizado)
            clienteExistente.id
        } else {
            clienteDao.inserir(
                ClienteEntidade(
                    nome = nomeLimpo,
                    documento = docLimpo,
                    telefone = telLimpo
                )
            )
        }
    }
}
