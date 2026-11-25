package br.com.grafica.calculadora.dominio.modelo

data class Usuario(
    val id: Long = 0,
    val nome: String,
    val email: String,
    val senha: String = "",
    val mesVigente: String = "Mês Atual",
    val metaMensal: Double = 0.0,
    val modoEstudioAtivo: Boolean = true,
    val logado: Boolean = false
)
