package br.com.grafica.calculadora.dados.repositorio

import br.com.grafica.calculadora.dados.local.dao.FornecedorDao
import br.com.grafica.calculadora.dados.local.dao.OfertaFornecedorDao
import br.com.grafica.calculadora.dados.local.mapeador.paraDominio
import br.com.grafica.calculadora.dados.local.mapeador.paraEntidade
import br.com.grafica.calculadora.dominio.modelo.Fornecedor
import br.com.grafica.calculadora.dominio.modelo.OfertaFornecedor
import br.com.grafica.calculadora.dominio.repositorio.FornecedorRepositorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FornecedorRepositorioImpl(
    private val fornecedorDao: FornecedorDao,
    private val ofertaFornecedorDao: OfertaFornecedorDao
) : FornecedorRepositorio {

    override fun observarTodosFornecedores(): Flow<List<Fornecedor>> =
        fornecedorDao.observarTodos().map { lista -> lista.map { it.paraDominio() } }

    override fun observarTodasOfertas(): Flow<List<OfertaFornecedor>> =
        ofertaFornecedorDao.observarTodas().map { lista -> lista.map { it.paraDominio() } }

    override suspend fun salvarFornecedor(fornecedor: Fornecedor): Long {
        val entidade = fornecedor.paraEntidade()
        return if (entidade.id > 0L) {
            fornecedorDao.atualizar(entidade)
            entidade.id
        } else {
            fornecedorDao.inserir(entidade)
        }
    }

    override suspend fun excluirFornecedor(fornecedor: Fornecedor) {
        fornecedorDao.excluir(fornecedor.paraEntidade())
    }

    override suspend fun salvarOfertaFornecedor(oferta: OfertaFornecedor): Long {
        val entidade = oferta.paraEntidade()
        return if (entidade.id > 0L) {
            ofertaFornecedorDao.atualizar(entidade)
            entidade.id
        } else {
            ofertaFornecedorDao.inserir(entidade)
        }
    }

    override suspend fun excluirOfertaFornecedor(oferta: OfertaFornecedor) {
        ofertaFornecedorDao.excluir(oferta.paraEntidade())
    }
}
