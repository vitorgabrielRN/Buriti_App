package br.com.grafica.calculadora.apresentacao.telas.calculadora

import br.com.grafica.calculadora.dominio.modelo.Cliente
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.FaixaPreco
import br.com.grafica.calculadora.dominio.modelo.MetodoEntrega
import br.com.grafica.calculadora.dominio.modelo.OfertaFornecedor
import br.com.grafica.calculadora.dominio.modelo.OpcaoComparativoFornecedor
import br.com.grafica.calculadora.dominio.modelo.Orcamento
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.modelo.PrioridadePrazo
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.dominio.modelo.TaxaAdicional

data class CalculadoraEstadoUi(
    // Catálogo
    val produtos: List<Produto> = emptyList(),
    val ofertasFornecedores: List<OfertaFornecedor> = emptyList(),
    val faixasPreco: List<FaixaPreco> = emptyList(),
    val taxasAdicionais: List<TaxaAdicional> = emptyList(),

    // Edição de pedido existente (etapa é preservada; novos orçamentos sempre iniciam em ORCAMENTO)
    val pedidoIdEmEdicao: Long? = null,
    val etapaPipeline: EtapaPipeline = EtapaPipeline.ORCAMENTO,

    // 1. Cliente
    val clienteIdSelecionado: Long? = null,
    val nomeCliente: String = "",
    val documentoCliente: String = "",
    val telefoneCliente: String = "",
    val sugestoesClientes: List<Cliente> = emptyList(),
    val exibirSugestoesClientes: Boolean = false,

    // 2. Produto (dropdown com autocomplete)
    val consultaProduto: String = "",
    val exibirSugestoesProdutos: Boolean = false,
    val produtoSelecionado: Produto? = null,

    // 3. Quantidade e impressão
    val quantidadeTexto: String = "1",
    val larguraMetrosTexto: String = "1.0",
    val alturaMetrosTexto: String = "1.0",
    val precoPersonalizadoTexto: String = "",
    val impressaoColorida: Boolean = true,
    val frenteEVerso: Boolean = false,

    // 4. Prazos, entrega e pagamento
    val prioridadePrazo: PrioridadePrazo = PrioridadePrazo.NORMAL,
    val prazoProducaoSolicitadoTexto: String = "",
    val dataEntregaPrevista: String = "",
    val dataEntregaEditadaManualmente: Boolean = false,
    val metodoEntrega: MetodoEntrega = MetodoEntrega.RETIRADA,
    val aplicarAdicionalNoturno: Boolean = false,
    val statusPagamento: StatusPagamento = StatusPagamento.A_COBRAR,

    // 5. Comparativo (onde produzir)
    val opcoesComparativo: List<OpcaoComparativoFornecedor> = emptyList(),
    val chaveOpcaoSelecionada: String = "PROPRIA",

    // Resultado / fluxo
    val orcamentoCalculado: Orcamento? = null,
    val exibirConfirmacao: Boolean = false,
    val salvando: Boolean = false,
    val mensagemSucesso: String? = null,
    val mensagemErro: String? = null
) {
    val rotuloAcabamento: String get() = if (impressaoColorida) "Colorido" else "Preto e branco"
    val rotuloFaces: String get() = if (frenteEVerso) "Frente e verso" else "Somente frente"
}

sealed interface CalculadoraEventoUi {
    // Cliente
    data class AlterarNomeCliente(val nome: String) : CalculadoraEventoUi
    data class AlterarDocumentoCliente(val documento: String) : CalculadoraEventoUi
    data class AlterarTelefoneCliente(val telefone: String) : CalculadoraEventoUi
    data class SelecionarSugestaoCliente(val cliente: Cliente) : CalculadoraEventoUi
    data object FecharSugestoesClientes : CalculadoraEventoUi

    // Produto
    data class AlterarBuscaProduto(val consulta: String) : CalculadoraEventoUi
    data class SelecionarProduto(val produto: Produto) : CalculadoraEventoUi
    data object AlternarListaProdutos : CalculadoraEventoUi

    // Quantidade e impressão
    data class AlterarQuantidade(val quantidade: String) : CalculadoraEventoUi
    data class IncrementarQuantidade(val passo: Int) : CalculadoraEventoUi
    data class SelecionarQuantidadeRapida(val quantidade: Int) : CalculadoraEventoUi
    data class AlterarLarguraMetros(val largura: String) : CalculadoraEventoUi
    data class AlterarAlturaMetros(val altura: String) : CalculadoraEventoUi
    data class AlterarPrecoPersonalizado(val preco: String) : CalculadoraEventoUi
    data class AlternarColorido(val colorido: Boolean) : CalculadoraEventoUi
    data class AlternarFrenteEVerso(val frenteEVerso: Boolean) : CalculadoraEventoUi

    // Prazos / entrega / pagamento
    data class AlterarPrioridadePrazo(val prioridade: PrioridadePrazo) : CalculadoraEventoUi
    data class AlterarPrazoProducaoSolicitado(val dias: String) : CalculadoraEventoUi
    data class AlterarDataEntrega(val data: String) : CalculadoraEventoUi
    data class AlterarMetodoEntrega(val metodo: MetodoEntrega) : CalculadoraEventoUi
    data class AlternarAdicionalNoturno(val ativo: Boolean) : CalculadoraEventoUi
    data class AlterarStatusPagamento(val status: StatusPagamento) : CalculadoraEventoUi

    // Comparativo
    data class SelecionarOpcaoComparativo(val opcao: OpcaoComparativoFornecedor) : CalculadoraEventoUi

    // Fluxo de confirmação
    data object SolicitarConfirmacao : CalculadoraEventoUi
    data object VoltarParaEdicao : CalculadoraEventoUi
    data object ConfirmarOrcamento : CalculadoraEventoUi

    data object LimparFormulario : CalculadoraEventoUi
    data object LimparMensagens : CalculadoraEventoUi
    data class CarregarPedidoParaEdicao(val pedido: Pedido) : CalculadoraEventoUi
}
