package br.com.grafica.calculadora.dominio.modelo

import kotlinx.serialization.Serializable

@Serializable
enum class ModalidadeProducao(val rotulo: String) {
    PROPRIA("Produção Própria"),
    TERCEIRIZADA("Terceirizada")
}

@Serializable
enum class ModoPrecificacao(val rotulo: String) {
    FAIXA_QUANTIDADE("Faixas de Quantidade"),
    UNITARIO_OU_LOTE("Unitário / Lote Fixo"),
    AREA_M2("Por Área (m²)")
}

@Serializable
enum class TipoTaxa(val rotulo: String) {
    URGENCIA("Taxa de Urgência"),
    EMERGENCIA("Taxa de Emergência"),
    ENTREGA("Taxa de Entrega"),
    ADICIONAL_NOTURNO("Adicional Noturno"),
    PERSONALIZADA("Taxa Adicional")
}

@Serializable
enum class EtapaPipeline(val numeroEtapa: Int, val rotulo: String) {
    ORCAMENTO(1, "Orçamento"),
    PRODUCAO(3, "Produção"),
    PRONTO(4, "Pronto");

    fun proximaEtapa(): EtapaPipeline? = when (this) {
        ORCAMENTO -> PRODUCAO
        PRODUCAO -> PRONTO
        PRONTO -> null
    }

    fun rotuloAcaoAvancar(): String = when (this) {
        ORCAMENTO -> "Mover para Produção ->"
        PRODUCAO -> "Mover para Pronto ->"
        PRONTO -> "Finalizado ✓"
    }
}

@Serializable
enum class StatusPagamento(val rotulo: String, val ordemClassificacao: Int) {
    A_COBRAR("A cobrar", 1),
    MEIO_A_MEIO("50% / 50%", 2),
    PAGO_100("100% pago", 3);

    fun proximo(): StatusPagamento = when (this) {
        A_COBRAR -> MEIO_A_MEIO
        MEIO_A_MEIO -> PAGO_100
        PAGO_100 -> A_COBRAR
    }
}

@Serializable
enum class MetodoEntrega(val rotulo: String) {
    RETIRADA("Retirada"),
    ENTREGA("Entrega")
}

@Serializable
enum class PrioridadePrazo(val rotulo: String) {
    NORMAL("Prazo Normal"),
    URGENCIA("Urgência"),
    EMERGENCIA("Emergência")
}
