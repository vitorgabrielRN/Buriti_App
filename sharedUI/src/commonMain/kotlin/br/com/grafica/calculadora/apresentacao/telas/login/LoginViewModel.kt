package br.com.grafica.calculadora.apresentacao.telas.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.grafica.calculadora.dominio.repositorio.TaxaRepositorio
import br.com.grafica.calculadora.dominio.repositorio.UsuarioRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val usuarioRepositorio: UsuarioRepositorio,
    private val taxaRepositorio: TaxaRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginEstadoUi())
    val estado: StateFlow<LoginEstadoUi> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            taxaRepositorio.garantirDadosIniciais()
        }
        observarSessao()
    }

    private fun observarSessao() {
        viewModelScope.launch {
            usuarioRepositorio.observarUsuarioLogado().collect { usuario ->
                _estado.update { it.copy(usuarioAutenticado = usuario != null) }
            }
        }
    }

    fun aoReceberEvento(evento: LoginEventoUi) {
        when (evento) {
            is LoginEventoUi.AlterarNome ->
                _estado.update { it.copy(nome = evento.nome, mensagemErro = null) }

            is LoginEventoUi.AlterarEmail ->
                _estado.update { it.copy(email = evento.email, mensagemErro = null) }

            is LoginEventoUi.AlterarSenha ->
                _estado.update { it.copy(senha = evento.senha, mensagemErro = null) }

            is LoginEventoUi.AlterarMesVigente ->
                _estado.update { it.copy(mesVigente = evento.mes, mensagemErro = null) }

            is LoginEventoUi.AlterarMetaMensal ->
                _estado.update { it.copy(metaMensalTexto = evento.meta, mensagemErro = null) }

            LoginEventoUi.AlternarModoCadastro ->
                _estado.update {
                    it.copy(
                        modoCadastro = !it.modoCadastro,
                        mensagemErro = null
                    )
                }

            LoginEventoUi.LimparErro ->
                _estado.update { it.copy(mensagemErro = null) }

            LoginEventoUi.Submeter -> submeter()
        }
    }

    private fun submeter() {
        val atual = _estado.value
        if (atual.carregando) return

        viewModelScope.launch {
            _estado.update { it.copy(carregando = true, mensagemErro = null) }

            val resultado = if (atual.modoCadastro) {
                val meta = atual.metaMensalTexto.replace(",", ".").toDoubleOrNull() ?: 0.0
                usuarioRepositorio.cadastrar(
                    nome = atual.nome,
                    email = atual.email,
                    senha = atual.senha,
                    metaMensal = meta,
                    mesVigente = atual.mesVigente.ifBlank { "Mês Atual" }
                )
            } else {
                usuarioRepositorio.entrar(
                    email = atual.email,
                    senha = atual.senha
                )
            }

            resultado.fold(
                onSuccess = {
                    _estado.update {
                        it.copy(
                            carregando = false,
                            usuarioAutenticado = true,
                            senha = "",
                            mensagemErro = null
                        )
                    }
                },
                onFailure = { erro ->
                    _estado.update {
                        it.copy(
                            carregando = false,
                            mensagemErro = erro.message ?: "Não foi possível autenticar."
                        )
                    }
                }
            )
        }
    }
}
