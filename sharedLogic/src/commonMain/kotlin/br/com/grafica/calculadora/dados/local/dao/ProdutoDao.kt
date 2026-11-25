package br.com.grafica.calculadora.dados.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.grafica.calculadora.dados.local.entidade.FaixaPrecoEntidade
import br.com.grafica.calculadora.dados.local.entidade.ProdutoEntidade
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Query("SELECT * FROM produtos ORDER BY nome ASC")
    fun observarTodos(): Flow<List<ProdutoEntidade>>

    @Query("SELECT COUNT(*) FROM produtos")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(produto: ProdutoEntidade): Long

    @Update
    suspend fun atualizar(produto: ProdutoEntidade)

    @Delete
    suspend fun excluir(produto: ProdutoEntidade)
}

@Dao
interface FaixaPrecoDao {
    @Query("SELECT * FROM faixas_preco ORDER BY produto_id ASC, quantidade_minima ASC")
    fun observarTodas(): Flow<List<FaixaPrecoEntidade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(faixa: FaixaPrecoEntidade): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirVarias(faixas: List<FaixaPrecoEntidade>)

    @Update
    suspend fun atualizar(faixa: FaixaPrecoEntidade)

    @Delete
    suspend fun excluir(faixa: FaixaPrecoEntidade)
}
