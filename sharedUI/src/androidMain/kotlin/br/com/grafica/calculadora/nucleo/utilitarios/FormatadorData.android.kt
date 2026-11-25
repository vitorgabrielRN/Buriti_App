package br.com.grafica.calculadora.nucleo.utilitarios

import java.util.Calendar

actual fun dataHojeMaisDias(dias: Int): String {
    val calendario = Calendar.getInstance()
    calendario.add(Calendar.DAY_OF_MONTH, dias.coerceAtLeast(0))
    val dia = calendario.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
    val mes = (calendario.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
    return "$dia/$mes"
}
