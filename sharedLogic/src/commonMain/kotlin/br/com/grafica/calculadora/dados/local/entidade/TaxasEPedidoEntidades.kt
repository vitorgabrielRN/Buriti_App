package br.com.grafica.calculadora.dados.local.entidade

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.MetodoEntrega
import br.com.grafica.calculadora.dominio.modelo.ModalidadeProducao
import br.com.grafica.calculadora.dominio.modelo.ModoPrecificacao
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.dominio.modelo.TipoTaxa

@Entity(tableName = "taxas_adicionais")
data class TaxaAdicionalEntidade(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "tipo_taxa")
    val tipoTaxa: TipoTaxa,
    @ColumnInfo(name = "nome")
    val nome: String,
    @ColumnInfo(name = "valor")
    val valor: Double,
    @ColumnInfo(name = "em_percentual")
    val emPercentual: Boolean = false,
    @ColumnInfo(name = "prazo_limite_dias")
    val prazoLimiteDias: Int? = null,
    @ColumnInfo(name = "aplicar_apos_hora")
    val aplicarAposHora: Int? = null,
    @ColumnInfo(name = "descricao")
    val descricao: String = ""
)

@Entity(
    tableName = "pedidos",
    indices = [
        Index(value = ["etapa_pipeline"]),
        Index(value = ["status_pagamento"]),
        Index(value = ["nome_cliente"])
    ]
)
data class PedidoEntidade(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "cliente_id")
    val clienteId: Long? = null,
    @ColumnInfo(name = "nome_cliente")
    val nomeCliente: String,
    @ColumnInfo(name = "documento_cliente")
    val documentoCliente: String = "",
    @ColumnInfo(name = "telefone_cliente")
    val telefoneCliente: String = "",
    @ColumnInfo(name = "produto_id")
    val produtoId: Long? = null,
    @ColumnInfo(name = "nome_produto")
    val nomeProduto: String,
    @ColumnInfo(name = "especificacao_material")
    val especificacaoMaterial: String,
    @ColumnInfo(name = "especificacao_gramatura")
    val especificacaoGramatura: String,
    @ColumnInfo(name = "especificacao_formato")
    val especificacaoFormato: String,
    @ColumnInfo(name = "especificacao_acabamento")
    val especificacaoAcabamento: String,
    @ColumnInfo(name = "especificacao_faces")
    val especificacaoFaces: String,
    @ColumnInfo(name = "modalidade_producao")
    val modalidadeProducao: ModalidadeProducao = ModalidadeProducao.PROPRIA,
    @ColumnInfo(name = "fornecedor_id")
    val fornecedorId: Long? = null,
    @ColumnInfo(name = "nome_fornecedor")
    val nomeFornecedor: String? = null,
    @ColumnInfo(name = "observacao_divergencia_material")
    val observacaoDivergenciaMaterial: String = "",
    @ColumnInfo(name = "modo_precificacao")
    val modoPrecificacao: ModoPrecificacao = ModoPrecificacao.UNITARIO_OU_LOTE,
    @ColumnInfo(name = "quantidade")
    val quantidade: Int = 1,
    @ColumnInfo(name = "largura_metros")
    val larguraMetros: Double = 0.0,
    @ColumnInfo(name = "altura_metros")
    val alturaMetros: Double = 0.0,
    @ColumnInfo(name = "descricao_faixa_aplicada")
    val descricaoFaixaAplicada: String = "",
    @ColumnInfo(name = "preco_unitario_aplicado")
    val precoUnitarioAplicado: Double = 0.0,
    @ColumnInfo(name = "subtotal_itens")
    val subtotalItens: Double = 0.0,
    @ColumnInfo(name = "dias_prazo_producao")
    val diasPrazoProducao: Int = 2,
    @ColumnInfo(name = "data_prazo_entrega")
    val dataPrazoEntrega: String = "20/11",
    @ColumnInfo(name = "metodo_entrega")
    val metodoEntrega: MetodoEntrega = MetodoEntrega.RETIRADA,
    @ColumnInfo(name = "nome_taxa_urgencia_aplicada")
    val nomeTaxaUrgenciaAplicada: String? = null,
    @ColumnInfo(name = "valor_taxa_urgencia_aplicada")
    val valorTaxaUrgenciaAplicada: Double = 0.0,
    @ColumnInfo(name = "valor_taxa_entrega_aplicada")
    val valorTaxaEntregaAplicada: Double = 0.0,
    @ColumnInfo(name = "valor_adicional_noturno_aplicado")
    val valorAdicionalNoturnoAplicado: Double = 0.0,
    @ColumnInfo(name = "valor_total_taxas")
    val valorTotalTaxas: Double = 0.0,
    @ColumnInfo(name = "valor_total_final")
    val valorTotalFinal: Double = 0.0,
    @ColumnInfo(name = "etapa_pipeline")
    val etapaPipeline: EtapaPipeline = EtapaPipeline.ORCAMENTO,
    @ColumnInfo(name = "status_pagamento")
    val statusPagamento: StatusPagamento = StatusPagamento.A_COBRAR,
    @ColumnInfo(name = "criado_em")
    val criadoEm: Long = 0L
)
