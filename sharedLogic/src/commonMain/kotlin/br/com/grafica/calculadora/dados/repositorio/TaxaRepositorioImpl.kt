package br.com.grafica.calculadora.dados.repositorio

import br.com.grafica.calculadora.dados.local.dao.TaxaAdicionalDao
import br.com.grafica.calculadora.dados.local.entidade.TaxaAdicionalEntidade
import br.com.grafica.calculadora.dados.local.mapeador.paraDominio
import br.com.grafica.calculadora.dados.local.mapeador.paraEntidade
import br.com.grafica.calculadora.dominio.modelo.TaxaAdicional
import br.com.grafica.calculadora.dominio.modelo.TipoTaxa
import br.com.grafica.calculadora.dominio.repositorio.TaxaRepositorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaxaRepositorioImpl(
    private val taxaAdicionalDao: TaxaAdicionalDao
) : TaxaRepositorio {

    override fun observarTodasTaxas(): Flow<List<TaxaAdicional>> =
        taxaAdicionalDao.observarTodas().map { lista -> lista.map { it.paraDominio() } }

    override suspend fun salvarTaxa(taxa: TaxaAdicional): Long {
        val entidade = taxa.paraEntidade()
        return if (entidade.id > 0L) {
            taxaAdicionalDao.atualizar(entidade)
            entidade.id
        } else {
            taxaAdicionalDao.inserir(entidade)
        }
    }

    override suspend fun excluirTaxa(taxa: TaxaAdicional) {
        taxaAdicionalDao.excluir(taxa.paraEntidade())
    }

    override suspend fun garantirDadosIniciais() {
        if (taxaAdicionalDao.contar() == 0) {
            taxaAdicionalDao.inserirVarias(
                listOf(
                    TaxaAdicionalEntidade(
                        tipoTaxa = TipoTaxa.URGENCIA,
                        nome = "Taxa de Urgência",
                        valor = 20.0,
                        emPercentual = true,
                        prazoLimiteDias = 2,
                        descricao = "Aplicada quando o prazo solicitado é menor que o prazo normal (até 2 dias úteis)."
                    ),
                    TaxaAdicionalEntidade(
                        tipoTaxa = TipoTaxa.EMERGENCIA,
                        nome = "Taxa de Emergência",
                        valor = 40.0,
                        emPercentual = true,
                        prazoLimiteDias = 0,
                        descricao = "Aplicada para produção expressa no mesmo dia / 24h."
                    ),
                    TaxaAdicionalEntidade(
                        tipoTaxa = TipoTaxa.ENTREGA,
                        nome = "Taxa de Entrega",
                        valor = 15.0,
                        emPercentual = false,
                        descricao = "Aplicada quando o cliente solicita entrega realizada pela gráfica."
                    ),
                    TaxaAdicionalEntidade(
                        tipoTaxa = TipoTaxa.ADICIONAL_NOTURNO,
                        nome = "Adicional Noturno (após 18h)",
                        valor = 15.0,
                        emPercentual = false,
                        aplicarAposHora = 18,
                        descricao = "Aplicado quando o cliente solicita a demanda após as 18:00."
                    )
                )
            )
        }
    }
}
