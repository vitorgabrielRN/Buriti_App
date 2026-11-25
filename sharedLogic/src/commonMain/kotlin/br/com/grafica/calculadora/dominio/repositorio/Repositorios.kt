package br.com.grafica.calculadora.dominio.repositorio

import br.com.grafica.calculadora.dominio.modelo.Cliente
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.FaixaPreco
import br.com.grafica.calculadora.dominio.modelo.Fornecedor
import br.com.grafica.calculadora.dominio.modelo.MetodoEntrega
import br.com.grafica.calculadora.dominio.modelo.OfertaFornecedor
import br.com.grafica.calculadora.dominio.modelo.OpcaoComparativoFornecedor
import br.com.grafica.calculadora.dominio.modelo.Orcamento
import br.com.grafica.calculadora.dominio.modelo.Pedido
import br.com.grafica.calculadora.dominio.modelo.PrioridadePrazo
import br.com.grafica.calculadora.dominio.modelo.Produto
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.dominio.modelo.TaxaAdicional
import br.com.grafica.calculadora.dominio.modelo.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepositorio {
    fun observarUsuarioLogado(): Flow<Usuario?>
    suspend fun obterUsuarioLogado(): Usuario?
    suspend fun entrar(email: String, senha: String): Result<Usuario>
    suspend fun cadastrar(
        nome: String,
        email: String,
        senha: String,
        metaMensal: Double = 0.0,
        mesVigente: String = "Mês Atual"
    ): Result<Usuario>
    suspend fun atualizarMetaEPerfil(
        nome: String,
        metaMensal: Double,
        mesVigente: String,
        modoEstudioAtivo: Boolean
    )
    suspend fun sair()
}

interface ClienteRepositorio {
    suspend fun buscarClientesPorNomeOuDocumento(consulta: String): List<Cliente>
    fun observarTodosClientes(): Flow<List<Cliente>>
    suspend fun salvarOuAtualizarCliente(
        clienteIdSelecionado: Long?,
        nome: String,
        documento: String,
        telefone: String
    ): Long
}

interface ProdutoRepositorio {
    fun observarTodosProdutos(): Flow<List<Produto>>
    fun observarTodasFaixasPreco(): Flow<List<FaixaPreco>>
    suspend fun salvarProduto(produto: Produto): Long
    suspend fun excluirProduto(produto: Produto)
    suspend fun salvarFaixaPreco(faixa: FaixaPreco): Long
    suspend fun excluirFaixaPreco(faixa: FaixaPreco)

    fun compararFornecedores(
        produto: Produto,
        ofertas: List<OfertaFornecedor>,
        faixas: List<FaixaPreco>,
        quantidade: Int,
        larguraMetros: Double,
        alturaMetros: Double,
        precoPersonalizado: Double? = null
    ): List<OpcaoComparativoFornecedor>

    fun gerarOrcamentoCompleto(
        produto: Produto,
        opcaoSelecionada: OpcaoComparativoFornecedor?,
        faixas: List<FaixaPreco>,
        taxas: List<TaxaAdicional>,
        quantidade: Int,
        larguraMetros: Double,
        alturaMetros: Double,
        precoPersonalizado: Double?,
        facesSelecionadas: String,
        acabamentoSelecionado: String,
        metodoEntrega: MetodoEntrega,
        dataEntregaPrevista: String,
        prazoProducaoSolicitadoDias: Int?,
        prioridadePrazo: PrioridadePrazo,
        aplicarAdicionalNoturno: Boolean
    ): Orcamento
}

interface FornecedorRepositorio {
    fun observarTodosFornecedores(): Flow<List<Fornecedor>>
    fun observarTodasOfertas(): Flow<List<OfertaFornecedor>>
    suspend fun salvarFornecedor(fornecedor: Fornecedor): Long
    suspend fun excluirFornecedor(fornecedor: Fornecedor)
    suspend fun salvarOfertaFornecedor(oferta: OfertaFornecedor): Long
    suspend fun excluirOfertaFornecedor(oferta: OfertaFornecedor)
}

interface TaxaRepositorio {
    fun observarTodasTaxas(): Flow<List<TaxaAdicional>>
    suspend fun salvarTaxa(taxa: TaxaAdicional): Long
    suspend fun excluirTaxa(taxa: TaxaAdicional)
    suspend fun garantirDadosIniciais()
}

interface PedidoRepositorio {
    fun observarTodosPedidos(): Flow<List<Pedido>>
    suspend fun salvarPedidoComCliente(
        clienteIdSelecionado: Long?,
        nomeCliente: String,
        documentoCliente: String,
        telefoneCliente: String,
        pedido: Pedido
    ): Result<Long>
    suspend fun atualizarPedido(pedido: Pedido)
    suspend fun atualizarEtapaPedido(pedidoId: Long, novaEtapa: EtapaPipeline)
    suspend fun avancarEtapaPedido(pedido: Pedido): Boolean
    suspend fun atualizarStatusPagamento(pedidoId: Long, novoStatus: StatusPagamento)
    suspend fun excluirPedido(pedido: Pedido)
}
