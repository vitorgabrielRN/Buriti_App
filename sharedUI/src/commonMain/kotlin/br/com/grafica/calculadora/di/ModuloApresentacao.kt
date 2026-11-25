package br.com.grafica.calculadora.di

import br.com.grafica.calculadora.apresentacao.telas.cadastros.CadastrosViewModel
import br.com.grafica.calculadora.apresentacao.telas.calculadora.CalculadoraOrcamentoViewModel
import br.com.grafica.calculadora.apresentacao.telas.inicio.InicioViewModel
import br.com.grafica.calculadora.apresentacao.telas.login.LoginViewModel
import br.com.grafica.calculadora.apresentacao.telas.pedidos.RegistroPedidosViewModel
import br.com.grafica.calculadora.apresentacao.telas.quadro.QuadroAtelieViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val moduloApresentacao: Module = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::InicioViewModel)
    viewModelOf(::QuadroAtelieViewModel)
    viewModelOf(::RegistroPedidosViewModel)
    viewModelOf(::CalculadoraOrcamentoViewModel)
    viewModelOf(::CadastrosViewModel)
}
