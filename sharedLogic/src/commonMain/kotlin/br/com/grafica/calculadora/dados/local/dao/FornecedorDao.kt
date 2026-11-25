package br.com.grafica.calculadora.dados.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.grafica.calculadora.dados.local.entidade.FornecedorEntidade
import br.com.grafica.calculadora.dados.local.entidade.OfertaFornecedorEntidade
import kotlinx.coroutines.flow.Flow

@Dao
interface FornecedorDao {
    @Query("SELECT * FROM fornecedores ORDER BY id ASC")
    fun observarTodos(): Flow<List<FornecedorEntidade>>

    @Query("SELECT COUNT(*) FROM fornecedores")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(fornecedor: FornecedorEntidade): Long

    @Update
    suspend fun atualizar(fornecedor: FornecedorEntidade)

    @Delete
    suspend fun excluir(fornecedor: FornecedorEntidade)
}

@Dao
interface OfertaFornecedorDao {
    @Query("SELECT * FROM ofertas_fornecedor ORDER BY produto_id ASC, fornecedor_id ASC")
    fun observarTodas(): Flow<List<OfertaFornecedorEntidade>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(oferta: OfertaFornecedorEntidade): Long

    @Update
    suspend fun atualizar(oferta: OfertaFornecedorEntidade)

    @Delete
    suspend fun excluir(oferta: OfertaFornecedorEntidade)
}
