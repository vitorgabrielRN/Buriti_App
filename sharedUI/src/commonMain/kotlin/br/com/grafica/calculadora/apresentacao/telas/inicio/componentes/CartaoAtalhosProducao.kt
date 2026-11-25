package br.com.grafica.calculadora.apresentacao.telas.inicio.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoContainer
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

@Composable
fun CartaoAtalhosProducao(
    aoIrParaCalculadora: () -> Unit,
    aoIrParaQuadro: () -> Unit,
    aoIrParaPedidos: () -> Unit,
    aoIrParaCadastros: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Fluxo de produção",
            style = MaterialTheme.typography.titleLarge,
            color = CorVinhoPrimaria
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AtalhoProducaoItem(
                icone = "🧮",
                titulo = "Orçar",
                aoClicar = aoIrParaCalculadora,
                modifier = Modifier.weight(1f)
            )
            AtalhoProducaoItem(
                icone = "📋",
                titulo = "Demandas",
                aoClicar = aoIrParaQuadro,
                modifier = Modifier.weight(1f)
            )
            AtalhoProducaoItem(
                icone = "📊",
                titulo = "Pedidos",
                aoClicar = aoIrParaPedidos,
                modifier = Modifier.weight(1f)
            )
            AtalhoProducaoItem(
                icone = "⚙️",
                titulo = "Catálogo",
                aoClicar = aoIrParaCadastros,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AtalhoProducaoItem(
    icone: String,
    titulo: String,
    aoClicar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { aoClicar() },
        shape = RoundedCornerShape(16.dp),
        color = CorBrancoSecundaria,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CorVinhoContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icone, style = MaterialTheme.typography.titleMedium)
            }
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelLarge,
                color = CorVinhoPrimaria,
                fontWeight = FontWeight.Bold
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CartaoAtalhosProducaoPreview() {
    TemaAtelie {
        CartaoAtalhosProducao(
            aoIrParaCalculadora = {},
            aoIrParaQuadro = {},
            aoIrParaPedidos = {},
            aoIrParaCadastros = {}
        )
    }
}
