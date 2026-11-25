package br.com.grafica.calculadora.dados.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.grafica.calculadora.dados.local.entidade.ClienteEntidade
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query(
        """
        SELECT * FROM clientes 
        WHERE nome LIKE '%' || :consulta || '%' 
           OR documento LIKE '%' || :consulta || '%' 
        ORDER BY nome ASC 
        LIMIT 10
        """
    )
    suspend fun buscarPorNomeOuDocumento(consulta: String): List<ClienteEntidade>

    @Query("SELECT * FROM clientes ORDER BY nome ASC")
    fun observarTodos(): Flow<List<ClienteEntidade>>

    @Query("SELECT * FROM clientes WHERE LOWER(nome) = LOWER(:nome) AND documento = :documento LIMIT 1")
    suspend fun encontrarPorNomeEDocumento(nome: String, documento: String): ClienteEntidade?

    @Query("SELECT * FROM clientes WHERE LOWER(nome) = LOWER(:nome) LIMIT 1")
    suspend fun encontrarPorNomeExato(nome: String): ClienteEntidade?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(cliente: ClienteEntidade): Long

    @Update
    suspend fun atualizar(cliente: ClienteEntidade)

    @Delete
    suspend fun excluir(cliente: ClienteEntidade)
}
