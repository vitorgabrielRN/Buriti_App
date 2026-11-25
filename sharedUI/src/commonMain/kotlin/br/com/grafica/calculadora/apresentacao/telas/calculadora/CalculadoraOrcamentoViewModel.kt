package br.com.grafica.calculadora.apresentacao.telas.calculadora

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.MetodoEntrega
import br.com.grafica.calculadora.dominio.modelo.ModalidadeProducao
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.modelo.PrioridadePrazo
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.dominio.repositorio.ClienteRepositorio
import br.com.grafica.calculadora.dominio.repositorio.FornecedorRepositorio
import br.com.grafica.calculadora.dominio.repositorio.PedidoRepositorio
import br.com.grafica.calculadora.dominio.repositorio.ProdutoRepositorio
import br.com.grafica.calculadora.dominio.repositorio.TaxaRepositorio
import br.com.grafica.calculadora.nucleo.utilitarios.dataHojeMaisDias
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculadoraOrcamentoViewModel(
    private val clienteRepositorio: ClienteRepositorio,
    private val produtoRepositorio: ProdutoRepositorio,
    private val fornecedorRepositorio: FornecedorRepositorio,
    private val taxaRepositorio: TaxaRepositorio,
    private val pedidoRepositorio: PedidoRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(CalculadoraEstadoUi())
    val estado: StateFlow<CalculadoraEstadoUi> = _estado.asStateFlow()

    private var jobBuscaCliente: Job? = null

    init {
        observarCatalogoETaxas()
    }

    private fun observarCatalogoETaxas() {
        viewModelScope.launch {
            combine(
                produtoRepositorio.observarTodosProdutos(),
                fornecedorRepositorio.observarTodasOfertas(),
                produtoRepositorio.observarTodasFaixasPreco(),
                taxaRepositorio.observarTodasTaxas()
            ) { produtos, ofertas, faixas, taxas ->
                CatalogoCarregado(produtos, ofertas, faixas, taxas)
            }.collect { catalogo ->
                // update {} é atômico: não sobrescreve o que o usuário digitou nesse meio tempo
                _estado.update { atual ->
                    val produtoAtual = atual.produtoSelecionado
                        ?.let { sel -> catalogo.produtos.find { it.id == sel.id } }
                    recalcular(
                        atual.copy(
                            produtos = catalogo.produtos,
                            ofertasFornecedores = catalogo.ofertas,
                            faixasPreco = catalogo.faixas,
                            taxasAdicionais = catalogo.taxas,
                            produtoSelecionado = produtoAtual
                        )
                    )
                }
            }
        }
    }

    fun aoReceberEvento(evento: CalculadoraEventoUi) {
        when (evento) {
            // ---------- 1. Cliente ----------
            is CalculadoraEventoUi.AlterarNomeCliente -> {
                _estado.update { it.copy(nomeCliente = evento.nome, clienteIdSelecionado = null, mensagemErro = null) }
                buscarClientesAutocomplete(evento.nome)
            }

            is CalculadoraEventoUi.AlterarDocumentoCliente -> {
                _estado.update { it.copy(documentoCliente = evento.documento, clienteIdSelecionado = null) }
                buscarClientesAutocomplete(evento.documento)
            }

            is CalculadoraEventoUi.AlterarTelefoneCliente ->
                _estado.update { it.copy(telefoneCliente = evento.telefone) }

            is CalculadoraEventoUi.SelecionarSugestaoCliente -> _estado.update {
                it.copy(
                    clienteIdSelecionado = evento.cliente.id,
                    nomeCliente = evento.cliente.nome,
                    documentoCliente = evento.cliente.documento,
                    telefoneCliente = evento.cliente.telefone,
                    exibirSugestoesClientes = false,
                    sugestoesClientes = emptyList()
                )
            }

            CalculadoraEventoUi.FecharSugestoesClientes ->
                _estado.update { it.copy(exibirSugestoesClientes = false) }

            // ---------- 2. Produto ----------
            is CalculadoraEventoUi.AlterarBuscaProduto -> _estado.update {
                it.copy(consultaProduto = evento.consulta, exibirSugestoesProdutos = true, mensagemErro = null)
            }

            CalculadoraEventoUi.AlternarListaProdutos -> _estado.update {
                it.copy(exibirSugestoesProdutos = !it.exibirSugestoesProdutos)
            }

            is CalculadoraEventoUi.SelecionarProduto -> _estado.update { atual ->
                recalcular(aplicarProduto(atual, evento.produto))
            }

            // ---------- 3. Quantidade e impressão ----------
            is CalculadoraEventoUi.AlterarQuantidade -> {
                val limpa = evento.quantidade.filter { it.isDigit() }.take(7)
                _estado.update { recalcular(it.copy(quantidadeTexto = limpa)) }
            }

            is CalculadoraEventoUi.IncrementarQuantidade -> _estado.update {
                val atual = it.quantidadeTexto.toIntOrNull() ?: 0
                val nova = (atual + evento.passo).coerceAtLeast(1)
                recalcular(it.copy(quantidadeTexto = nova.toString()))
            }

            is CalculadoraEventoUi.SelecionarQuantidadeRapida ->
                _estado.update { recalcular(it.copy(quantidadeTexto = evento.quantidade.toString())) }

            is CalculadoraEventoUi.AlterarLarguraMetros ->
                _estado.update { recalcular(it.copy(larguraMetrosTexto = evento.largura.replace(',', '.'))) }

            is CalculadoraEventoUi.AlterarAlturaMetros ->
                _estado.update { recalcular(it.copy(alturaMetrosTexto = evento.altura.replace(',', '.'))) }

            is CalculadoraEventoUi.AlterarPrecoPersonalizado ->
                _estado.update { recalcular(it.copy(precoPersonalizadoTexto = evento.preco.replace(',', '.'))) }

            is CalculadoraEventoUi.AlternarColorido ->
                _estado.update { recalcular(it.copy(impressaoColorida = evento.colorido)) }

            is CalculadoraEventoUi.AlternarFrenteEVerso ->
                _estado.update { recalcular(it.copy(frenteEVerso = evento.frenteEVerso)) }

            // ---------- 4. Prazos / entrega / pagamento ----------
            is CalculadoraEventoUi.AlterarPrioridadePrazo -> _estado.update {
                recalcular(
                    it.copy(
                        prioridadePrazo = evento.prioridade,
                        prazoProducaoSolicitadoTexto = "",
                        dataEntregaEditadaManualmente = false
                    )
                )
            }

            is CalculadoraEventoUi.AlterarPrazoProducaoSolicitado -> {
                val limpo = evento.dias.filter { it.isDigit() }.take(3)
                _estado.update {
                    recalcular(it.copy(prazoProducaoSolicitadoTexto = limpo, dataEntregaEditadaManualmente = false))
                }
            }

            is CalculadoraEventoUi.AlterarDataEntrega -> _estado.update {
                recalcular(it.copy(dataEntregaPrevista = evento.data, dataEntregaEditadaManualmente = true))
            }

            is CalculadoraEventoUi.AlterarMetodoEntrega ->
                _estado.update { recalcular(it.copy(metodoEntrega = evento.metodo)) }

            is CalculadoraEventoUi.AlternarAdicionalNoturno ->
                _estado.update { recalcular(it.copy(aplicarAdicionalNoturno = evento.ativo)) }

            is CalculadoraEventoUi.AlterarStatusPagamento ->
                _estado.update { it.copy(statusPagamento = evento.status) }

            // ---------- 5. Comparativo ----------
            is CalculadoraEventoUi.SelecionarOpcaoComparativo -> _estado.update { atual ->
                val qtdAtual = atual.quantidadeTexto.toIntOrNull() ?: 1
                val qtdAjustada = maxOf(qtdAtual, evento.opcao.quantidadeMinima)
                recalcular(
                    atual.copy(
                        chaveOpcaoSelecionada = evento.opcao.chaveOpcao,
                        quantidadeTexto = qtdAjustada.toString(),
                        precoPersonalizadoTexto = "",
                        dataEntregaEditadaManualmente = false
                    )
                )
            }

            // ---------- Fluxo de confirmação ----------
            CalculadoraEventoUi.SolicitarConfirmacao -> solicitarConfirmacao()
            CalculadoraEventoUi.VoltarParaEdicao -> _estado.update { it.copy(exibirConfirmacao = false) }
            CalculadoraEventoUi.ConfirmarOrcamento -> salvarOrcamento()

            CalculadoraEventoUi.LimparFormulario -> limparFormulario()
            CalculadoraEventoUi.LimparMensagens ->
                _estado.update { it.copy(mensagemSucesso = null, mensagemErro = null) }

            is CalculadoraEventoUi.CarregarPedidoParaEdicao -> carregarPedido(evento.pedido)
        }
    }

    // =====================================================================

    private fun aplicarProduto(atual: CalculadoraEstadoUi, produto: Produto): CalculadoraEstadoUi {
        val chavePadrao = if (produto.aceitaProducaoPropria) {
            "PROPRIA"
        } else {
            atual.ofertasFornecedores.firstOrNull { it.produtoId == produto.id }
                ?.let { "FORNECEDOR_${it.id}" } ?: "PROPRIA"
        }
        return atual.copy(
            produtoSelecionado = produto,
            consultaProduto = produto.nome,
            exibirSugestoesProdutos = false,
            chaveOpcaoSelecionada = chavePadrao,
            quantidadeTexto = produto.quantidadeMinimaPropria.coerceAtLeast(1).toString(),
            precoPersonalizadoTexto = "",
            impressaoColorida = !textoIndicaPretoEBranco(produto.acabamentoPadrao),
            frenteEVerso = produto.opcoesFaces.contains("verso", ignoreCase = true) &&
                !produto.opcoesFaces.contains("/", ignoreCase = true),
            prazoProducaoSolicitadoTexto = "",
            dataEntregaEditadaManualmente = false,
            mensagemErro = null
        )
    }

    private fun textoIndicaPretoEBranco(texto: String): Boolean =
        texto.contains("preto", ignoreCase = true) || texto.contains("P&B", ignoreCase = true)

    private fun buscarClientesAutocomplete(consulta: String) {
        jobBuscaCliente?.cancel()
        if (consulta.isBlank()) {
            _estado.update { it.copy(sugestoesClientes = emptyList(), exibirSugestoesClientes = false) }
            return
        }
        jobBuscaCliente = viewModelScope.launch {
            delay(150)
            val encontrados = clienteRepositorio.buscarClientesPorNomeOuDocumento(consulta.trim())
            _estado.update {
                it.copy(sugestoesClientes = encontrados, exibirSugestoesClientes = encontrados.isNotEmpty())
            }
        }
    }

    private fun carregarPedido(pedido: Pedido) {
        _estado.update { atual ->
            val produtoEncontrado = atual.produtos.find { it.id == pedido.produtoId }
                ?: atual.produtos.find { it.nome.equals(pedido.nomeProduto, ignoreCase = true) }

            val chaveOpcao = if (pedido.modalidadeProducao == ModalidadeProducao.TERCEIRIZADA) {
                atual.ofertasFornecedores.find {
                    it.produtoId == produtoEncontrado?.id &&
                        (it.fornecedorId == pedido.fornecedorId || it.nomeFornecedor == pedido.nomeFornecedor)
                }?.let { "FORNECEDOR_${it.id}" } ?: "PROPRIA"
            } else "PROPRIA"

            recalcular(
                atual.copy(
                    pedidoIdEmEdicao = pedido.id,
                    etapaPipeline = pedido.etapaPipeline,
                    clienteIdSelecionado = pedido.clienteId,
                    nomeCliente = pedido.nomeCliente,
                    documentoCliente = pedido.documentoCliente,
                    telefoneCliente = pedido.telefoneCliente,
                    produtoSelecionado = produtoEncontrado,
                    consultaProduto = produtoEncontrado?.nome ?: pedido.nomeProduto,
                    exibirSugestoesProdutos = false,
                    chaveOpcaoSelecionada = chaveOpcao,
                    quantidadeTexto = pedido.quantidade.toString(),
                    larguraMetrosTexto = if (pedido.larguraMetros > 0) pedido.larguraMetros.toString() else "1.0",
                    alturaMetrosTexto = if (pedido.alturaMetros > 0) pedido.alturaMetros.toString() else "1.0",
                    precoPersonalizadoTexto = pedido.precoUnitarioAplicado.toString(),
                    impressaoColorida = !textoIndicaPretoEBranco(pedido.especificacaoAcabamento),
                    frenteEVerso = pedido.especificacaoFaces.contains("verso", ignoreCase = true),
                    metodoEntrega = pedido.metodoEntrega,
                    dataEntregaPrevista = pedido.dataPrazoEntrega,
                    dataEntregaEditadaManualmente = true,
                    prazoProducaoSolicitadoTexto = pedido.diasPrazoProducao.toString(),
                    prioridadePrazo = when {
                        pedido.nomeTaxaUrgenciaAplicada?.contains("Emergência", ignoreCase = true) == true -> PrioridadePrazo.EMERGENCIA
                        pedido.valorTaxaUrgenciaAplicada > 0.0 -> PrioridadePrazo.URGENCIA
                        else -> PrioridadePrazo.NORMAL
                    },
                    aplicarAdicionalNoturno = pedido.valorAdicionalNoturnoAplicado > 0.0,
                    statusPagamento = pedido.statusPagamento,
                    exibirConfirmacao = false,
                    mensagemErro = null,
                    mensagemSucesso = null
                )
            )
        }
    }

    private fun limparFormulario() {
        _estado.update { atual ->
            CalculadoraEstadoUi(
                produtos = atual.produtos,
                ofertasFornecedores = atual.ofertasFornecedores,
                faixasPreco = atual.faixasPreco,
                taxasAdicionais = atual.taxasAdicionais,
                mensagemSucesso = atual.mensagemSucesso
            )
        }
    }

    /** Valida o formulário e abre o diálogo "Deseja confirmar este orçamento?". */
    private fun solicitarConfirmacao() {
        val erro = validar(_estado.value)
        _estado.update {
            if (erro != null) it.copy(mensagemErro = erro, exibirConfirmacao = false)
            else it.copy(mensagemErro = null, exibirConfirmacao = true)
        }
    }

    private fun validar(s: CalculadoraEstadoUi): String? {
        val orcamento = s.orcamentoCalculado
        return when {
            s.nomeCliente.isBlank() -> "Informe o nome do cliente."
            s.produtoSelecionado == null || orcamento == null -> "Selecione um produto na lista."
            (s.quantidadeTexto.toIntOrNull() ?: 0) < 1 -> "Informe uma quantidade válida."
            !orcamento.atendeQuantidadeMinima ->
                "A quantidade mínima para esta opção é de ${orcamento.quantidadeMinimaExigida} unidades."
            orcamento.valorTotalFinal <= 0.0 -> "O valor total está zerado. Verifique o preço do produto."
            else -> null
        }
    }

    private fun salvarOrcamento() {
        val s = _estado.value
        if (s.salvando) return
        val erro = validar(s)
        val orcamento = s.orcamentoCalculado
        if (erro != null || orcamento == null) {
            _estado.update { it.copy(mensagemErro = erro, exibirConfirmacao = false) }
            return
        }

        val opcaoSelecionada = s.opcoesComparativo.find { it.chaveOpcao == s.chaveOpcaoSelecionada }
        val pedido = Pedido(
            id = s.pedidoIdEmEdicao ?: 0L,
            clienteId = s.clienteIdSelecionado,
            nomeCliente = s.nomeCliente.trim(),
            documentoCliente = s.documentoCliente.trim(),
            telefoneCliente = s.telefoneCliente.trim(),
            produtoId = s.produtoSelecionado?.id,
            nomeProduto = orcamento.nomeProduto,
            especificacaoMaterial = orcamento.materialEspecificacao,
            especificacaoGramatura = orcamento.gramaturaEspecificacao,
            especificacaoFormato = orcamento.formatoEspecificacao,
            especificacaoAcabamento = orcamento.acabamentoEspecificacao,
            especificacaoFaces = orcamento.facesEspecificacao,
            modalidadeProducao = orcamento.modalidadeProducao,
            fornecedorId = opcaoSelecionada?.fornecedorId,
            nomeFornecedor = orcamento.nomeFornecedor,
            observacaoDivergenciaMaterial = orcamento.observacaoDivergenciaMaterial,
            modoPrecificacao = orcamento.modoPrecificacao,
            quantidade = orcamento.quantidadeEfetiva,
            larguraMetros = orcamento.larguraMetros,
            alturaMetros = orcamento.alturaMetros,
            descricaoFaixaAplicada = orcamento.descricaoFaixaAplicada,
            precoUnitarioAplicado = orcamento.precoUnitarioAplicado,
            subtotalItens = orcamento.subtotalItens,
            diasPrazoProducao = orcamento.diasProducaoEfetivo,
            dataPrazoEntrega = orcamento.dataEntregaPrevista,
            metodoEntrega = s.metodoEntrega,
            nomeTaxaUrgenciaAplicada = orcamento.rotuloTaxaUrgencia,
            valorTaxaUrgenciaAplicada = orcamento.valorTaxaUrgencia,
            valorTaxaEntregaAplicada = orcamento.valorTaxaEntrega,
            valorAdicionalNoturnoAplicado = orcamento.valorAdicionalNoturno,
            valorTotalTaxas = orcamento.totalTaxasAdicionais,
            valorTotalFinal = orcamento.valorTotalFinal,
            // Novos orçamentos sempre entram no pipeline como ORCAMENTO
            etapaPipeline = if (s.pedidoIdEmEdicao != null) s.etapaPipeline else EtapaPipeline.ORCAMENTO,
            statusPagamento = s.statusPagamento
        )

        _estado.update { it.copy(salvando = true) }
        viewModelScope.launch {
            pedidoRepositorio.salvarPedidoComCliente(
                clienteIdSelecionado = s.clienteIdSelecionado,
                nomeCliente = s.nomeCliente,
                documentoCliente = s.documentoCliente,
                telefoneCliente = s.telefoneCliente,
                pedido = pedido
            ).onSuccess { idSalvo ->
                val codigo = "#${idSalvo.toString().padStart(4, '0')}"
                _estado.update { atual ->
                    CalculadoraEstadoUi(
                        produtos = atual.produtos,
                        ofertasFornecedores = atual.ofertasFornecedores,
                        faixasPreco = atual.faixasPreco,
                        taxasAdicionais = atual.taxasAdicionais,
                        mensagemSucesso = "Orçamento $codigo confirmado e salvo!"
                    )
                }
            }.onFailure { e ->
                _estado.update {
                    it.copy(salvando = false, exibirConfirmacao = false, mensagemErro = "Erro ao salvar: ${e.message}")
                }
            }
        }
    }

    /** Recalcula comparativo, orçamento e data de entrega (vinculada ao prazo de produção). */
    private fun recalcular(estado: CalculadoraEstadoUi): CalculadoraEstadoUi {
        val produto = estado.produtoSelecionado
            ?: return estado.copy(orcamentoCalculado = null, opcoesComparativo = emptyList())

        val qtd = estado.quantidadeTexto.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val largura = estado.larguraMetrosTexto.toDoubleOrNull()?.coerceAtLeast(0.1) ?: 1.0
        val altura = estado.alturaMetrosTexto.toDoubleOrNull()?.coerceAtLeast(0.1) ?: 1.0
        val precoCustom = estado.precoPersonalizadoTexto.toDoubleOrNull()?.takeIf { it > 0.0 }
        val diasSolicitados = estado.prazoProducaoSolicitadoTexto.toIntOrNull()

        val ofertasProduto = estado.ofertasFornecedores.filter { it.produtoId == produto.id }
        val faixasProduto = estado.faixasPreco.filter { it.produtoId == produto.id }

        val opcoes = produtoRepositorio.compararFornecedores(
            produto = produto,
            ofertas = ofertasProduto,
            faixas = faixasProduto,
            quantidade = qtd,
            larguraMetros = largura,
            alturaMetros = altura,
            precoPersonalizado = precoCustom
        )
        val opcaoSelecionada = opcoes.find { it.chaveOpcao == estado.chaveOpcaoSelecionada } ?: opcoes.firstOrNull()

        // Prazo de produção efetivo (mesma regra usada no repositório) -> data de entrega automática
        val diasNormal = opcaoSelecionada?.diasProducao ?: produto.diasProducaoPropria
        val diasEfetivo = diasSolicitados ?: when (estado.prioridadePrazo) {
            PrioridadePrazo.EMERGENCIA -> 0
            PrioridadePrazo.URGENCIA -> maxOf(1, diasNormal - 1)
            PrioridadePrazo.NORMAL -> diasNormal
        }
        val dataEntrega = if (estado.dataEntregaEditadaManualmente && estado.dataEntregaPrevista.isNotBlank()) {
            estado.dataEntregaPrevista
        } else {
            dataHojeMaisDias(diasEfetivo)
        }

        val orcamento = produtoRepositorio.gerarOrcamentoCompleto(
            produto = produto,
            opcaoSelecionada = opcaoSelecionada,
            faixas = faixasProduto,
            taxas = estado.taxasAdicionais,
            quantidade = qtd,
            larguraMetros = largura,
            alturaMetros = altura,
            precoPersonalizado = precoCustom,
            facesSelecionadas = estado.rotuloFaces,
            acabamentoSelecionado = estado.rotuloAcabamento,
            metodoEntrega = estado.metodoEntrega,
            dataEntregaPrevista = dataEntrega,
            prazoProducaoSolicitadoDias = diasSolicitados,
            prioridadePrazo = estado.prioridadePrazo,
            aplicarAdicionalNoturno = estado.aplicarAdicionalNoturno
        )

        return estado.copy(
            opcoesComparativo = opcoes,
            chaveOpcaoSelecionada = opcaoSelecionada?.chaveOpcao ?: "PROPRIA",
            dataEntregaPrevista = dataEntrega,
            orcamentoCalculado = orcamento
        )
    }

    private class CatalogoCarregado(
        val produtos: List<Produto>,
        val ofertas: List<br.com.grafica.calculadora.dominio.modelo.OfertaFornecedor>,
        val faixas: List<br.com.grafica.calculadora.dominio.modelo.FaixaPreco>,
        val taxas: List<br.com.grafica.calculadora.dominio.modelo.TaxaAdicional>
    )
}
