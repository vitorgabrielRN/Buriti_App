package br.com.grafica.calculadora.dados.local.entidade

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.grafica.calculadora.dominio.modelo.ModoPrecificacao

@Entity(
    tableName = "produtos",
    indices = [Index(value = ["nome"])]
)
data class ProdutoEntidade(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "nome")
    val nome: String,
    @ColumnInfo(name = "material_ou_papel")
    val materialOuPapel: String,
    @ColumnInfo(name = "gramatura")
    val gramatura: String,
    @ColumnInfo(name = "formato_ou_tamanho")
    val formatoOuTamanho: String,
    @ColumnInfo(name = "acabamento_padrao")
    val acabamentoPadrao: String,
    @ColumnInfo(name = "opcoes_faces")
    val opcoesFaces: String,
    @ColumnInfo(name = "modo_precificacao")
    val modoPrecificacao: ModoPrecificacao = ModoPrecificacao.UNITARIO_OU_LOTE,
    @ColumnInfo(name = "aceita_producao_propria")
    val aceitaProducaoPropria: Boolean = true,
    @ColumnInfo(name = "preco_base_propria")
    val precoBasePropria: Double = 0.0,
    @ColumnInfo(name = "quantidade_base_propria")
    val quantidadeBasePropria: Int = 1,
    @ColumnInfo(name = "quantidade_minima_propria")
    val quantidadeMinimaPropria: Int = 1,
    @ColumnInfo(name = "dias_producao_propria")
    val diasProducaoPropria: Int = 2,
    @ColumnInfo(name = "acabamento_propria")
    val acabamentoPropria: String = "",
    @ColumnInfo(name = "observacoes_propria")
    val observacoesPropria: String = ""
)
