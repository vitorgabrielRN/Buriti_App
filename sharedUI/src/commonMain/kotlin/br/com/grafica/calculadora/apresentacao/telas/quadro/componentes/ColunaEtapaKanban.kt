package br.com.grafica.calculadora.apresentacao.telas.quadro.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie

@Composable
fun ColunaEtapaKanban(
    etapa: EtapaPipeline,
    pedidos: List<Pedido>,
    aoClicarPedido: (Pedido) -> Unit,
    aoAvancarEtapa: (Pedido) -> Unit
) {
    Surface(
        modifier = Modifier
            .width(292.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFFF1ECEF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${etapa.numeroEtapa}. ${etapa.rotulo}",
                    style = MaterialTheme.typography.titleLarge,
                    color = CorVinhoPrimaria,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(CorVinhoPrimaria),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pedidos.size.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = CorBrancoSecundaria,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (pedidos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma demanda nesta etapa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(pedidos, key = { it.id }) { pedido ->
                        CartaoDemandaKanban(
                            pedido = pedido,
                            aoClicar = { aoClicarPedido(pedido) },
                            aoAvancarEtapa = { aoAvancarEtapa(pedido) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColunaEtapaKanbanPreview() {
    TemaAtelie {
        ColunaEtapaKanban(
            etapa = EtapaPipeline.PRODUCAO,
            pedidos = listOf(
                Pedido(
                    id = 5,
                    nomeCliente = "João Silva",
                    nomeProduto = "Caderno A5",
                    especificacaoMaterial = "Sulfite",
                    especificacaoGramatura = "90g",
                    especificacaoFormato = "A5",
                    especificacaoAcabamento = "Colorido",
                    especificacaoFaces = "Frente e verso",
                    valorTotalFinal = 240.0,
                    etapaPipeline = EtapaPipeline.PRODUCAO
                )
            ),
            aoClicarPedido = {},
            aoAvancarEtapa = {}
        )
    }
}
