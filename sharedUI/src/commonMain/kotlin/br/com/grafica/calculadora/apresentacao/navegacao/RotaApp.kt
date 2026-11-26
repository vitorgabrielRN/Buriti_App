package br.com.grafica.calculadora.apresentacao.navegacao

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Rotas de alto nível da aplicação, centralizadas para suportar Android e Desktop.
 */
sealed interface RotaApp {
    data object Splash : RotaApp
    data object Login : RotaApp
    data object Principal : RotaApp
}

/**
 * Abas principais da área logada/principal do Ateliê.
 */
enum class AbaNavegacao(
    val titulo: String,
    val icone: ImageVector
) {
    INICIO("Início", Icons.Rounded.Home),
    KANBAN("Kanban", Icons.Rounded.Dashboard),
    PEDIDOS("Pedidos", Icons.Rounded.Receipt),

    ORCAMENTO("Orçar", Icons.Rounded.Calculate),
    CADASTROS("Painel", Icons.Rounded.Settings)
}
