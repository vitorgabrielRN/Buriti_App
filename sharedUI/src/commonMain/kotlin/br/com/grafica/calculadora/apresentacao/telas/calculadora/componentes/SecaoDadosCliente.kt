package br.com.grafica.calculadora.apresentacao.telas.calculadora.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEstadoUi
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraEventoUi
import br.com.grafica.calculadora.nucleo.componentes.CartaoSecaoFormulario
import br.com.grafica.calculadora.nucleo.tema.CorVinhoContainer
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

@Composable
fun SecaoDadosCliente(
    estado: CalculadoraEstadoUi,
    aoEnviarEvento: (CalculadoraEventoUi) -> Unit
) {
    CartaoSecaoFormulario(
        titulo = "1. Dados do Cliente",
        subtitulo = "Autocomplete inteligente sem duplicar cadastros"
    ) {
        OutlinedTextField(
            value = estado.nomeCliente,
            onValueChange = { aoEnviarEvento(CalculadoraEventoUi.AlterarNomeCliente(it)) },
            label = { Text("Nome do Cliente *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (estado.exibirSugestoesClientes && estado.sugestoesClientes.isNotEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CorVinhoContainer,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Clientes encontrados no banco local:",
                        style = MaterialTheme.typography.labelSmall,
                        color = CorVinhoPrimaria,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    estado.sugestoesClientes.forEach { cliente ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    aoEnviarEvento(CalculadoraEventoUi.SelecionarSugestaoCliente(cliente))
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = cliente.nome,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = CorVinhoPrimaria
                            )
                            Text(
                                text = "${cliente.documento} • ${cliente.telefone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = estado.documentoCliente,
                onValueChange = { aoEnviarEvento(CalculadoraEventoUi.AlterarDocumentoCliente(it)) },
                label = { Text("CPF / CNPJ") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = estado.telefoneCliente,
                onValueChange = { aoEnviarEvento(CalculadoraEventoUi.AlterarTelefoneCliente(it)) },
                label = { Text("WhatsApp / Telefone") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecaoDadosClientePreview() {
    TemaAtelie {
        SecaoDadosCliente(
            estado = CalculadoraEstadoUi(
                nomeCliente = "Mariana Costa",
                documentoCliente = "123.456.789-00",
                telefoneCliente = "(11) 99999-0000"
            ),
            aoEnviarEvento = {}
        )
    }
}
