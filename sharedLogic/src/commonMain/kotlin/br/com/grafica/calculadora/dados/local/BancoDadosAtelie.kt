package br.com.grafica.calculadora.dados.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import br.com.grafica.calculadora.dados.local.dao.ClienteDao
import br.com.grafica.calculadora.dados.local.dao.FaixaPrecoDao
import br.com.grafica.calculadora.dados.local.dao.FornecedorDao
import br.com.grafica.calculadora.dados.local.dao.OfertaFornecedorDao
import br.com.grafica.calculadora.dados.local.dao.PedidoDao
import br.com.grafica.calculadora.dados.local.dao.ProdutoDao
import br.com.grafica.calculadora.dados.local.dao.TaxaAdicionalDao
import br.com.grafica.calculadora.dados.local.dao.UsuarioDao
import br.com.grafica.calculadora.dados.local.entidade.ClienteEntidade
import br.com.grafica.calculadora.dados.local.entidade.FaixaPrecoEntidade
import br.com.grafica.calculadora.dados.local.entidade.FornecedorEntidade
import br.com.grafica.calculadora.dados.local.entidade.OfertaFornecedorEntidade
import br.com.grafica.calculadora.dados.local.entidade.PedidoEntidade
import br.com.grafica.calculadora.dados.local.entidade.ProdutoEntidade
import br.com.grafica.calculadora.dados.local.entidade.TaxaAdicionalEntidade
import br.com.grafica.calculadora.dados.local.entidade.UsuarioEntidade

@Database(
    entities = [
        ClienteEntidade::class,
        ProdutoEntidade::class,
        FornecedorEntidade::class,
        OfertaFornecedorEntidade::class,
        FaixaPrecoEntidade::class,
        TaxaAdicionalEntidade::class,
        PedidoEntidade::class,
        UsuarioEntidade::class
    ],
    version = 6,
    exportSchema = true
)
@TypeConverters(ConversoresRoom::class)
@ConstructedBy(BancoDadosAtelieConstrutor::class)
abstract class BancoDadosAtelie : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun fornecedorDao(): FornecedorDao
    abstract fun ofertaFornecedorDao(): OfertaFornecedorDao
    abstract fun faixaPrecoDao(): FaixaPrecoDao
    abstract fun taxaAdicionalDao(): TaxaAdicionalDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun usuarioDao(): UsuarioDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object BancoDadosAtelieConstrutor : RoomDatabaseConstructor<BancoDadosAtelie> {
    override fun initialize(): BancoDadosAtelie
}
