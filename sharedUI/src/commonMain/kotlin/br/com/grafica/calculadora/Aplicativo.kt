package br.com.grafica.calculadora

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.grafica.calculadora.apresentacao.navegacao.NavegadorCentral
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

@Composable
fun Aplicativo() {
    TemaAtelie {
        NavegadorCentral()
    }
}

@Preview
@Composable
fun AplicativoPreview() {
    TemaAtelie {
        Aplicativo(

        )
    }
}


