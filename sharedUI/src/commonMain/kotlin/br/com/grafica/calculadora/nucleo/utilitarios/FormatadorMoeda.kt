package br.com.grafica.calculadora.nucleo.utilitarios

import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Formata um valor Double para moeda brasileira (ex: R$ 2.450,00) compatível com KMP.
 */
fun formatarMoedaBr(valor: Double): String {
    val totalCentavos = (valor * 100.0).roundToLong()
    val negativo = totalCentavos < 0
    val centavosAbs = abs(totalCentavos)
    val parteInteira = centavosAbs / 100
    val parteDecimal = (centavosAbs % 100).toString().padStart(2, '0')

    val textoInteiro = parteInteira.toString()
    val inteiroFormatado = buildString {
        for (i in textoInteiro.indices) {
            if (i > 0 && (textoInteiro.length - i) % 3 == 0) {
                append('.')
            }
            append(textoInteiro[i])
        }
    }
    val prefixo = if (negativo) "-R$ " else "R$ "
    return "$prefixo$inteiroFormatado,$parteDecimal"
}
