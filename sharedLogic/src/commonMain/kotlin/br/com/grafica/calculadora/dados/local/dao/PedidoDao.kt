package br.com.grafica.calculadora.dados.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.grafica.calculadora.dados.local.entidade.PedidoEntidade
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    fun observarTodos(): Flow<List<PedidoEntidade>>

    @Query("SELECT COUNT(*) FROM pedidos")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(pedido: PedidoEntidade): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirVarios(pedidos: List<PedidoEntidade>)

    @Update
    suspend fun atualizar(pedido: PedidoEntidade)

    @Query("UPDATE pedidos SET etapa_pipeline = :etapa WHERE id = :pedidoId")
    suspend fun atualizarEtapa(pedidoId: Long, etapa: EtapaPipeline)

    @Query("UPDATE pedidos SET status_pagamento = :statusPagamento WHERE id = :pedidoId")
    suspend fun atualizarStatusPagamento(pedidoId: Long, statusPagamento: StatusPagamento)

    @Delete
    suspend fun excluir(pedido: PedidoEntidade)
}
