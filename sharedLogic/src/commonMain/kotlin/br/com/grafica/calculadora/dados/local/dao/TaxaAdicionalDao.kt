package br.com.grafica.calculadora.dados.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.grafica.calculadora.dados.local.entidade.TaxaAdicionalEntidade
import kotlinx.coroutines.flow.Flow

@Dao
interface TaxaAdicionalDao {
    @Query("SELECT * FROM taxas_adicionais ORDER BY id ASC")
    fun observarTodas(): Flow<List<TaxaAdicionalEntidade>>

    @Query("SELECT COUNT(*) FROM taxas_adicionais")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(taxa: TaxaAdicionalEntidade): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirVarias(taxas: List<TaxaAdicionalEntidade>)

    @Update
    suspend fun atualizar(taxa: TaxaAdicionalEntidade)

    @Delete
    suspend fun excluir(taxa: TaxaAdicionalEntidade)
}
