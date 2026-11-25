package br.com.grafica.calculadora.dados.local.mapeador

import br.com.grafica.calculadora.dados.local.entidade.UsuarioEntidade
import br.com.grafica.calculadora.dominio.modelo.Usuario

fun UsuarioEntidade.paraDominio(): Usuario = Usuario(
    id = id,
    nome = nome,
    email = email,
    senha = senha,
    mesVigente = mesVigente,
    metaMensal = metaMensal,
    modoEstudioAtivo = modoEstudioAtivo,
    logado = logado
)

fun Usuario.paraEntidade(): UsuarioEntidade = UsuarioEntidade(
    id = id,
    nome = nome,
    email = email,
    senha = senha,
    mesVigente = mesVigente,
    metaMensal = metaMensal,
    modoEstudioAtivo = modoEstudioAtivo,
    logado = logado
)
