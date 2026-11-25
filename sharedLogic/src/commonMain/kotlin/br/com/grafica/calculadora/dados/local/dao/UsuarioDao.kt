package br.com.grafica.calculadora.dados.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.grafica.calculadora.dados.local.entidade.UsuarioEntidade
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE logado = 1 LIMIT 1")
    fun observarUsuarioLogado(): Flow<UsuarioEntidade?>

    @Query("SELECT * FROM usuarios WHERE logado = 1 LIMIT 1")
    suspend fun obterUsuarioLogado(): UsuarioEntidade?

    @Query("SELECT * FROM usuarios WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun buscarPorEmail(email: String): UsuarioEntidade?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(usuario: UsuarioEntidade): Long

    @Update
    suspend fun atualizar(usuario: UsuarioEntidade)

    @Query("UPDATE usuarios SET logado = 0")
    suspend fun deslogarTodos()

    @Query("UPDATE usuarios SET logado = 1 WHERE id = :usuarioId")
    suspend fun definirLogado(usuarioId: Long)
}
