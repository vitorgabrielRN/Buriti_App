package br.com.grafica.calculadora.apresentacao.telas.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.grafica.calculadora.dominio.modelo.EtapaPipeline
import br.com.grafica.calculadora.dominio.modelo.StatusPagamento
import br.com.grafica.calculadora.dominio.repositorio.FornecedorRepositorio
import br.com.grafica.calculadora.dominio.repositorio.PedidoRepositorio
import br.com.grafica.calculadora.dominio.repositorio.ProdutoRepositorio
import br.com.grafica.calculadora.dominio.repositorio.TaxaRepositorio
import br.com.grafica.calculadora.dominio.repositorio.UsuarioRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InicioViewModel(
    private val usuarioRepositorio: UsuarioRepositorio,
    private val taxaRepositorio: TaxaRepositorio,
    private val pedidoRepositorio: PedidoRepositorio,
    private val produtoRepositorio: ProdutoRepositorio,
    private val fornecedorRepositorio: FornecedorRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(InicioEstadoUi())
    val estado: StateFlow<InicioEstadoUi> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            taxaRepositorio.garantirDadosIniciais()
        }
        observarDadosDashboard()
    }

    private fun observarDadosDashboard() {
        viewModelScope.launch {
            combine(
                pedidoRepositorio.observarTodosPedidos(),
                usuarioRepositorio.observarUsuarioLogado(),
                produtoRepositorio.observarTodosProdutos(),
                fornecedorRepositorio.observarTodosFornecedores()
            ) { pedidos, usuario, produtos, fornecedores ->
                val nome = usuario?.nome?.takeIf { it.isNotBlank() } ?: "Ateliê"
                val meta = usuario?.metaMensal ?: 0.0
                val mes = usuario?.mesVigente?.takeIf { it.isNotBlank() } ?: "Mês Atual"
                val modoEstudio = usuario?.modoEstudioAtivo ?: true

                val faturamentoMensal = pedidos.sumOf { it.valorTotalFinal }
                val pedidosConcluidos = pedidos.count { it.etapaPipeline == EtapaPipeline.PRONTO }
                val valorAReceber = pedidos
                    .filter { it.statusPagamento == StatusPagamento.A_COBRAR }
                    .sumOf { it.valorTotalFinal }

                val demandasAtivas = pedidos.filter { it.etapaPipeline != EtapaPipeline.PRONTO }
                    .ifEmpty { pedidos }

                InicioEstadoUi(
                    nomeProprietaria = nome,
                    mesVigente = mes,
                    saudacao = "Olá, $nome",
                    modoEstudioAtivo = modoEstudio,
                    metaMensal = meta,
                    faturamentoMensal = faturamentoMensal,
                    pedidosConcluidos = pedidosConcluidos,
                    valorAReceber = valorAReceber,
                    totalProdutosCatalogo = produtos.size,
                    totalFornecedores = fornecedores.size,
                    totalPedidosRegistrados = pedidos.size,
                    demandasPrioritarias = demandasAtivas
                )
            }.collect { novoEstado ->
                _estado.value = novoEstado
            }
        }
    }

    fun aoReceberEvento(evento: InicioEventoUi) {
        when (evento) {
            InicioEventoUi.AlternarModoEstudio -> {
                val atual = _estado.value
                val proximoModo = !atual.modoEstudioAtivo
                _estado.update { it.copy(modoEstudioAtivo = proximoModo) }
                viewModelScope.launch {
                    usuarioRepositorio.atualizarMetaEPerfil(
                        nome = atual.nomeProprietaria,
                        metaMensal = atual.metaMensal,
                        mesVigente = atual.mesVigente,
                        modoEstudioAtivo = proximoModo
                    )
                }
            }

            is InicioEventoUi.AvancarEtapaPedido -> {
                viewModelScope.launch {
                    pedidoRepositorio.avancarEtapaPedido(evento.pedido)
                }
            }

            InicioEventoUi.SairSessao -> {
                viewModelScope.launch {
                    usuarioRepositorio.sair()
                }
            }
        }
    }
}
