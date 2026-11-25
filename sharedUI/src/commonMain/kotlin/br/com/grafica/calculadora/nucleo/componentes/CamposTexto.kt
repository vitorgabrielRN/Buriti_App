package br.com.grafica.calculadora.nucleo.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

@Composable
fun CampoTextoPadrao(
    valor: String,
    aoAlterarValor: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    dica: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    linhaUnica: Boolean = true
) {
    OutlinedTextField(
        value = valor,
        onValueChange = aoAlterarValor,
        label = { Text(rotulo) },
        placeholder = dica?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        modifier = modifier,
        singleLine = linhaUnica
    )
}

@Composable
fun CartaoSecaoFormulario(
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    conteudo: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            conteudo()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartaoSecaoFormularioPreview() {
    TemaAtelie {
        CartaoSecaoFormulario(
            titulo = "Dados do Cliente",
            subtitulo = "Exemplo de seção de formulário",
            modifier = Modifier.padding(16.dp)
        ) {
            CampoTextoPadrao(
                valor = "Mariana Costa",
                aoAlterarValor = {},
                rotulo = "Nome do cliente",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
