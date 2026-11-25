package br.com.grafica.calculadora.dados.local.entidade

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fornecedores",
    indices = [Index(value = ["nome"])]
)
data class FornecedorEntidade(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "nome")
    val nome: String,
    @ColumnInfo(name = "contato_ou_observacoes")
    val contatoOuObservacoes: String = "",
    @ColumnInfo(name = "dias_producao_padrao")
    val diasProducaoPadrao: Int = 3
)

@Entity(
    tableName = "ofertas_fornecedor",
    foreignKeys = [
        ForeignKey(
            entity = ProdutoEntidade::class,
            parentColumns = ["id"],
            childColumns = ["produto_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FornecedorEntidade::class,
            parentColumns = ["id"],
            childColumns = ["fornecedor_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["produto_id"]),
        Index(value = ["fornecedor_id"])
    ]
)
data class OfertaFornecedorEntidade(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "produto_id")
    val produtoId: Long,
    @ColumnInfo(name = "fornecedor_id")
    val fornecedorId: Long,
    @ColumnInfo(name = "nome_fornecedor")
    val nomeFornecedor: String,
    @ColumnInfo(name = "preco_base")
    val precoBase: Double,
    @ColumnInfo(name = "quantidade_base")
    val quantidadeBase: Int = 1,
    @ColumnInfo(name = "quantidade_minima")
    val quantidadeMinima: Int = 1,
    @ColumnInfo(name = "dias_producao")
    val diasProducao: Int = 3,
    @ColumnInfo(name = "acabamento")
    val acabamento: String = "",
    @ColumnInfo(name = "observacao_divergencia_material")
    val observacaoDivergenciaMaterial: String = ""
)

@Entity(
    tableName = "faixas_preco",
    foreignKeys = [
        ForeignKey(
            entity = ProdutoEntidade::class,
            parentColumns = ["id"],
            childColumns = ["produto_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["produto_id"]),
        Index(value = ["oferta_fornecedor_id"])
    ]
)
data class FaixaPrecoEntidade(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "produto_id")
    val produtoId: Long,
    @ColumnInfo(name = "oferta_fornecedor_id")
    val ofertaFornecedorId: Long? = null,
    @ColumnInfo(name = "quantidade_minima")
    val quantidadeMinima: Int,
    @ColumnInfo(name = "quantidade_maxima")
    val quantidadeMaxima: Int? = null,
    @ColumnInfo(name = "preco_unitario_ou_faixa")
    val precoUnitarioOuFaixa: Double,
    @ColumnInfo(name = "preco_por_lote_fechado")
    val precoPorLoteFechado: Boolean = false
)
