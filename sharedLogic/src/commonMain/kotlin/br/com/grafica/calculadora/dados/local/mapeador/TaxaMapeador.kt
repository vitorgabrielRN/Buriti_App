package br.com.grafica.calculadora.dados.local.mapeador

import br.com.grafica.calculadora.dados.local.entidade.TaxaAdicionalEntidade
import br.com.grafica.calculadora.dominio.modelo.TaxaAdicional

fun TaxaAdicionalEntidade.paraDominio(): TaxaAdicional = TaxaAdicional(
    id = id,
    tipoTaxa = tipoTaxa,
    nome = nome,
    valor = valor,
    emPercentual = emPercentual,
    prazoLimiteDias = prazoLimiteDias,
    aplicarAposHora = aplicarAposHora,
    descricao = descricao
)

fun TaxaAdicional.paraEntidade(): TaxaAdicionalEntidade = TaxaAdicionalEntidade(
    id = id,
    tipoTaxa = tipoTaxa,
    nome = nome,
    valor = valor,
    emPercentual = emPercentual,
    prazoLimiteDias = prazoLimiteDias,
    aplicarAposHora = aplicarAposHora,
    descricao = descricao
)
