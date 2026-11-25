package br.com.grafica.calculadora.apresentacao.telas.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoProfundo
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import calculadora.sharedui.generated.resources.Res
import calculadora.sharedui.generated.resources.logo_colorida
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun TelaSplash(
    aoConcluirSplash: () -> Unit,
    duracaoMs: Long = 1300L
) {
    val escala = remember { Animatable(0.86f) }
    val opacidade = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            escala.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }
        launch {
            opacidade.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 550)
            )
        }
        delay(duracaoMs)
        aoConcluirSplash()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CorVinhoProfundo, CorVinhoPrimaria)
                )
            )
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .scale(escala.value)
                .alpha(opacidade.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(
                        width = 1.dp,
                        color = CorDouradoTerciaria.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo_colorida),
                    contentDescription = "Logo do aplicativo",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp)
                )

            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = CorDouradoTerciaria.copy(alpha = 0.18f),
                shape = CircleShape
            ) {
                Text(
                    text = "GRÁFICA",
                    style = MaterialTheme.typography.labelMedium,
                    color = CorDouradoTerciaria,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Buriti Prints",
                style = MaterialTheme.typography.headlineLarge,
                color = CorBrancoSecundaria,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Orçamentos inteligentes, catálogo unificado\ne gestão de produção offline",
                style = MaterialTheme.typography.bodyMedium,
                color = CorBrancoSecundaria.copy(alpha = 0.78f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(26.dp),
                color = CorDouradoTerciaria,
                trackColor = Color.White.copy(alpha = 0.16f),
                strokeWidth = 2.5.dp
            )

        }
    }
}

@Preview
@Composable
private fun TelaSplashPreview() {
    TemaAtelie {
        TelaSplash(aoConcluirSplash = {}, duracaoMs = 60_000L)
    }
}
