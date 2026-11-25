package br.com.grafica.calculadora.dominio.modelo

data class Pedido(
    val id: Long = 0,
    val clienteId: Long? = null,
    val nomeCliente: String,
    val documentoCliente: String = "",
    val telefoneCliente: String = "",
    val produtoId: Long? = null,
    val nomeProduto: String,
    val especificacaoMaterial: String,
    val especificacaoGramatura: String,
    val especificacaoFormato: String,
    val especificacaoAcabamento: String,
    val especificacaoFaces: String,
    val modalidadeProducao: ModalidadeProducao = ModalidadeProducao.PROPRIA,
    val fornecedorId: Long? = null,
    val nomeFornecedor: String? = null,
    val observacaoDivergenciaMaterial: String = "",
    val modoPrecificacao: ModoPrecificacao = ModoPrecificacao.UNITARIO_OU_LOTE,
    val quantidade: Int = 1,
    val larguraMetros: Double = 0.0,
    val alturaMetros: Double = 0.0,
    val descricaoFaixaAplicada: String = "",
    val precoUnitarioAplicado: Double = 0.0,
    val subtotalItens: Double = 0.0,
    val diasPrazoProducao: Int = 2,
    val dataPrazoEntrega: String = "20/11",
    val metodoEntrega: MetodoEntrega = MetodoEntrega.RETIRADA,
    val nomeTaxaUrgenciaAplicada: String? = null,
    val valorTaxaUrgenciaAplicada: Double = 0.0,
    val valorTaxaEntregaAplicada: Double = 0.0,
    val valorAdicionalNoturnoAplicado: Double = 0.0,
    val valorTotalTaxas: Double = 0.0,
    val valorTotalFinal: Double = 0.0,
    val etapaPipeline: EtapaPipeline = EtapaPipeline.ORCAMENTO,
    val statusPagamento: StatusPagamento = StatusPagamento.A_COBRAR,
    val criadoEm: Long = 0L
) {
    val codigoFormatado: String
        get() = "#${id.toString().padStart(4, '0')}"
}