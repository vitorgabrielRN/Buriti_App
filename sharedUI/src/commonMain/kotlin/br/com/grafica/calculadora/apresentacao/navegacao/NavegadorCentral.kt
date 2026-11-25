package br.com.grafica.calculadora.apresentacao.navegacao

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.grafica.calculadora.apresentacao.telas.cadastros.CadastrosEventoUi
import br.com.grafica.calculadora.apresentacao.telas.cadastros.CadastrosViewModel
import br.com.grafica.calculadora.apresentacao.telas.cadastros.TelaCadastros
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEventoUi
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraOrcamentoViewModel
import br.com.grafica.calculadora.apresentacao.telas.calculadora.TelaCalculadoraOrcamento
import br.com.grafica.calculadora.apresentacao.telas.inicio.InicioViewModel
import br.com.grafica.calculadora.apresentacao.telas.inicio.TelaInicio
import br.com.grafica.calculadora.apresentacao.telas.login.LoginViewModel
import br.com.grafica.calculadora.apresentacao.telas.login.TelaLogin
import br.com.grafica.calculadora.apresentacao.telas.pedidos.RegistroPedidosEventoUi
import br.com.grafica.calculadora.apresentacao.telas.pedidos.RegistroPedidosViewModel
import br.com.grafica.calculadora.apresentacao.telas.pedidos.TelaRegistroPedidos
import br.com.grafica.calculadora.apresentacao.telas.pedidos.componentes.DialogoEdicaoPedido
import br.com.grafica.calculadora.apresentacao.telas.quadro.QuadroAtelieViewModel
import br.com.grafica.calculadora.apresentacao.telas.quadro.TelaKanban
import br.com.grafica.calculadora.apresentacao.telas.splash.TelaSplash
import org.koin.compose.viewmodel.koinViewModel

/**
 * Centralizador de navegação do aplicativo.
 * Gerencia a transição entre Splash, Login e o container principal com abas.
 */
@Composable
fun NavegadorCentral(
    controlador: ControladorNavegacao = rememberControladorNavegacao()
) {
    val loginViewModel: LoginViewModel = koinViewModel()
    val estadoLogin by loginViewModel.estado.collectAsStateWithLifecycle()

    LaunchedEffect(estadoLogin.usuarioAutenticado, controlador.rotaAtual) {
        if (controlador.rotaAtual == RotaApp.Login && estadoLogin.usuarioAutenticado) {
            controlador.irParaPrincipal()
        } else if (controlador.rotaAtual == RotaApp.Principal && !estadoLogin.usuarioAutenticado) {
            controlador.irParaLogin()
        }
    }

    AnimatedContent(
        targetState = controlador.rotaAtual,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "transicaoRotaPrincipal"
    ) { rota ->
        when (rota) {
            RotaApp.Splash -> {
                TelaSplash(
                    aoConcluirSplash = {
                        controlador.concluirSplash(estaAutenticado = estadoLogin.usuarioAutenticado)
                    }
                )
            }

            RotaApp.Login -> {
                TelaLogin(
                    estado = estadoLogin,
                    aoEnviarEvento = loginViewModel::aoReceberEvento
                )
            }

            RotaApp.Principal -> {
                ConteudoPrincipalAtelie(controlador = controlador)
            }
        }
    }
}

