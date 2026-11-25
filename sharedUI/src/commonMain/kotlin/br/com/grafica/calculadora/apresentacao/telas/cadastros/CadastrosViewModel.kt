package br.com.grafica.calculadora.apresentacao.telas.cadastros

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.grafica.calculadora.dominio.repositorio.FornecedorRepositorio
import br.com.grafica.calculadora.dominio.repositorio.ProdutoRepositorio
import br.com.grafica.calculadora.dominio.repositorio.TaxaRepositorio
import br.com.grafica.calculadora.dominio.repositorio.UsuarioRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CadastrosViewModel(
    private val usuarioRepositorio: UsuarioRepositorio,
    private val produtoRepositorio: ProdutoRepositorio,
    private val fornecedorRepositorio: FornecedorRepositorio,
    private val taxaRepositorio: TaxaRepositorio
) : ViewModel() {

    private val _estado = MutableStateFlow(CadastrosEstadoUi())
    val estado: StateFlow<CadastrosEstadoUi> = _estado.asStateFlow()

    init {
        observarCadastros()
    }

    private fun observarCadastros() {
        viewModelScope.launch {
            produtoRepositorio.observarTodosProdutos().collect { produtos ->
                _estado.update { it.copy(produtos = produtos) }
            }
        }
        viewModelScope.launch {
            fornecedorRepositorio.observarTodosFornecedores().collect { fornecedores ->
                _estado.update { it.copy(fornecedores = fornecedores) }
            }
        }
        viewModelScope.launch {
            fornecedorRepositorio.observarTodasOfertas().collect { ofertas ->
                _estado.update { it.copy(ofertasFornecedores = ofertas) }
            }
        }
        viewModelScope.launch {
            produtoRepositorio.observarTodasFaixasPreco().collect { faixas ->
                _estado.update { it.copy(faixasPreco = faixas) }
            }
        }
        viewModelScope.launch {
            taxaRepositorio.observarTodasTaxas().collect { taxas ->
                _estado.update { it.copy(taxasAdicionais = taxas) }
            }
        }
        viewModelScope.launch {
            usuarioRepositorio.observarUsuarioLogado().collect { usuario ->
                _estado.update {
                    it.copy(
                        nomeProprietaria = usuario?.nome ?: "",
                        mesVigente = usuario?.mesVigente ?: "Mês Atual",
                        metaMensal = usuario?.metaMensal ?: 0.0,
                        modoEstudioAtivo = usuario?.modoEstudioAtivo ?: true
                    )
                }
            }
        }
    }

    fun aoReceberEvento(evento: CadastrosEventoUi) {
        when (evento) {
            is CadastrosEventoUi.SelecionarSubAba -> {
                _estado.update { it.copy(subAbaSelecionada = evento.subAba) }
            }

            is CadastrosEventoUi.SalvarProduto -> viewModelScope.launch {
                produtoRepositorio.salvarProduto(evento.produto)
                emitirFeedback("Produto '${evento.produto.nome}' salvo no catálogo unificado!")
            }

            is CadastrosEventoUi.ExcluirProduto -> viewModelScope.launch {
                produtoRepositorio.excluirProduto(evento.produto)
                emitirFeedback("Produto '${evento.produto.nome}' removido.")
            }

            is CadastrosEventoUi.SalvarFornecedor -> viewModelScope.launch {
                fornecedorRepositorio.salvarFornecedor(evento.fornecedor)
                emitirFeedback("Fornecedor '${evento.fornecedor.nome}' salvo!")
            }

            is CadastrosEventoUi.ExcluirFornecedor -> viewModelScope.launch {
                fornecedorRepositorio.excluirFornecedor(evento.fornecedor)
                emitirFeedback("Fornecedor '${evento.fornecedor.nome}' excluído.")
            }

            is CadastrosEventoUi.SalvarOfertaFornecedor -> viewModelScope.launch {
                fornecedorRepositorio.salvarOfertaFornecedor(evento.oferta)
                emitirFeedback("Vínculo com '${evento.oferta.nomeFornecedor}' salvo!")
            }

            is CadastrosEventoUi.ExcluirOfertaFornecedor -> viewModelScope.launch {
                fornecedorRepositorio.excluirOfertaFornecedor(evento.oferta)
                emitirFeedback("Vínculo de fornecedor removido.")
            }

            is CadastrosEventoUi.SalvarFaixaPreco -> viewModelScope.launch {
                produtoRepositorio.salvarFaixaPreco(evento.faixa)
                emitirFeedback("Faixa de quantidade salva!")
            }

            is CadastrosEventoUi.ExcluirFaixaPreco -> viewModelScope.launch {
                produtoRepositorio.excluirFaixaPreco(evento.faixa)
                emitirFeedback("Faixa de quantidade removida.")
            }

            is CadastrosEventoUi.SalvarTaxaAdicional -> viewModelScope.launch {
                taxaRepositorio.salvarTaxa(evento.taxa)
                emitirFeedback("Taxa '${evento.taxa.nome}' atualizada!")
            }

            is CadastrosEventoUi.ExcluirTaxaAdicional -> viewModelScope.launch {
                taxaRepositorio.excluirTaxa(evento.taxa)
                emitirFeedback("Taxa '${evento.taxa.nome}' removida.")
            }

            is CadastrosEventoUi.AtualizarMetaEstudio -> viewModelScope.launch {
                usuarioRepositorio.atualizarMetaEPerfil(
                    nome = evento.nomeProprietaria,
                    metaMensal = evento.metaMensal.coerceAtLeast(0.0),
                    mesVigente = evento.mesVigente.ifBlank { "Mês Atual" },
                    modoEstudioAtivo = _estado.value.modoEstudioAtivo
                )
                emitirFeedback("Configurações do estúdio salvas!")
            }

            CadastrosEventoUi.LimparFeedback -> {
                _estado.update { it.copy(mensagemFeedback = null) }
            }
        }
    }

    private fun emitirFeedback(mensagem: String) {
        _estado.update { it.copy(mensagemFeedback = mensagem) }
    }
}
