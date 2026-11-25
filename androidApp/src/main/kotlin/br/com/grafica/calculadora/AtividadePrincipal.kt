package br.com.grafica.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.grafica.calculadora.nucleo.componentes.CartaoFaturamentoEstudio
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

class AtividadePrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            Aplicativo()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AtividadePrincipalPreview() {
    TemaAtelie {
        CartaoFaturamentoEstudio(
            faturamentoMensal = 2450.0,
            pedidosConcluidos = 24,
            valorAReceber = 450.0
        )
    }
}