@Composable
private fun ConteudoPrincipalAtelie(
    controlador: ControladorNavegacao
) {
    val inicioViewModel: InicioViewModel = koinViewModel()
    val quadroViewModel: QuadroAtelieViewModel = koinViewModel()
    val pedidosViewModel: RegistroPedidosViewModel = koinViewModel()
    val calculadoraViewModel: CalculadoraOrcamentoViewModel = koinViewModel()
    val cadastrosViewModel: CadastrosViewModel = koinViewModel()

    val estadoInicio by inicioViewModel.estado.collectAsStateWithLifecycle()
    val estadoQuadro by quadroViewModel.estado.collectAsStateWithLifecycle()
    val estadoPedidos by pedidosViewModel.estado.collectAsStateWithLifecycle()
    val estadoCalculadora by calculadoraViewModel.estado.collectAsStateWithLifecycle()
    val estadoCadastros by cadastrosViewModel.estado.collectAsStateWithLifecycle()

    estadoPedidos.pedidoEmEdicao?.let { pedidoEditando ->
        DialogoEdicaoPedido(
            pedido = pedidoEditando,
            produtos = estadoPedidos.produtosCatalogo,
            fornecedores = estadoPedidos.fornecedores,
            aoFechar = { pedidosViewModel.aoReceberEvento(RegistroPedidosEventoUi.FecharEdicaoPedido) },
            aoSalvar = { pedidosViewModel.aoReceberEvento(RegistroPedidosEventoUi.SalvarPedidoEditado(it)) },
            aoExcluir = { pedidosViewModel.aoReceberEvento(RegistroPedidosEventoUi.ExcluirPedido(it)) },
            aoAbrirNaCalculadora = { pedido ->
                pedidosViewModel.aoReceberEvento(RegistroPedidosEventoUi.FecharEdicaoPedido)
                calculadoraViewModel.aoReceberEvento(CalculadoraEventoUi.CarregarPedidoParaEdicao(pedido))
                controlador.abrirCalculadora()
            }
        )
    }

    Scaffold(
        bottomBar = {
            BarraNavegacaoAtelie(
                abaAtual = controlador.abaAtual,
                aoSelecionarAba = { novaAba ->
                    controlador.navegarParaAba(novaAba)
                    calculadoraViewModel.aoReceberEvento(CalculadoraEventoUi.LimparMensagens)
                    cadastrosViewModel.aoReceberEvento(CadastrosEventoUi.LimparFeedback)
                    pedidosViewModel.aoReceberEvento(RegistroPedidosEventoUi.LimparFeedback)
                }
            )
        }
    ) { preenchimentoInterno ->
        when (controlador.abaAtual) {
            AbaNavegacao.INICIO -> {
                TelaInicio(
                    estado = estadoInicio,
                    aoEnviarEvento = inicioViewModel::aoReceberEvento,
                    aoIrParaCalculadora = { controlador.abrirCalculadora() },
                    aoIrParaQuadro = { controlador.abrirQuadro() },
                    aoIrParaPedidos = { controlador.abrirPedidos() },
                    aoIrParaCadastros = { controlador.abrirCadastros() },
                    aoSelecionarPedido = { pedido ->
                        pedidosViewModel.aoReceberEvento(RegistroPedidosEventoUi.AbrirEdicaoPedido(pedido))
                    },
                    preenchimentoConteudo = preenchimentoInterno
                )
            }

            AbaNavegacao.KANBAN -> {
                TelaKanban(
                    estado = estadoQuadro,
                    aoEnviarEvento = quadroViewModel::aoReceberEvento,
                    aoSelecionarPedido = { pedido ->
                        pedidosViewModel.aoReceberEvento(RegistroPedidosEventoUi.AbrirEdicaoPedido(pedido))
                    },
                    preenchimentoConteudo = preenchimentoInterno
                )
            }

            AbaNavegacao.PEDIDOS -> {
                TelaRegistroPedidos(
                    estado = estadoPedidos,
                    aoEnviarEvento = pedidosViewModel::aoReceberEvento,
                    aoClicarPedido = { pedido ->
                        pedidosViewModel.aoReceberEvento(RegistroPedidosEventoUi.AbrirEdicaoPedido(pedido))
                    },
                    preenchimentoConteudo = preenchimentoInterno
                )
            }

            AbaNavegacao.ORCAMENTO -> {
                TelaCalculadoraOrcamento(
                    estado = estadoCalculadora,
                    aoEnviarEvento = calculadoraViewModel::aoReceberEvento,
                    preenchimentoConteudo = preenchimentoInterno
                )
            }

            AbaNavegacao.CADASTROS -> {
                TelaCadastros(
                    estado = estadoCadastros,
                    aoEnviarEvento = cadastrosViewModel::aoReceberEvento,
                    preenchimentoConteudo = preenchimentoInterno
                )
            }
        }
    }
}

@Composable
private fun BarraNavegacaoAtelie(
    abaAtual: AbaNavegacao,
    aoSelecionarAba: (AbaNavegacao) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp
    ) {
        AbaNavegacao.entries.forEach { aba ->
            val selecionada = abaAtual == aba
            NavigationBarItem(
                selected = selecionada,
                onClick = { aoSelecionarAba(aba) },
                icon = {
                    Icon(
                        imageVector = aba.icone,
                        contentDescription = aba.titulo
                    )
                },
                label = {
                    Text(
                        text = aba.titulo,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
