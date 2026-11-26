package br.com.grafica.calculadora.apresentacao.telas.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.grafica.calculadora.nucleo.tema.CoBranca
import br.com.grafica.calculadora.nucleo.tema.CorBrancoSecundaria
import br.com.grafica.calculadora.nucleo.tema.CorDouradoTerciaria
import br.com.grafica.calculadora.nucleo.tema.CorPagamentoPendenteFundo
import br.com.grafica.calculadora.nucleo.tema.CorPagamentoPendenteTexto
import br.com.grafica.calculadora.nucleo.tema.CorVinhoPrimaria
import br.com.grafica.calculadora.nucleo.tema.CorVinhoProfundo
import br.com.grafica.calculadora.nucleo.tema.TemaAtelie
import calculadora.sharedui.generated.resources.Res
import calculadora.sharedui.generated.resources.logo_colorida
import org.jetbrains.compose.resources.painterResource

@Composable
fun TelaLogin(
    estado: LoginEstadoUi,
    aoEnviarEvento: (LoginEventoUi) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CorVinhoProfundo, CorVinhoPrimaria)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Surface(
                color = CorDouradoTerciaria.copy(alpha = 0.2f),
                shape = CircleShape
            ) {

                Image(
                    painter = painterResource(Res.drawable.logo_colorida),
                    contentDescription = "Logo do aplicativo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(100.dp)
                )


            }

            Text(
                text = if (estado.modoCadastro) "Criar conta do Buriti" else "Acesse sua grafica",
                style = MaterialTheme.typography.headlineMedium,
                color = CorBrancoSecundaria,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (estado.modoCadastro)
                    "Configure seu perfil local para personalizar metas, faturamento e orçamentos."
                else
                    "Entre com seu e-mail/login e senha cadastrados.",
                style = MaterialTheme.typography.bodyMedium,
                color = CorBrancoSecundaria.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = CoBranca,
                tonalElevation = 6.dp,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (estado.mensagemErro != null) {
                        Surface(
                            color = CorPagamentoPendenteFundo,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = estado.mensagemErro,
                                style = MaterialTheme.typography.bodySmall,
                                color = CorPagamentoPendenteTexto,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    if (estado.modoCadastro) {
                        OutlinedTextField(
                            value = estado.nome,
                            onValueChange = { aoEnviarEvento(LoginEventoUi.AlterarNome(it)) },
                            label = { Text("Seu nome ou nome do Ateliê") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = estado.email,
                        onValueChange = { aoEnviarEvento(LoginEventoUi.AlterarEmail(it)) },
                        label = { Text("E-mail ou Login") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = estado.senha,
                        onValueChange = { aoEnviarEvento(LoginEventoUi.AlterarSenha(it)) },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (estado.modoCadastro) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = estado.mesVigente,
                                onValueChange = { aoEnviarEvento(LoginEventoUi.AlterarMesVigente(it)) },
                                label = { Text("Mês vigente") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = estado.metaMensalTexto,
                                onValueChange = { aoEnviarEvento(LoginEventoUi.AlterarMetaMensal(it)) },
                                label = { Text("Meta (R$)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { aoEnviarEvento(LoginEventoUi.Submeter) },
                        enabled = !estado.carregando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CorVinhoPrimaria,
                            contentColor = CorBrancoSecundaria
                        )
                    ) {
                        if (estado.carregando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = CorDouradoTerciaria,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (estado.modoCadastro) "Cadastrar e Entrar" else "Entrar na conta",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = CorBrancoSecundaria,
                            )
                        }
                    }

                    TextButton(
                        onClick = { aoEnviarEvento(LoginEventoUi.AlternarModoCadastro) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (estado.modoCadastro)
                                "Já tem uma conta? Faça login"
                            else
                                "Primeiro acesso? Criar conta",
                            color = CorVinhoPrimaria,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TelaLoginPreview() {
    TemaAtelie {
        TelaLogin(
            estado = LoginEstadoUi(modoCadastro = false),
            aoEnviarEvento = {}
        )
    }
}
