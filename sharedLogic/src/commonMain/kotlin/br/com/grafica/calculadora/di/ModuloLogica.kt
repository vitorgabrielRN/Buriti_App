package br.com.grafica.calculadora.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import br.com.grafica.calculadora.dados.local.BancoDadosAtelie
import br.com.grafica.calculadora.dados.local.dao.ClienteDao
import br.com.grafica.calculadora.dados.local.dao.FaixaPrecoDao
import br.com.grafica.calculadora.dados.local.dao.FornecedorDao
import br.com.grafica.calculadora.dados.local.dao.OfertaFornecedorDao
import br.com.grafica.calculadora.dados.local.dao.PedidoDao
import br.com.grafica.calculadora.dados.local.dao.ProdutoDao
import br.com.grafica.calculadora.dados.local.dao.TaxaAdicionalDao
import br.com.grafica.calculadora.dados.local.dao.UsuarioDao
import br.com.grafica.calculadora.dados.local.obterConstrutorBancoDados
import br.com.grafica.calculadora.dados.repositorio.ClienteRepositorioImpl
import br.com.grafica.calculadora.dados.repositorio.FornecedorRepositorioImpl
import br.com.grafica.calculadora.dados.repositorio.PedidoRepositorioImpl
import br.com.grafica.calculadora.dados.repositorio.ProdutoRepositorioImpl
import br.com.grafica.calculadora.dados.repositorio.TaxaRepositorioImpl
import br.com.grafica.calculadora.dados.repositorio.UsuarioRepositorioImpl
import br.com.grafica.calculadora.dominio.repositorio.ClienteRepositorio
import br.com.grafica.calculadora.dominio.repositorio.FornecedorRepositorio
import br.com.grafica.calculadora.dominio.repositorio.PedidoRepositorio
import br.com.grafica.calculadora.dominio.repositorio.ProdutoRepositorio
import br.com.grafica.calculadora.dominio.repositorio.TaxaRepositorio
import br.com.grafica.calculadora.dominio.casosdeuso.CalcularFolhasAdesivosUseCase
import br.com.grafica.calculadora.dominio.casosdeuso.CongelarValoresOrcamentoUseCase
import br.com.grafica.calculadora.dominio.casosdeuso.ObterDemandasPrioritariasUseCase
import br.com.grafica.calculadora.dominio.repositorio.UsuarioRepositorio
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

val moduloLogica: Module = module {
    single<BancoDadosAtelie> {
        obterConstrutorBancoDados()
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(true)
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
    }

    // DAOs
    single<UsuarioDao> { get<BancoDadosAtelie>().usuarioDao() }
    single<ClienteDao> { get<BancoDadosAtelie>().clienteDao() }
    single<ProdutoDao> { get<BancoDadosAtelie>().produtoDao() }
    single<FornecedorDao> { get<BancoDadosAtelie>().fornecedorDao() }
    single<OfertaFornecedorDao> { get<BancoDadosAtelie>().ofertaFornecedorDao() }
    single<FaixaPrecoDao> { get<BancoDadosAtelie>().faixaPrecoDao() }
    single<TaxaAdicionalDao> { get<BancoDadosAtelie>().taxaAdicionalDao() }
    single<PedidoDao> { get<BancoDadosAtelie>().pedidoDao() }

    // Repositórios
    single<UsuarioRepositorio> { UsuarioRepositorioImpl(get()) }
    single<ClienteRepositorio> { ClienteRepositorioImpl(get()) }
    single<TaxaRepositorio> { TaxaRepositorioImpl(get()) }
    single<ProdutoRepositorio> { ProdutoRepositorioImpl(get(), get()) }
    single<FornecedorRepositorio> { FornecedorRepositorioImpl(get(), get()) }
    single<PedidoRepositorio> { PedidoRepositorioImpl(get(), get()) }

    // UseCases
    factory { CalcularFolhasAdesivosUseCase() }
    factory { ObterDemandasPrioritariasUseCase(get()) }
    factory { CongelarValoresOrcamentoUseCase(get()) }
}
