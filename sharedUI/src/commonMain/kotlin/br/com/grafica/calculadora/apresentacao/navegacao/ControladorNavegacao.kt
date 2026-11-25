package br.com.grafica.calculadora.apresentacao.navegacao

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Controlador centralizado de navegação da aplicação (multiplataforma).
 */
@Stable
class ControladorNavegacao(
    rotaInicial: RotaApp = RotaApp.Splash,
    abaInicial: AbaNavegacao = AbaNavegacao.INICIO
) {
    var rotaAtual by mutableStateOf(rotaInicial)
        private set

    var abaAtual by mutableStateOf(abaInicial)
        private set
    fun concluirSplash(estaAutenticado: Boolean) {
        rotaAtual = if (estaAutenticado) RotaApp.Principal else RotaApp.Login
    }

    fun irParaPrincipal() {
        rotaAtual = RotaApp.Principal
    }

    fun irParaLogin() {
        abaAtual = AbaNavegacao.INICIO
        rotaAtual = RotaApp.Login
    }

    fun navegarParaAba(destino: AbaNavegacao) {
        if (abaAtual == destino) return
        abaAtual = destino
    }

    fun abrirCalculadora() {
        navegarParaAba(AbaNavegacao.ORCAMENTO)
    }

    fun abrirQuadro() {
        navegarParaAba(AbaNavegacao.KANBAN)
    }

    fun abrirPedidos() {
        navegarParaAba(AbaNavegacao.PEDIDOS)
    }

    fun abrirCadastros() {
        navegarParaAba(AbaNavegacao.CADASTROS)
    }

}

@Composable
fun rememberControladorNavegacao(
    rotaInicial: RotaApp = RotaApp.Splash,
    abaInicial: AbaNavegacao = AbaNavegacao.INICIO
): ControladorNavegacao {
    return remember { ControladorNavegacao(rotaInicial, abaInicial) }
}
