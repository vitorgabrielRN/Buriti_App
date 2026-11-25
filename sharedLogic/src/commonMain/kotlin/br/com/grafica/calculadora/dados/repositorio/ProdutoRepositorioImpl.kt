package br.com.grafica.calculadora.dados.repositorio

import br.com.grafica.calculadora.dados.local.dao.FaixaPrecoDao
import br.com.grafica.calculadora.dados.local.dao.ProdutoDao
import br.com.grafica.calculadora.dados.local.mapeador.paraDominio
import br.com.grafica.calculadora.dados.local.mapeador.paraEntidade
import br.com.grafica.calculadora.dominio.modelo.FaixaPreco
import br.com.grafica.calculadora.dominio.modelo.MetodoEntrega
import br.com.grafica.calculadora.dominio.modelo.ModalidadeProducao
import br.com.grafica.calculadora.dominio.modelo.ModoPrecificacao
import br.com.grafica.calculadora.dominio.modelo.OfertaFornecedor
import br.com.grafica.calculadora.dominio.modelo.OpcaoComparativoFornecedor
import br.com.grafica.calculadora.dominio.modelo.Orcamento
import br.com.grafica.calculadora.dominio.modelo.PrioridadePrazo
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.dominio.modelo.TaxaAdicional
import br.com.grafica.calculadora.dominio.modelo.TipoTaxa
import br.com.grafica.calculadora.dominio.repositorio.ProdutoRepositorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

