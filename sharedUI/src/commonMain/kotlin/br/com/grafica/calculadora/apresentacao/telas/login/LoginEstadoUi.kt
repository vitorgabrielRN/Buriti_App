package br.com.grafica.calculadora.apresentacao.telas.login

data class LoginEstadoUi(
    val modoCadastro: Boolean = false,
    val nome: String = "",
    val email: String = "",
    val senha: String = "",
    val mesVigente: String = "",
    val metaMensalTexto: String = "",
    val carregando: Boolean = false,
    val usuarioAutenticado: Boolean = false,
    val mensagemErro: String? = null
)

sealed interface LoginEventoUi {
    data class AlterarNome(val nome: String) : LoginEventoUi
    data class AlterarEmail(val email: String) : LoginEventoUi
    data class AlterarSenha(val senha: String) : LoginEventoUi
    data class AlterarMesVigente(val mes: String) : LoginEventoUi
    data class AlterarMetaMensal(val meta: String) : LoginEventoUi
    data object AlternarModoCadastro : LoginEventoUi
    data object Submeter : LoginEventoUi
    data object LimparErro : LoginEventoUi
}
