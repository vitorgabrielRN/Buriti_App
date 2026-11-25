package br.com.grafica.calculadora.apresentacao.telas.pedidos.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.apresentacao.telas.inicio.componentes.paraEstiloBadge
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.nucleo.componentes.PilulaEtapaPipeline
import br.com.grafica.calculadora.nucleo.componentes.PilulaStatusPagamento
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorCabecalhoTabela
import br.com.grafica.calculadora.nucleo.tema.CorContornoSutil
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

@Composable
fun CabecalhoColunasTabelaPedidos() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CorCabecalhoTabela)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ID",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF475569),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(56.dp)
        )
        Text(
            text = "PRODUTO",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF475569),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "PRAZO",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF475569),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(62.dp)
        )
        Text(
            text = "PGTO",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF475569),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(96.dp)
        )
    }
}

@Composable
fun LinhaTabelaPedidoEditavel(
    pedido: Pedido,
    aoClicar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { aoClicar() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = pedido.codigoFormatado,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF475569),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(56.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = pedido.nomeProduto,
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PilulaEtapaPipeline(
                    rotulo = pedido.etapaPipeline.rotulo,
                    estilo = pedido.etapaPipeline.paraEstiloBadge()
                )
                Text(
                    text = formatarMoedaBr(pedido.valorTotalFinal),
                    style = MaterialTheme.typography.labelSmall,
                    color = CorVinhoPrimaria,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = pedido.dataPrazoEntrega,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF334155),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(62.dp)
        )

        Box(
            modifier = Modifier.width(96.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            PilulaStatusPagamento(
                rotulo = pedido.statusPagamento.rotulo,
                estilo = pedido.statusPagamento.paraEstiloBadge()
            )
        }
    }
}

@Composable
fun ChipFiltroPagamento(
    rotulo: String,
    selecionado: Boolean,
    aoClicar: () -> Unit
) {
    val corFundo = if (selecionado) Color(0xFF1F1A1D) else CorBrancoSecundaria
    val corTexto = if (selecionado) CorBrancoSecundaria else Color(0xFF334155)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(corFundo)
            .border(
                width = 1.dp,
                color = if (selecionado) Color.Transparent else CorContornoSutil,
                shape = RoundedCornerShape(50)
            )
            .clickable { aoClicar() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.labelLarge,
            color = corTexto,
            fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TabelaPedidosComponentesPreview() {
    TemaAtelie {
        Column {
            CabecalhoColunasTabelaPedidos()
            LinhaTabelaPedidoEditavel(
                pedido = Pedido(
                    id = 3,
                    nomeCliente = "Boutique Flor",
                    nomeProduto = "Sacolas",
                    especificacaoMaterial = "Offset",
                    especificacaoGramatura = "180g",
                    especificacaoFormato = "25x35cm",
                    especificacaoAcabamento = "Colorido",
                    especificacaoFaces = "Frente e verso",
                    valorTotalFinal = 450.0,
                    etapaPipeline = EtapaPipeline.PRONTO,
                    statusPagamento = StatusPagamento.MEIO_A_MEIO
                ),
                aoClicar = {}
            )
        }
    }
}
