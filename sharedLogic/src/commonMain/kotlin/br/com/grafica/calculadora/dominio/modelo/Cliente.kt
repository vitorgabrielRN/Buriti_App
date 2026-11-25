package br.com.grafica.calculadora.dominio.modelo

data class Cliente(
    val id: Long = 0,
    val nome: String,
    val documento: String,
    val telefone: String,
    val criadoEm: Long = 0L
)
