package br.com.grafica.calculadora.dominio.casosdeuso

import kotlin.math.ceil
import kotlin.math.floor

/**
 * Entrada para o cálculo de folhas A3 necessárias para uma demanda de adesivos.
 *
 * @param tamanhoAdesivoCm Lado do adesivo em centímetros (assumimos formato quadrado).
 * @param quantidadeTotalAdesivos Total de unidades de adesivos solicitadas pelo cliente.
 */
data class EntradaCalculoFolhas(
    val tamanhoAdesivoCm: Double,
    val quantidadeTotalAdesivos: Int
)

/**
 * Resultado do cálculo.
 *
 * @param adesivosporFolha Quantos adesivos cabem em uma folha A3 útil.
 * @param folhasNecessarias Quantidade mínima de folhas A3 para suprir a demanda (⌈ total / porFolha ⌉).
 * @param quantidadeTotalAdesivos Espelho da entrada para exibição no resumo.
 */
data class ResultadoCalculoFolhas(
    val adesivosporFolha: Int,
    val folhasNecessarias: Int,
    val quantidadeTotalAdesivos: Int
)

/**
 * UseCase: Calcula a quantidade mínima de folhas A3 necessárias para imprimir
 * uma determinada quantidade de adesivos de tamanho fixo.
 *
 * Área útil considerada: A3 (420 × 297 mm) com margem de 5 mm em cada borda.
 *   → Largura útil = 410 mm
 *   → Altura útil  = 287 mm
 *
 * Fórmula:
 *   adesivos/folha = ⌊ L_útil / X ⌋ × ⌊ A_útil / X ⌋
 *   folhas         = ⌈ total / adesivos_por_folha ⌉
 */
class CalcularFolhasAdesivosUseCase {

    private val larguraUtilMm = 410.0
    private val alturaUtilMm  = 287.0

    operator fun invoke(entrada: EntradaCalculoFolhas): ResultadoCalculoFolhas {
        require(entrada.tamanhoAdesivoCm > 0) {
            "O tamanho do adesivo deve ser maior que zero."
        }
        require(entrada.quantidadeTotalAdesivos > 0) {
            "A quantidade total de adesivos deve ser maior que zero."
        }

        val tamanhoMm = entrada.tamanhoAdesivoCm * 10.0

        val colunas = floor(larguraUtilMm / tamanhoMm).toInt()
        val linhas  = floor(alturaUtilMm  / tamanhoMm).toInt()
        val adesivosporFolha = colunas * linhas

        val folhasNecessarias = if (adesivosporFolha > 0)
            ceil(entrada.quantidadeTotalAdesivos.toDouble() / adesivosporFolha).toInt()
        else
            0

        return ResultadoCalculoFolhas(
            adesivosporFolha      = adesivosporFolha,
            folhasNecessarias     = folhasNecessarias,
            quantidadeTotalAdesivos = entrada.quantidadeTotalAdesivos
        )
    }
}
