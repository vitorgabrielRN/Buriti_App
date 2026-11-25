package br.com.grafica.calculadora.nucleo.componentes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.Aplicativo
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.utilitarios.formatarMoedaBr

/**
 * Card de Meta Mensal com Donut Progress Indicator em dourado (#E3AE01) sobre tom vinho sólido (#540C3F).
 */
@Composable
fun CartaoMetaMensal(
    faturamentoAcumulado: Double,
    metaMensal: Double,
    modifier: Modifier = Modifier
) {
    val progresso = if (metaMensal > 0.0) {
        (faturamentoAcumulado / metaMensal).coerceIn(0.0, 1.0).toFloat()
    } else 0f
    val percentualInt = ((faturamentoAcumulado / metaMensal.coerceAtLeast(1.0)) * 100.0)
        .toInt()
        .coerceAtLeast(0)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CorVinhoPrimaria,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(76.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val espessura = 8.dp.toPx()
                    drawArc(
                        color = Color.White.copy(alpha = 0.18f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = espessura, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = CorDouradoTerciaria,
                        startAngle = -90f,
                        sweepAngle = 360f * progresso,
                        useCenter = false,
                        style = Stroke(width = espessura, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "$percentualInt%",
                    style = MaterialTheme.typography.titleLarge,
                    color = CorBrancoSecundaria,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Meta do mês",
                    style = MaterialTheme.typography.labelLarge,
                    color = CorBrancoSecundaria.copy(alpha = 0.8f)
                )
                Text(
                    text = formatarMoedaBr(faturamentoAcumulado),
                    style = MaterialTheme.typography.headlineMedium,
                    color = CorDouradoTerciaria,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "de ${formatarMoedaBr(metaMensal)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CorBrancoSecundaria.copy(alpha = 0.75f)
                )
            }
        }
    }
}
@Preview
@Composable
fun CartaoMetaPreview(){
        CartaoMetaMensal(
            faturamentoAcumulado = 12.0000,
            metaMensal = 3.000,
            modifier = Modifier,
        )
}
