package br.com.grafica.calculadora.dados.local.mapeador

import br.com.grafica.calculadora.dados.local.entidade.ClienteEntidade
import br.com.grafica.calculadora.dominio.modelo.Cliente

fun ClienteEntidade.paraDominio(): Cliente = Cliente(
    id = id,
    nome = nome,
    documento = documento,
    telefone = telefone,
    criadoEm = criadoEm
)

fun Cliente.paraEntidade(): ClienteEntidade = ClienteEntidade(
    id = id,
    nome = nome,
    documento = documento,
    telefone = telefone,
    criadoEm = criadoEm
)
