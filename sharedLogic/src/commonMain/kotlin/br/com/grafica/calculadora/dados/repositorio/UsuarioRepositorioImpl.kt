package br.com.grafica.calculadora.dados.repositorio

import br.com.grafica.calculadora.dados.local.dao.UsuarioDao
import br.com.grafica.calculadora.dados.local.entidade.UsuarioEntidade
import br.com.grafica.calculadora.dados.local.mapeador.paraDominio
import br.com.grafica.calculadora.dominio.modelo.Usuario
import br.com.grafica.calculadora.dominio.repositorio.UsuarioRepositorio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UsuarioRepositorioImpl(
    private val usuarioDao: UsuarioDao
) : UsuarioRepositorio {

    override fun observarUsuarioLogado(): Flow<Usuario?> =
        usuarioDao.observarUsuarioLogado().map { it?.paraDominio() }

    override suspend fun obterUsuarioLogado(): Usuario? =
        usuarioDao.obterUsuarioLogado()?.paraDominio()

    override suspend fun entrar(email: String, senha: String): Result<Usuario> = runCatching {
        val emailLimpo = email.trim()
        require(emailLimpo.isNotBlank()) { "Informe o e-mail ou login." }
        require(senha.isNotBlank()) { "Informe a senha." }

        val encontrado = usuarioDao.buscarPorEmail(emailLimpo)
            ?: throw IllegalArgumentException("Usuário não encontrado. Crie sua conta abaixo.")

        if (encontrado.senha != senha) {
            throw IllegalArgumentException("Senha incorreta.")
        }

        usuarioDao.deslogarTodos()
        usuarioDao.definirLogado(encontrado.id)
        encontrado.copy(logado = true).paraDominio()
    }

    override suspend fun cadastrar(
        nome: String,
        email: String,
        senha: String,
        metaMensal: Double,
        mesVigente: String
    ): Result<Usuario> = runCatching {
        val nomeLimpo = nome.trim()
        val emailLimpo = email.trim()
        require(nomeLimpo.isNotBlank()) { "Informe o nome do responsável ou ateliê." }
        require(emailLimpo.isNotBlank()) { "Informe um e-mail ou login." }
        require(senha.length >= 4) { "A senha deve ter pelo menos 4 caracteres." }

        val existente = usuarioDao.buscarPorEmail(emailLimpo)
        if (existente != null) {
            throw IllegalArgumentException("Já existe uma conta cadastrada com este e-mail/login.")
        }

        usuarioDao.deslogarTodos()
        val entidade = UsuarioEntidade(
            nome = nomeLimpo,
            email = emailLimpo,
            senha = senha,
            mesVigente = mesVigente.ifBlank { "Mês Atual" },
            metaMensal = metaMensal.coerceAtLeast(0.0),
            modoEstudioAtivo = true,
            logado = true
        )
        val novoId = usuarioDao.inserir(entidade)
        entidade.copy(id = novoId).paraDominio()
    }

    override suspend fun atualizarMetaEPerfil(
        nome: String,
        metaMensal: Double,
        mesVigente: String,
        modoEstudioAtivo: Boolean
    ) {
        val atual = usuarioDao.obterUsuarioLogado() ?: return
        usuarioDao.atualizar(
            atual.copy(
                nome = nome.ifBlank { atual.nome },
                metaMensal = metaMensal.coerceAtLeast(0.0),
                mesVigente = mesVigente.ifBlank { atual.mesVigente },
                modoEstudioAtivo = modoEstudioAtivo
            )
        )
    }

    override suspend fun sair() {
        usuarioDao.deslogarTodos()
    }
}
