package br.com.grafica.calculadora.dados.local

import androidx.room.TypeConverter
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.MetodoEntrega
import br.com.grafica.calculadora.dominio.modelo.ModalidadeProducao
import br.com.grafica.calculadora.dominio.modelo.ModoPrecificacao
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.dominio.modelo.TipoTaxa

class ConversoresRoom {
    @TypeConverter
    fun deModalidadeProducao(valor: ModalidadeProducao): String = valor.name

    @TypeConverter
    fun paraModalidadeProducao(valor: String): ModalidadeProducao =
        runCatching { ModalidadeProducao.valueOf(valor) }.getOrDefault(ModalidadeProducao.PROPRIA)

    @TypeConverter
    fun deModoPrecificacao(valor: ModoPrecificacao): String = valor.name

    @TypeConverter
    fun paraModoPrecificacao(valor: String): ModoPrecificacao =
        runCatching { ModoPrecificacao.valueOf(valor) }.getOrDefault(ModoPrecificacao.UNITARIO_OU_LOTE)

    @TypeConverter
    fun deTipoTaxa(valor: TipoTaxa): String = valor.name

    @TypeConverter
    fun paraTipoTaxa(valor: String): TipoTaxa =
        runCatching { TipoTaxa.valueOf(valor) }.getOrDefault(TipoTaxa.PERSONALIZADA)

    @TypeConverter
    fun deEtapaPipeline(valor: EtapaPipeline): String = valor.name

    @TypeConverter
    fun paraEtapaPipeline(valor: String): EtapaPipeline =
        runCatching { EtapaPipeline.valueOf(valor) }.getOrDefault(EtapaPipeline.ORCAMENTO)

    @TypeConverter
    fun deStatusPagamento(valor: StatusPagamento): String = valor.name

    @TypeConverter
    fun paraStatusPagamento(valor: String): StatusPagamento =
        runCatching { StatusPagamento.valueOf(valor) }.getOrDefault(StatusPagamento.A_COBRAR)

    @TypeConverter
    fun deMetodoEntrega(valor: MetodoEntrega): String = valor.name

    @TypeConverter
    fun paraMetodoEntrega(valor: String): MetodoEntrega =
        runCatching { MetodoEntrega.valueOf(valor) }.getOrDefault(MetodoEntrega.RETIRADA)
}