class ProdutoRepositorioImpl(
    private val produtoDao: ProdutoDao,
    private val faixaPrecoDao: FaixaPrecoDao
) : ProdutoRepositorio {

    override fun observarTodosProdutos(): Flow<List<Produto>> =
        produtoDao.observarTodos().map { lista -> lista.map { it.paraDominio() } }

    override fun observarTodasFaixasPreco(): Flow<List<FaixaPreco>> =
        faixaPrecoDao.observarTodas().map { lista -> lista.map { it.paraDominio() } }

    override suspend fun salvarProduto(produto: Produto): Long {
        val entidade = produto.paraEntidade()
        return if (entidade.id > 0L) {
            produtoDao.atualizar(entidade)
            entidade.id
        } else {
            produtoDao.inserir(entidade)
        }
    }

    override suspend fun excluirProduto(produto: Produto) {
        produtoDao.excluir(produto.paraEntidade())
    }

    override suspend fun salvarFaixaPreco(faixa: FaixaPreco): Long {
        val entidade = faixa.paraEntidade()
        return if (entidade.id > 0L) {
            faixaPrecoDao.atualizar(entidade)
            entidade.id
        } else {
            faixaPrecoDao.inserir(entidade)
        }
    }

    override suspend fun excluirFaixaPreco(faixa: FaixaPreco) {
        faixaPrecoDao.excluir(faixa.paraEntidade())
    }

    override fun compararFornecedores(
        produto: Produto,
        ofertas: List<OfertaFornecedor>,
        faixas: List<FaixaPreco>,
        quantidade: Int,
        larguraMetros: Double,
        alturaMetros: Double,
        precoPersonalizado: Double?
    ): List<OpcaoComparativoFornecedor> {
        val qtdSegura = max(1, quantidade)
        val opcoes = mutableListOf<OpcaoComparativoFornecedor>()

        if (produto.aceitaProducaoPropria) {
            val faixasProprias = faixas.filter { it.ofertaFornecedorId == null }
            val (precoAplicado, descricaoFaixa, subtotal) = calcularValoresItem(
                modoPrecificacao = produto.modoPrecificacao,
                precoBase = precoPersonalizado ?: produto.precoBasePropria,
                quantidadeBase = produto.quantidadeBasePropria,
                faixas = faixasProprias,
                quantidade = qtdSegura,
                larguraMetros = larguraMetros,
                alturaMetros = alturaMetros
            )
            opcoes.add(
                OpcaoComparativoFornecedor(
                    chaveOpcao = "PROPRIA",
                    modalidade = ModalidadeProducao.PROPRIA,
                    fornecedorId = null,
                    ofertaFornecedorId = null,
                    titulo = "Produção Própria (Ateliê)",
                    precoBase = produto.precoBasePropria,
                    quantidadeBase = produto.quantidadeBasePropria,
                    quantidadeMinima = produto.quantidadeMinimaPropria,
                    diasProducao = produto.diasProducaoPropria,
                    acabamento = produto.acabamentoPropria.ifBlank { produto.acabamentoPadrao },
                    observacaoDivergenciaMaterial = produto.observacoesPropria,
                    precoAplicadoPorUnidadeOuLote = precoAplicado,
                    descricaoFaixaAplicada = descricaoFaixa,
                    subtotalEstimado = subtotal,
                    atendeQuantidadeMinima = qtdSegura >= produto.quantidadeMinimaPropria
                )
            )
        }

        ofertas.forEach { oferta ->
            val faixasOferta = faixas.filter { it.ofertaFornecedorId == oferta.id }
            val modoOferta = if (faixasOferta.isNotEmpty()) ModoPrecificacao.FAIXA_QUANTIDADE else produto.modoPrecificacao
            val (precoAplicado, descricaoFaixa, subtotal) = calcularValoresItem(
                modoPrecificacao = modoOferta,
                precoBase = precoPersonalizado ?: oferta.precoBase,
                quantidadeBase = oferta.quantidadeBase,
                faixas = faixasOferta,
                quantidade = qtdSegura,
                larguraMetros = larguraMetros,
                alturaMetros = alturaMetros
            )
            opcoes.add(
                OpcaoComparativoFornecedor(
                    chaveOpcao = "FORNECEDOR_${oferta.id}",
                    modalidade = ModalidadeProducao.TERCEIRIZADA,
                    fornecedorId = oferta.fornecedorId,
                    ofertaFornecedorId = oferta.id,
                    titulo = oferta.nomeFornecedor,
                    precoBase = oferta.precoBase,
                    quantidadeBase = oferta.quantidadeBase,
                    quantidadeMinima = oferta.quantidadeMinima,
                    diasProducao = oferta.diasProducao,
                    acabamento = oferta.acabamento.ifBlank { produto.acabamentoPadrao },
                    observacaoDivergenciaMaterial = oferta.observacaoDivergenciaMaterial,
                    precoAplicadoPorUnidadeOuLote = precoAplicado,
                    descricaoFaixaAplicada = descricaoFaixa,
                    subtotalEstimado = subtotal,
                    atendeQuantidadeMinima = qtdSegura >= oferta.quantidadeMinima
                )
            )
        }

        return opcoes
    }

    override fun gerarOrcamentoCompleto(
        produto: Produto,
        opcaoSelecionada: OpcaoComparativoFornecedor?,
        faixas: List<FaixaPreco>,
        taxas: List<TaxaAdicional>,
        quantidade: Int,
        larguraMetros: Double,
        alturaMetros: Double,
        precoPersonalizado: Double?,
        facesSelecionadas: String,
        acabamentoSelecionado: String,
        metodoEntrega: MetodoEntrega,
        dataEntregaPrevista: String,
        prazoProducaoSolicitadoDias: Int?,
        prioridadePrazo: PrioridadePrazo,
        aplicarAdicionalNoturno: Boolean
    ): Orcamento {
        val qtdSegura = max(1, quantidade)
        val modalidade = opcaoSelecionada?.modalidade ?: ModalidadeProducao.PROPRIA
        val qtdMinima = opcaoSelecionada?.quantidadeMinima ?: produto.quantidadeMinimaPropria
        val qtdBase = opcaoSelecionada?.quantidadeBase ?: produto.quantidadeBasePropria
        val precoPadrao = opcaoSelecionada?.precoBase ?: produto.precoBasePropria
        val precoEfetivo = precoPersonalizado ?: precoPadrao

        val faixasRelevantes = if (opcaoSelecionada?.ofertaFornecedorId != null) {
            faixas.filter { it.ofertaFornecedorId == opcaoSelecionada.ofertaFornecedorId }
        } else if (modalidade == ModalidadeProducao.PROPRIA) {
            faixas.filter { it.ofertaFornecedorId == null }
        } else {
            emptyList()
        }

        val modoEfetivo = when {
            produto.modoPrecificacao == ModoPrecificacao.AREA_M2 -> ModoPrecificacao.AREA_M2
            faixasRelevantes.isNotEmpty() && precoPersonalizado == null -> ModoPrecificacao.FAIXA_QUANTIDADE
            else -> ModoPrecificacao.UNITARIO_OU_LOTE
        }

        val (precoAplicado, descricaoFaixa, subtotalItens) = calcularValoresItem(
            modoPrecificacao = modoEfetivo,
            precoBase = precoEfetivo,
            quantidadeBase = qtdBase,
            faixas = faixasRelevantes,
            quantidade = qtdSegura,
            larguraMetros = larguraMetros,
            alturaMetros = alturaMetros
        )

        val areaM2 = if (modoEfetivo == ModoPrecificacao.AREA_M2) {
            max(0.0, larguraMetros) * max(0.0, alturaMetros)
        } else 0.0

        val diasNormal = opcaoSelecionada?.diasProducao ?: produto.diasProducaoPropria
        val diasEfetivo = prazoProducaoSolicitadoDias ?: when (prioridadePrazo) {
            PrioridadePrazo.EMERGENCIA -> 0
            PrioridadePrazo.URGENCIA -> max(1, diasNormal - 1)
            PrioridadePrazo.NORMAL -> diasNormal
        }

        val taxaUrgencia = taxas.firstOrNull { it.tipoTaxa == TipoTaxa.URGENCIA }
        val taxaEmergencia = taxas.firstOrNull { it.tipoTaxa == TipoTaxa.EMERGENCIA }

        val prioridadeResolvida = when {
            prioridadePrazo != PrioridadePrazo.NORMAL -> prioridadePrazo
            prazoProducaoSolicitadoDias != null && prazoProducaoSolicitadoDias < diasNormal -> {
                val limiteEmergencia = taxaEmergencia?.prazoLimiteDias ?: 0
                if (prazoProducaoSolicitadoDias <= limiteEmergencia) PrioridadePrazo.EMERGENCIA
                else PrioridadePrazo.URGENCIA
            }
            else -> PrioridadePrazo.NORMAL
        }

        val (rotuloUrgencia, valorUrgencia) = when (prioridadeResolvida) {
            PrioridadePrazo.URGENCIA -> resolverRotuloEValor(
                taxa = taxaUrgencia,
                subtotal = subtotalItens,
                rotuloPadrao = "Taxa de Urgência (20%)",
                percentualPadrao = 0.20
            )
            PrioridadePrazo.EMERGENCIA -> resolverRotuloEValor(
                taxa = taxaEmergencia,
                subtotal = subtotalItens,
                rotuloPadrao = "Taxa de Emergência (40%)",
                percentualPadrao = 0.40
            )
            PrioridadePrazo.NORMAL -> null to 0.0
        }

        val taxaEntrega = taxas.firstOrNull { it.tipoTaxa == TipoTaxa.ENTREGA }
        val valorEntrega = if (metodoEntrega == MetodoEntrega.ENTREGA) {
            if (taxaEntrega != null) calcularValorTaxa(taxaEntrega, subtotalItens) else 15.0
        } else 0.0

        val taxaNoturna = taxas.firstOrNull { it.tipoTaxa == TipoTaxa.ADICIONAL_NOTURNO }
        val valorNoturno = if (aplicarAdicionalNoturno) {
            if (taxaNoturna != null) calcularValorTaxa(taxaNoturna, subtotalItens) else 15.0
        } else 0.0

        val totalTaxas = valorUrgencia + valorEntrega + valorNoturno

        return Orcamento(
            nomeProduto = produto.nome,
            materialEspecificacao = produto.materialOuPapel,
            gramaturaEspecificacao = produto.gramatura,
            formatoEspecificacao = produto.formatoOuTamanho,
            acabamentoEspecificacao = acabamentoSelecionado.ifBlank {
                opcaoSelecionada?.acabamento ?: produto.acabamentoPadrao
            },
            facesEspecificacao = facesSelecionadas.ifBlank { produto.opcoesFaces },
            modalidadeProducao = modalidade,
            nomeFornecedor = if (modalidade == ModalidadeProducao.TERCEIRIZADA) opcaoSelecionada?.titulo else null,
            observacaoDivergenciaMaterial = opcaoSelecionada?.observacaoDivergenciaMaterial.orEmpty(),
            modoPrecificacao = modoEfetivo,
            quantidadeSolicitada = qtdSegura,
            quantidadeEfetiva = qtdSegura,
            quantidadeMinimaExigida = qtdMinima,
            atendeQuantidadeMinima = qtdSegura >= qtdMinima,
            larguraMetros = larguraMetros,
            alturaMetros = alturaMetros,
            areaMetrosQuadrados = areaM2,
            precoUnitarioAplicado = precoAplicado,
            descricaoFaixaAplicada = descricaoFaixa,
            subtotalItens = subtotalItens,
            diasProducaoNormal = diasNormal,
            diasProducaoEfetivo = diasEfetivo,
            dataEntregaPrevista = dataEntregaPrevista,
            rotuloTaxaUrgencia = rotuloUrgencia,
            valorTaxaUrgencia = valorUrgencia,
            valorTaxaEntrega = valorEntrega,
            valorAdicionalNoturno = valorNoturno,
            totalTaxasAdicionais = totalTaxas,
            valorTotalFinal = subtotalItens + totalTaxas
        )
    }

    private fun calcularValoresItem(
        modoPrecificacao: ModoPrecificacao,
        precoBase: Double,
        quantidadeBase: Int,
        faixas: List<FaixaPreco>,
        quantidade: Int,
        larguraMetros: Double,
        alturaMetros: Double
    ): Triple<Double, String, Double> {
        val qtdSegura = max(1, quantidade)
        val qtdBaseSegura = max(1, quantidadeBase)

        return when (modoPrecificacao) {
            ModoPrecificacao.AREA_M2 -> {
                val area = max(0.0, larguraMetros) * max(0.0, alturaMetros)
                val subtotal = area * precoBase * qtdSegura
                val descricao = "${formatarDecimal2(area)} m² × Qtd $qtdSegura"
                Triple(precoBase, descricao, subtotal)
            }

            ModoPrecificacao.FAIXA_QUANTIDADE -> {
                val faixaCorrespondente = faixas.firstOrNull { faixa ->
                    qtdSegura >= faixa.quantidadeMinima &&
                        (faixa.quantidadeMaxima == null || qtdSegura <= faixa.quantidadeMaxima)
                } ?: faixas.lastOrNull()

                if (faixaCorrespondente != null) {
                    val rotuloFaixa = if (faixaCorrespondente.quantidadeMaxima != null) {
                        "Faixa ${faixaCorrespondente.quantidadeMinima} a ${faixaCorrespondente.quantidadeMaxima} un"
                    } else {
                        "Faixa acima de ${faixaCorrespondente.quantidadeMinima - 1} un"
                    }
                    val subtotal = if (faixaCorrespondente.precoPorLoteFechado) {
                        faixaCorrespondente.precoUnitarioOuFaixa
                    } else {
                        qtdSegura * faixaCorrespondente.precoUnitarioOuFaixa
                    }
                    Triple(faixaCorrespondente.precoUnitarioOuFaixa, rotuloFaixa, subtotal)
                } else {
                    calcularUnitarioOuLote(precoBase, qtdBaseSegura, qtdSegura)
                }
            }

            ModoPrecificacao.UNITARIO_OU_LOTE -> {
                calcularUnitarioOuLote(precoBase, qtdBaseSegura, qtdSegura)
            }
        }
    }

    private fun calcularUnitarioOuLote(
        precoBase: Double,
        qtdBaseSegura: Int,
        qtdSegura: Int
    ): Triple<Double, String, Double> {
        val subtotal = if (qtdBaseSegura > 1) {
            (qtdSegura.toDouble() / qtdBaseSegura.toDouble()) * precoBase
        } else {
            qtdSegura * precoBase
        }
        val descricao = if (qtdBaseSegura > 1) {
            "Lote base $qtdBaseSegura un"
        } else {
            "Unitário (Qtd base 1)"
        }
        return Triple(precoBase, descricao, subtotal)
    }

    private fun resolverRotuloEValor(
        taxa: TaxaAdicional?,
        subtotal: Double,
        rotuloPadrao: String,
        percentualPadrao: Double
    ): Pair<String, Double> {
        return if (taxa != null) {
            val valor = calcularValorTaxa(taxa, subtotal)
            val sufixo = if (taxa.emPercentual) " (${taxa.valor.toInt()}%)" else ""
            "${taxa.nome}$sufixo" to valor
        } else {
            rotuloPadrao to (subtotal * percentualPadrao)
        }
    }

    private fun calcularValorTaxa(taxa: TaxaAdicional, subtotal: Double): Double {
        return if (taxa.emPercentual) {
            subtotal * (taxa.valor / 100.0)
        } else {
            taxa.valor
        }
    }

    private fun formatarDecimal2(valor: Double): String {
        val arredondado = round(valor * 100.0).toLong()
        val inteiro = arredondado / 100
        val decimal = abs(arredondado % 100).toString().padStart(2, '0')
        return "$inteiro,$decimal"
    }
}
