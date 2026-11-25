package br.com.grafica.calculadora.dados.local.entidade

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clientes",
    indices = [
        Index(value = ["nome"]),
        Index(value = ["documento"])
    ]
)
data class ClienteEntidade(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "nome")
    val nome: String,
    @ColumnInfo(name = "documento")
    val documento: String,
    @ColumnInfo(name = "telefone")
    val telefone: String,
    @ColumnInfo(name = "criado_em")
    val criadoEm: Long = 0L
)
