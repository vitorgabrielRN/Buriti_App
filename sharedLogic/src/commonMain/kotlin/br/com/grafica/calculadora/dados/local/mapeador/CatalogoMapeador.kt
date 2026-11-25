package br.com.grafica.calculadora.dados.local.mapeador

import br.com.grafica.calculadora.dados.local.entidade.FaixaPrecoEntidade
import br.com.grafica.calculadora.dados.local.entidade.FornecedorEntidade
import br.com.grafica.calculadora.dados.local.entidade.OfertaFornecedorEntidade
import br.com.grafica.calculadora.dados.local.entidade.ProdutoEntidade
import br.com.grafica.calculadora.dominio.modelo.FaixaPreco
import br.com.grafica.calculadora.dominio.modelo.Fornecedor
import br.com.grafica.calculadora.dominio.modelo.OfertaFornecedor
import br.com.grafica.calculadora.dominio.modelo.Produto

fun ProdutoEntidade.paraDominio(): Produto = Produto(
    id = id,
    nome = nome,
    materialOuPapel = materialOuPapel,
    gramatura = gramatura,
    formatoOuTamanho = formatoOuTamanho,
    acabamentoPadrao = acabamentoPadrao,
    opcoesFaces = opcoesFaces,
    modoPrecificacao = modoPrecificacao,
    aceitaProducaoPropria = aceitaProducaoPropria,
    precoBasePropria = precoBasePropria,
    quantidadeBasePropria = quantidadeBasePropria,
    quantidadeMinimaPropria = quantidadeMinimaPropria,
    diasProducaoPropria = diasProducaoPropria,
    acabamentoPropria = acabamentoPropria,
    observacoesPropria = observacoesPropria
)

fun Produto.paraEntidade(): ProdutoEntidade = ProdutoEntidade(
    id = id,
    nome = nome,
    materialOuPapel = materialOuPapel,
    gramatura = gramatura,
    formatoOuTamanho = formatoOuTamanho,
    acabamentoPadrao = acabamentoPadrao,
    opcoesFaces = opcoesFaces,
    modoPrecificacao = modoPrecificacao,
    aceitaProducaoPropria = aceitaProducaoPropria,
    precoBasePropria = precoBasePropria,
    quantidadeBasePropria = quantidadeBasePropria,
    quantidadeMinimaPropria = quantidadeMinimaPropria,
    diasProducaoPropria = diasProducaoPropria,
    acabamentoPropria = acabamentoPropria,
    observacoesPropria = observacoesPropria
)

fun FornecedorEntidade.paraDominio(): Fornecedor = Fornecedor(
    id = id,
    nome = nome,
    contatoOuObservacoes = contatoOuObservacoes,
    diasProducaoPadrao = diasProducaoPadrao
)

fun Fornecedor.paraEntidade(): FornecedorEntidade = FornecedorEntidade(
    id = id,
    nome = nome,
    contatoOuObservacoes = contatoOuObservacoes,
    diasProducaoPadrao = diasProducaoPadrao
)

fun OfertaFornecedorEntidade.paraDominio(): OfertaFornecedor = OfertaFornecedor(
    id = id,
    produtoId = produtoId,
    fornecedorId = fornecedorId,
    nomeFornecedor = nomeFornecedor,
    precoBase = precoBase,
    quantidadeBase = quantidadeBase,
    quantidadeMinima = quantidadeMinima,
    diasProducao = diasProducao,
    acabamento = acabamento,
    observacaoDivergenciaMaterial = observacaoDivergenciaMaterial
)

fun OfertaFornecedor.paraEntidade(): OfertaFornecedorEntidade = OfertaFornecedorEntidade(
    id = id,
    produtoId = produtoId,
    fornecedorId = fornecedorId,
    nomeFornecedor = nomeFornecedor,
    precoBase = precoBase,
    quantidadeBase = quantidadeBase,
    quantidadeMinima = quantidadeMinima,
    diasProducao = diasProducao,
    acabamento = acabamento,
    observacaoDivergenciaMaterial = observacaoDivergenciaMaterial
)

fun FaixaPrecoEntidade.paraDominio(): FaixaPreco = FaixaPreco(
    id = id,
    produtoId = produtoId,
    ofertaFornecedorId = ofertaFornecedorId,
    quantidadeMinima = quantidadeMinima,
    quantidadeMaxima = quantidadeMaxima,
    precoUnitarioOuFaixa = precoUnitarioOuFaixa,
    precoPorLoteFechado = precoPorLoteFechado
)

fun FaixaPreco.paraEntidade(): FaixaPrecoEntidade = FaixaPrecoEntidade(
    id = id,
    produtoId = produtoId,
    ofertaFornecedorId = ofertaFornecedorId,
    quantidadeMinima = quantidadeMinima,
    quantidadeMaxima = quantidadeMaxima,
    precoUnitarioOuFaixa = precoUnitarioOuFaixa,
    precoPorLoteFechado = precoPorLoteFechado
)
