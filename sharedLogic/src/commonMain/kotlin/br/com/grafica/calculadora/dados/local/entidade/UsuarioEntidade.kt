package br.com.grafica.calculadora.dados.local.entidade

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class UsuarioEntidade(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "nome")
    val nome: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "senha")
    val senha: String,
    @ColumnInfo(name = "mes_vigente")
    val mesVigente: String = "Mês Atual",
    @ColumnInfo(name = "meta_mensal")
    val metaMensal: Double = 0.0,
    @ColumnInfo(name = "modo_estudio_ativo")
    val modoEstudioAtivo: Boolean = true,
    @ColumnInfo(name = "logado")
    val logado: Boolean = false
)
