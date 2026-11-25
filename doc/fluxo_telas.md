# Manual Completo de Fluxo de Telas e Arquitetura Visual — Ateliê Print

> **Objetivo deste documento:** Servir como mapa definitivo da camada visual (`:sharedUI`), mostrando o desenho de cada tela, como uma tela conversa com a outra, quais estados/eventos controlam cada botão e **onde exatamente mexer no código** para fazer qualquer alteração.

---

## 1. Mapa Visual Completo de Navegação

O aplicativo **não usa fragmentos nem rotas em string**. Toda a navegação é controlada por estado em Kotlin puro dentro de `:sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/navegacao/`.

```text
[ :androidApp ] AtividadePrincipal.kt
       │
       ▼
[ :sharedUI ] Aplicativo.kt ──► envolve tudo em TemaAtelie { NavegadorCentral() }
       │
       ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ NavegadorCentral.kt (Observa LoginViewModel + ControladorNavegacao.rotaAtual)           │
└──────┬───────────────────────────────────────┬──────────────────────────────────────────┘
       │                                       │
       ▼ (1. Rota Inicial)                     ▼ (2. Se não autenticado)
┌─────────────────────────┐             ┌─────────────────────────────────────────────────┐
│ RotaAtelie.Splash       │             │ RotaAtelie.Login                                │
│ TelaSplash.kt           │────────────►│ TelaLogin.kt + LoginViewModel.kt                │
│ (Animação 1.3s)         │             │ • Modo Entrar (E-mail + Senha)                  │
└──────┬──────────────────┘             │ • Modo Criar Conta (Nome, E-mail, Senha, Meta)  │
       │                                └──────┬──────────────────────────────────────────┘
       │ (Se já autenticado)                   │ (LaunchedEffect: usuarioAutenticado == true)
       ▼                                       ▼
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ RotaAtelie.Principal ──► ConteudoPrincipalAtelie()                                      │
│                                                                                         │
│  ┌───────────────────────────────────────────────────────────────────────────────────┐  │
│  │ MODAL GLOBAL: DialogoEdicaoPedido.kt                                              │  │
│  │ Aparece por cima de QUALQUER aba quando `estadoPedidos.pedidoEmEdicao != null`    │  │
│  │ (Acionado ao clicar num pedido em INICIO, QUADRO ou PEDIDOS)                      │  │
│  │   └──► Botão [Calculadora ->]: Carrega o pedido na Calculadora e muda de aba!     │  │
│  └───────────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                         │
│  ┌─────────────┬─────────────────┬─────────────────┬─────────────────┬───────────────┐  │
│  │ Aba 1       │ Aba 2           │ Aba 3           │ Aba 4           │ Aba 5         │  │
│  │ INICIO      │ QUADRO          │ PEDIDOS         │ CALCULADORA     │ CADASTROS     │  │
│  │ "Início"    │ "Demandas"      │ "Pedidos"       │ "Orçar"         │ "Painel"      │  │
│  ├─────────────┼─────────────────┼─────────────────┼─────────────────┼───────────────┤  │
│  │ TelaInicio  │ TelaQuadroAtelie│ TelaRegistro    │ TelaCalculadora │ TelaCadastros │  │
│  │             │                 │ Pedidos         │ Orcamento       │               │  │
│  │ • Cabeçalho │ • 4 Colunas     │ • Ordenação     │ • 1. Cliente    │ • Sub-aba 1:  │  │
│  │   + Sair    │   Kanban:       │   ↓↑ Pagamento  │ • 2. Produto    │   Insumos     │  │
│  │ • Card      │   1. Orçamento  │ • Tabela com    │ • 3. Qtd/Cor/   │ • Sub-aba 2:  │  │
│  │   Fatura.   │   2. Arte       │   soma no       │      FrenteVerso│   Fornecedores│  │
│  │ • Card Meta │   3. Produção   │   rodapé        │ • 4. Prazos/    │ • Sub-aba 3:  │  │
│  │ • Atalhos   │   4. Pronto     │ • Chips Filtro: │      Entrega    │   Faixas Qtd  │  │
│  │   p/ 4 abas │ • Botão avançar │   Todos / Pago  │ • 5. Comparativo│ • Sub-aba 4:  │  │
│  │ • Gaveta    │   etapa no card │   50% / Cobrar  │ • 6. Comprovante│   Taxas & Meta│  │
│  │   Demandas  │                 │                 │ • 7. Modal      │               │  │
│  │   (Sheet)   │                 │                 │   Confirmação   │               │  │
│  └─────────────┴─────────────────┴─────────────────┴─────────────────┴───────────────┘  │
│                                                                                         │
│  ┌───────────────────────────────────────────────────────────────────────────────────┐  │
│  │ BarraNavegacaoAtelie (NavigationBar fixa no rodapé com as 5 abas)                 │  │
│  └───────────────────────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Como Funciona o Padrão das Telas (UDF: Fluxo Unidirecional)

Todas as 6 telas do app seguem **exatamente a mesma estrutura de 3 arquivos**:

```text
  ┌─────────────────────────┐
  │ 1. TelaXxx.kt           │  ◄── Recebe apenas (estado: XxxEstadoUi, aoEnviarEvento: (XxxEventoUi) -> Unit)
  │    (Composable puro)    │      NÃO faz cálculo e NÃO acessa banco de dados diretamente.
  └───────────┬─────────────┘
              │ Usuário clica / digita ──► dispara XxxEventoUi
              ▼
  ┌─────────────────────────┐
  │ 2. XxxViewModel.kt      │  ◄── Função `aoReceberEvento(evento)` processa a ação,
  │    (Cérebro da tela)    │      chama os Repositórios de `:sharedLogic` e atualiza o `_estado`.
  └───────────┬─────────────┘
              │ Emite novo estado via StateFlow<XxxEstadoUi>
              ▼
  ┌─────────────────────────┐
  │ 3. XxxEstadoUi.kt       │  ◄── Contém a `data class XxxEstadoUi` (dados na tela)
  │    (Estado + Eventos)   │      e a `sealed interface XxxEventoUi` (ações possíveis).
  └─────────────────────────┘
```

> **Por que isso é ótimo para você?**
> - Como as telas (`Tela*.kt`) recebem apenas `estado` e `aoEnviarEvento`, você pode editar o layout à vontade e ver o resultado imediato no `@Preview` no final de cada arquivo sem precisar rodar o banco de dados.
> - Se quiser criar um botão novo numa tela, basta:
>   1. Adicionar um `data object MeuNovoEvento : XxxEventoUi` no arquivo `*EstadoUi.kt`.
>   2. Tratar esse evento no `when (evento)` dentro do `*ViewModel.kt`.
>   3. Chamar `aoEnviarEvento(XxxEventoUi.MeuNovoEvento)` no `onClick` do botão na `Tela*.kt`.

---

## 3. Os 3 Arquivos que Controlam a Navegação

Antes de entrar em cada tela, veja o que cada arquivo da pasta `apresentacao/navegacao/` faz:

### 3.1. [RotaAtelie.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/navegacao/RotaAtelie.kt)
Define quais telas existem no app:
- **`sealed interface RotaAtelie`**:
  - `RotaAtelie.Splash` (abertura)
  - `RotaAtelie.Login` (autenticação/cadastro)
  - `RotaAtelie.Principal` (área logada com barra inferior)
- **`enum class AbaNavegacao(val titulo: String, val icone: ImageVector)`**:
  - `INICIO("Início", Icons.Rounded.Home)`
  - `QUADRO("Demandas", Icons.Rounded.Dashboard)`
  - `PEDIDOS("Pedidos", Icons.Rounded.Receipt)`
  - `CALCULADORA("Orçar", Icons.Rounded.Calculate)`
  - `CADASTROS("Painel", Icons.Rounded.Settings)`

### 3.2. [ControladorNavegacao.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/navegacao/ControladorNavegacao.kt)
Guarda em memória qual rota e qual aba estão abertas no momento:
- `var rotaAtual`: começa em `RotaAtelie.Splash`.
- `var abaAtual`: começa em `AbaNavegacao.INICIO`.
- `historicoAbas`: pilha de abas visitadas para permitir `voltarAba()`.
- **Funções de transição:**
  - `concluirSplash(estaAutenticado: Boolean)` -> vai para `Principal` se logado, ou `Login` se deslogado.
  - `irParaPrincipal()` / `irParaLogin()`
  - `navegarParaAba(destino: AbaNavegacao)`
  - Atalhos rápidos: `abrirCalculadora()`, `abrirQuadro()`, `abrirPedidos()`, `abrirCadastros()`.

### 3.3. [NavegadorCentral.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/navegacao/NavegadorCentral.kt)
É o maestro que liga os `ViewModel`s às telas:
- **Vigia o Login em tempo real (linhas 58–64):**
  ```kotlin
  LaunchedEffect(estadoLogin.usuarioAutenticado, controlador.rotaAtual) {
      if (controlador.rotaAtual == RotaAtelie.Login && estadoLogin.usuarioAutenticado) {
          controlador.irParaPrincipal()
      } else if (controlador.rotaAtual == RotaAtelie.Principal && !estadoLogin.usuarioAutenticado) {
          controlador.irParaLogin()
      }
  }
  ```
  Ou seja: quando o usuário faz login (ou clica em "Sair" na `TelaInicio`), o `estadoLogin.usuarioAutenticado` muda no banco Room e o `NavegadorCentral` troca automaticamente entre `Login` e `Principal`.
- **Mantém os 5 ViewModels vivos na área logada (`ConteudoPrincipalAtelie`, linhas 98–108):**
  Como `inicioViewModel`, `quadroViewModel`, `pedidosViewModel`, `calculadoraViewModel` e `cadastrosViewModel` são instanciados em `ConteudoPrincipalAtelie`, **você não perde o que estava digitando na Calculadora se trocar rapidamente para a aba Pedidos ou Painel e voltar**.
- **Limpa mensagens de aviso ao trocar de aba (linhas 130–135):**
  Sempre que você toca num ícone da `BarraNavegacaoAtelie`, ele envia `LimparMensagens` / `LimparFeedback` para limpar banners verdes/vermelhos antigos.

---

## 4. Anatomia Detalhada de Cada Tela (Desenho + Estados + Eventos)

---

### 4.1. Tela de Abertura — `TelaSplash`
- **Arquivo:** [TelaSplash.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/splash/TelaSplash.kt)
- **O que faz:** Exibe a marca "Ateliê Print" com fundo degradê vinho (`CorVinhoProfundo` -> `CorVinhoPrimaria`), anima a escala (`0.86f` -> `1f`) e a opacidade (`0f` -> `1f`), aguarda `duracaoMs = 1300L` (1,3 segundo) e chama `aoConcluirSplash()`.
- **Para onde vai depois:** O `NavegadorCentral` chama `controlador.concluirSplash(estaAutenticado = estadoLogin.usuarioAutenticado)`. Se houver sessão ativa no banco (`UsuarioEntidade`), entra direto em `RotaAtelie.Principal`; caso contrário, abre `RotaAtelie.Login`.

---

### 4.2. Tela de Autenticação e Primeiro Acesso — `TelaLogin`
- **Arquivos:**
  - UI: [TelaLogin.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/login/TelaLogin.kt)
  - Estado/Eventos: [LoginEstadoUi.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/login/LoginEstadoUi.kt)
  - Lógica: [LoginViewModel.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/login/LoginViewModel.kt)

#### Desenho da Tela (`modoCadastro = false` vs `modoCadastro = true`)
```text
┌──────────────────────────────────────────────────────────┐
│                     ✦ ATELIÊ PRINT                       │
│        "Acesse seu Ateliê" / "Criar conta do Ateliê"     │
│                                                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │ [!] Banner de Erro (se estado.mensagemErro != null)│  │
│  │                                                    │  │
│  │ [ Seu nome ou nome do Ateliê ]  (só no cadastro)   │  │
│  │ [ E-mail ou Login            ]                     │  │
│  │ [ Senha (••••••)             ]                     │  │
│  │ [ Mês vigente ]  [ Meta (R$) ]  (só no cadastro)   │  │
│  │                                                    │  │
│  │ (       Entrar no Ateliê / Cadastrar e Entrar    ) │  │
│  │         "Primeiro acesso? Criar conta..."          │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

#### Eventos que a `TelaLogin` dispara (`LoginEventoUi`)
| Ação do Usuário | Evento Disparado | O que o `LoginViewModel` faz |
| :--- | :--- | :--- |
| Digitar nos campos | `AlterarNome`, `AlterarEmail`, `AlterarSenha`, `AlterarMesVigente`, `AlterarMetaMensal` | Atualiza o texto no `LoginEstadoUi` e limpa erro anterior. |
| Clicar no botão de alternar modo | `AlternarModoCadastro` | Inverte `modoCadastro` (`true` <-> `false`) para mostrar/esconder Nome, Mês e Meta. |
| Clicar no botão principal | `Submeter` | Valida campos e chama `usuarioRepositorio.autenticar(...)` ou `registrarUsuario(...)`. Quando dá certo, `usuarioAutenticado` vira `true` e o `NavegadorCentral` abre a tela `Principal`. |

---

### 4.3. Aba 1: Início (Dashboard) — `TelaInicio`
- **Arquivos:**
  - UI Principal: [TelaInicio.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/inicio/TelaInicio.kt)
  - Estado/Eventos: [InicioEstadoUi.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/inicio/InicioEstadoUi.kt)
  - Lógica: [InicioViewModel.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/inicio/InicioViewModel.kt)
  - Subcomponentes:
    - [CartaoFaturamentoEstudio.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/nucleo/componentes/CartaoFaturamentoEstudio.kt) (em `nucleo/componentes/`)
    - [CartaoMetaMensal.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/nucleo/componentes/CartaoMetaMensal.kt) (em `nucleo/componentes/`)
    - [CartaoAtalhosProducao.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/inicio/componentes/CartaoAtalhosProducao.kt)
    - [GavetaDemandas.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/inicio/componentes/GavetaDemandas.kt) e [CartaoDemandaAtiva.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/inicio/componentes/CartaoDemandaAtiva.kt)

#### Desenho da Tela
```text
┌──────────────────────────────────────────────────────────┐
│ [NOVEMBRO] [✨ Modo estúdio]                     [Sair]  │ ◄── CabecalhoInicio (interno em TelaInicio.kt)
│ Olá, Ateliê                                              │
│                                                          │
│ ┌──────────────────────────────────────────────────────┐ │
│ │ CartaoFaturamentoEstudio                             │ │ ◄── Faturamento acumulado, pedidos concluídos
│ │ R$ 1.250,00  |  Concluídos: 8  |  A receber: R$ 300  │ │     e valor pendente a receber
│ └──────────────────────────────────────────────────────┘ │
│ ┌──────────────────────────────────────────────────────┐ │
│ │ CartaoMetaMensal                                     │ │ ◄── Progresso circular % da meta mensal
│ │ Meta: R$ 4.000,00 (31%)                              │ │
│ └──────────────────────────────────────────────────────┘ │
│ ┌──────────────────────────────────────────────────────┐ │
│ │ CartaoAtalhosProducao                                │ │ ◄── 4 botões que navegam para as outras abas:
│ │ [Novo Orçamento]  [Quadro Kanban]                    │ │     aoIrParaCalculadora, aoIrParaQuadro,
│ │ [Tabela Pedidos]  [Painel / Insumos]                 │ │     aoIrParaPedidos, aoIrParaCadastros
│ └──────────────────────────────────────────────────────┘ │
│                                                          │
│ ╔══════════════════════════════════════════════════════╗ │
│ ║ ─── (Alça de arrastar) GavetaDemandas (BottomSheet)  ║ │ ◄── Lista `estado.demandasPrioritarias`
│ ║ Demandas Ativas                      [Ver quadro ->] ║ │     • Clicar no card ──► abre DialogoEdicaoPedido
│ ║ ┌──────────────────────────────────────────────────┐ ║ │     • Botão avançar ──► AvancarEtapaPedido
│ ║ │ CartaoDemandaAtiva (#0021 - Mariana Costa)       │ ║ │
│ ║ └──────────────────────────────────────────────────┘ ║ │
│ ╚══════════════════════════════════════════════════════╝ │
└──────────────────────────────────────────────────────────┘
```

#### Ligações de Navegação da `TelaInicio`
- **Botão `[Sair]`**: dispara `InicioEventoUi.SairSessao` -> desloga o usuário no banco -> `NavegadorCentral` detecta `usuarioAutenticado == false` e volta para `TelaLogin`.
- **Atalhos de Produção**: chamam diretamente `controlador.abrirCalculadora()`, `controlador.abrirQuadro()`, `controlador.abrirPedidos()` e `controlador.abrirCadastros()`.
- **Clicar numa demanda da gaveta (`aoSelecionarPedido`)**: o `NavegadorCentral` intercepta e envia `RegistroPedidosEventoUi.AbrirEdicaoPedido(pedido)` para o `pedidosViewModel`, abrindo o **`DialogoEdicaoPedido`**.

---

### 4.4. Aba 2: Demandas (Quadro Kanban) — `TelaKanban`
- **Arquivos:**
  - UI Principal: [TelaQuadroAtelie.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/quadro/TelaQuadroAtelie.kt)
  - Estado/Eventos: [QuadroAtelieEstadoUi.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/quadro/QuadroAtelieEstadoUi.kt)
  - Lógica: [QuadroAtelieViewModel.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/quadro/QuadroAtelieViewModel.kt)
  - Subcomponentes:
    - [ColunaEtapaKanban.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/quadro/componentes/ColunaEtapaKanban.kt)
    - [CartaoDemandaKanban.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/quadro/componentes/CartaoDemandaKanban.kt)

#### Desenho da Tela (Rolagem Horizontal `LazyRow`)
```text
┌─────────────────────────────────────────────────────────────────────────┐
│ Pipeline de produção                                                    │
│ Fila de Demandas                                                        │
│                                                                         │
│ ◄── Rolagem Horizontal (LazyRow com as 4 etapas de EtapaPipeline) ──►   │
│ ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐        │
│ │ 1. Orçamento (2) │  │ 2. Arte (1)      │  │ 3. Produção (3)  │  ...   │
│ ├──────────────────┤  ├──────────────────┤  ├──────────────────┤        │
│ │ ┌──────────────┐ │  │ ┌──────────────┐ │  │ ┌──────────────┐ │        │
│ │ │ #0012        │ │  │ │ #0015        │ │  │ │ #0018        │ │        │
│ │ │ Cliente A    │ │  │ │ Cliente B    │ │  │ │ Cliente C    │ │        │
│ │ │ Cartão Visita│ │  │ │ Adesivo      │ │  │ │ Banner       │ │        │
│ │ │ [Avançar ->] │ │  │ │ [Avançar ->] │ │  │ │ [Avançar ->] │ │        │
│ │ └──────────────┘ │  │ └──────────────┘ │  │ └──────────────┘ │        │
│ └──────────────────┘  └──────────────────┘  └──────────────────┘        │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Como funciona
- As colunas vêm de `EtapaPipeline.entries` (`ORCAMENTO` -> `ARTE` -> `PRODUCAO` -> `PRONTO`).
- **Clicar no corpo do cartão (`aoClicarPedido`)**: abre o modal global `DialogoEdicaoPedido`.
- **Clicar no botão de avançar etapa (`aoAvancarEtapa`)**: dispara `QuadroAtelieEventoUi.AvancarEtapa(pedido)` -> chama `pedidoRepositorio.avancarEtapaPedido(pedido)` e move o cartão para a coluna seguinte no banco Room.

---

### 4.5. Aba 3: Pedidos (Tabela Editável) — `TelaRegistroPedidos`
- **Arquivos:**
  - UI Principal: [TelaRegistroPedidos.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/pedidos/TelaRegistroPedidos.kt)
  - Estado/Eventos: [RegistroPedidosEstadoUi.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/pedidos/RegistroPedidosEstadoUi.kt)
  - Lógica: [RegistroPedidosViewModel.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/pedidos/RegistroPedidosViewModel.kt)
  - Subcomponentes:
    - [TabelaPedidosComponentes.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/pedidos/componentes/TabelaPedidosComponentes.kt) (`CabecalhoColunasTabelaPedidos`, `LinhaTabelaPedidoEditavel`, `ChipFiltroPagamento`)
    - [DialogoEdicaoPedido.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/pedidos/componentes/DialogoEdicaoPedido.kt)

#### Desenho da Tela
```text
┌──────────────────────────────────────────────────────────┐
│ Pedidos ( 12 )                           [ ↓↑ Pagamento ]│ ◄── AlternarOrdenacaoPagamento
│ Toque em qualquer registro da tabela para editar         │
│                                                          │
│ ┌──────────────────────────────────────────────────────┐ │
│ │ CLIENTE / ITEM        ETAPA      PGTO        VALOR   │ │ ◄── CabecalhoColunasTabelaPedidos
│ ├──────────────────────────────────────────────────────┤ │
│ │ Mariana Costa         Produção   100% Pago   R$ 180  │ │ ◄── LinhaTabelaPedidoEditavel
│ │ Cartão de Visita                                     │ │     (Clicar na linha abre DialogoEdicaoPedido)
│ ├──────────────────────────────────────────────────────┤ │
│ │ João Silva            Arte       50% Sinal   R$ 95   │ │
│ │ Panfleto A5                                          │ │
│ ├──────────────────────────────────────────────────────┤ │
│ │ Total                                     R$ 275,00  │ │ ◄── Rodapé com estado.somaTotalFiltrada
│ └──────────────────────────────────────────────────────┘ │
│                                                          │
│ ( Todos )  ( 100% Pago )  ( 50% Sinal )  ( A Cobrar )    │ ◄── ChipFiltroPagamento
└──────────────────────────────────────────────────────────┘
```

---

### 4.6. Modal Global: Edição Rápida de Pedido — `DialogoEdicaoPedido`
- **Arquivo:** [DialogoEdicaoPedido.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/pedidos/componentes/DialogoEdicaoPedido.kt)
- **Onde ele é montado:** Em [NavegadorCentral.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/navegacao/NavegadorCentral.kt#L110-L124).
- **Como funciona:**
  - Sempre que `estadoPedidos.pedidoEmEdicao` recebe um `Pedido` (vindo de um clique na `TelaInicio`, `TelaKanban` ou `TelaRegistroPedidos`), o modal abre por cima da tela atual.
  - Possui estado local (`remember(pedido)`) para editar rapidamente: Cliente, Telefone, Prazo (`dd/MM`), Produto (com chips rápidos do catálogo), Quantidade, Valor Unitário, Taxas, Modalidade (`Própria` / `Terceirizada`), Fornecedor, Etapa do Pipeline (`1. Orçamento` a `4. Pronto`) e Status Financeiro (`100% Pago`, `50% Sinal`, `A Cobrar`).
  - Recalcula o total em tempo real enquanto você digita.
  - **Ponte para a Calculadora (`[Calculadora ->]` no canto superior direito):**
    Quando clicado, executa 3 passos em `NavegadorCentral.kt` (linhas 118–122):
    1. Fecha o modal (`RegistroPedidosEventoUi.FecharEdicaoPedido`).
    2. Envia o pedido para a calculadora (`CalculadoraEventoUi.CarregarPedidoParaEdicao(pedido)`).
    3. Muda a aba ativa para `AbaNavegacao.CALCULADORA` (`controlador.abrirCalculadora()`).

---

### 4.7. Aba 4: Orçar (Calculadora de Orçamentos) — `TelaCalculadoraOrcamento`
- **Arquivos:**
  - UI Principal: [TelaCalculadoraOrcamento.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/calculadora/TelaCalculadoraOrcamento.kt)
  - Estado/Eventos: [CalculadoraEstadoUi.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/calculadora/CalculadoraEstadoUi.kt)
  - Lógica: [CalculadoraOrcamentoViewModel.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/calculadora/CalculadoraOrcamentoViewModel.kt)

#### Ordem Exata das Seções na `LazyColumn` de `TelaCalculadoraOrcamento.kt`
```text
┌──────────────────────────────────────────────────────────┐
│ Novo Orçamento (ou "Editando Pedido #0021")   [ Limpar ] │ ◄── Cabeçalho (linhas 51-78)
│ Cliente → Produto → Quantidade → Prazo → Confirmar       │
├──────────────────────────────────────────────────────────┤
│ 1. SecaoDadosCliente.kt                                  │ ◄── Nome (com autocomplete de clientes salvos),
│    • Nome do Cliente + sugestões                         │     Documento e Telefone/WhatsApp
│    • Telefone / WhatsApp e Documento                     │
├──────────────────────────────────────────────────────────┤
│ 2. SecaoCatalogoProduto.kt                               │ ◄── Campo de busca inteligente (aceita nome ou
│    • Busca responsiva (ex: "cv" acha "Cartão de Visita") │     iniciais das palavras) + resumo do material
│    • Dropdown com itens filtrados                        │
├──────────────────────────────────────────────────────────┤
│ 3. SecaoMedidasEFaixas.kt                                │ ◄── • Botões [−] e [+] + campo Qtd
│    • Quantidade com [−] [+] e chips rápidos (50,100...)  │     • Se produto for por m², exibe Largura x Altura
│    • Banner Subtotal: "100 un × R$ 0,50 = R$ 50,00"      │     • Preço personalizado opcional
│    • Switch: Colorido / Preto e branco                   │     • Switch (impressaoColorida)
│    • Checkbox: Impressão Frente e Verso                  │     • Checkbox (frenteEVerso)
├──────────────────────────────────────────────────────────┤
│ 4. SecaoPrazosETaxas.kt                                  │ ◄── • Prioridade: Normal / Urgência / Emergência
│    • Chips de Prioridade + Prazo de produção (dias)      │     • Ao mudar os dias de produção, atualiza
│    • Data de entrega vinculada automaticamente           │       automaticamente `dataEntregaPrevista`!
│    • Modalidade: Retirada / Entrega                      │     • Nota: Pipeline começa sempre em ORCAMENTO
│    • Switch: Adicional Noturno                           │       (não precisa escolher na tela)
│    • Chips de Pagamento (A Cobrar, 50% Sinal, 100% Pago) │
├──────────────────────────────────────────────────────────┤
│ 5. SecaoComparativoFornecedores.kt                       │ ◄── Fica no final do formulário! Compara Produção
│    • Cards comparando Produção Própria vs Fornecedores   │     Própria vs Terceirizada (preço, prazo, mín.)
├──────────────────────────────────────────────────────────┤
│ 6. CartaoComprovanteOrcamento.kt                         │ ◄── Resumo financeiro completo + botão principal:
│    • Botão [ Confirmar Orçamento ]                       │     Dispara `SolicitarConfirmacao`
└──────────────────────────────────────────────────────────┘
                            │
                            ▼ (Quando `estado.exibirConfirmacao == true`)
┌──────────────────────────────────────────────────────────┐
│ 7. MODAL: DialogoConfirmacaoOrcamento.kt                 │
│    "Deseja confirmar este orçamento?"                    │
│    • Mostra resumo final (Cliente, Item, Qtd, Entrega,   │
│      Modalidade, Pagamento e Valor Total)                │
│    • Botão [ Voltar e Editar ] ──► `VoltarParaEdicao`    │
│    • Botão [ Sim, Confirmar  ] ──► `ConfirmarOrcamento`  │
│      (Salva no Room como Pedido na etapa ORCAMENTO)      │
└──────────────────────────────────────────────────────────┘
```

---

### 4.8. Aba 5: Painel Gerencial (CRUD) — `TelaCadastros`
- **Arquivos:**
  - UI Principal: [TelaCadastros.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/cadastros/TelaCadastros.kt)
  - Estado/Eventos: [CadastrosEstadoUi.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/cadastros/CadastrosEstadoUi.kt)
  - Lógica: [CadastrosViewModel.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/cadastros/CadastrosViewModel.kt)

#### Sub-abas Internas (`enum class SubAbaCadastros`)
A `TelaCadastros` tem uma barra horizontal de `FilterChip` que alterna `estado.subAbaSelecionada` entre 4 sub-telas independentes:

| Chip na Tela | Enum `SubAbaCadastros` | Arquivo do Componente | O que gerencia no Banco Room |
| :--- | :--- | :--- | :--- |
| **Insumos & Papéis** | `PRODUTOS` | [SecaoCadastroProdutos.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/cadastros/componentes/SecaoCadastroProdutos.kt) | Criar/editar/excluir produtos do catálogo (material, gramatura, formato, modo de precificação, preço base próprio, prazo normal e qtd mínima). |
| **Fornecedores** | `FORNECEDORES` | [SecaoCadastroFornecedores.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/cadastros/componentes/SecaoCadastroFornecedores.kt) | Cadastrar gráficas parceiras (`Fornecedor`) e vincular ofertas terceirizadas por produto (`OfertaFornecedor`). |
| **Faixas de Qtd** | `FAIXAS` | [SecaoCadastroFaixasPreco.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/cadastros/componentes/SecaoCadastroFaixasPreco.kt) | Configurar tabelas progressivas de desconto por quantidade (`FaixaPreco`: de X a Y unidades = R$ Z). |
| **Taxas & Meta** | `TAXAS_E_META` | [SecaoTaxasEMeta.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/cadastros/componentes/SecaoTaxasEMeta.kt) | Configurar taxas extras (Urgência, Emergência, Entrega, Adicional Noturno) e atualizar Nome do Ateliê, Mês Vigente e Meta Mensal (R$). |

---

## 5. Guia Prático: "Quero Modificar X, Onde Eu Mexo?"

### Caso 1: "Quero pular o Splash e o Login para testar direto a tela que estou mexendo"
1. Abra [ControladorNavegacao.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/navegacao/ControladorNavegacao.kt#L15-L18).
2. Mude `rotaInicial` para `RotaAtelie.Principal` e `abaInicial` para a aba desejada (ex: `AbaNavegacao.CALCULADORA`).
3. Em [NavegadorCentral.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/navegacao/NavegadorCentral.kt#L58-L64), comente temporariamente o `else if (controlador.rotaAtual == RotaAtelie.Principal && !estadoLogin.usuarioAutenticado)` para ele não te jogar de volta para o Login caso não haja usuário salvo.

### Caso 2: "Quero mudar a ordem, o nome ou o ícone das 5 abas inferiores"
1. Abra [RotaAtelie.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/navegacao/RotaAtelie.kt#L23-L32).
2. Reordene as linhas do `enum class AbaNavegacao` ou troque o texto/ícone. A `BarraNavegacaoAtelie` em `NavegadorCentral.kt` percorre `AbaNavegacao.entries` automaticamente.

### Caso 3: "Quero mudar a ordem das seções da Calculadora ou esconder alguma seção"
1. Abra [TelaCalculadoraOrcamento.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/calculadora/TelaCalculadoraOrcamento.kt#L116-L131).
2. As 6 seções são chamadas em linhas seguidas dentro da `LazyColumn`:
   ```kotlin
   item { SecaoDadosCliente(estado = estado, aoEnviarEvento = aoEnviarEvento) }
   item { SecaoCatalogoProduto(estado = estado, aoEnviarEvento = aoEnviarEvento) }
   item { SecaoMedidasEFaixas(estado = estado, aoEnviarEvento = aoEnviarEvento) }
   item { SecaoPrazosETaxas(estado = estado, aoEnviarEvento = aoEnviarEvento) }
   item { SecaoComparativoFornecedores(estado = estado, aoEnviarEvento = aoEnviarEvento) }
   ```
   Basta trocar a ordem dos blocos `item { ... }` ou entrar no componente específico (em `apresentacao/telas/calculadora/componentes/`) para editar os campos internos.

### Caso 4: "Quero alterar como o preço ou prazo é calculado quando mexo na Calculadora"
1. **Regras de tela e preenchimento automático de data de entrega:** ficam em [CalculadoraOrcamentoViewModel.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/calculadora/CalculadoraOrcamentoViewModel.kt) (função `recalcularOrcamento()` e `atualizarDataEntregaAutomatica()`).
2. **Fórmula matemática pura (faixas de quantidade, m², taxas, comparativo de fornecedores):** fica em `:sharedLogic`, dentro de [ProdutoRepositorioImpl.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/dados/repositorio/ProdutoRepositorioImpl.kt) e [TaxaRepositorioImpl.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/dados/repositorio/TaxaRepositorioImpl.kt).

### Caso 5: "Quero criar uma Tela / Aba Nova do zero"
1. **Crie a pasta da tela** em `sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/telas/minhatela/` com:
   - `MinhaTelaEstadoUi.kt` (`data class MinhaTelaEstadoUi` + `sealed interface MinhaTelaEventoUi`)
   - `MinhaTelaViewModel.kt`
   - `TelaMinhaTela.kt` (com `@Preview` separado no final)
2. **Registre o ViewModel no Koin** em [ModuloApresentacao.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/di/ModuloApresentacao.kt):
   ```kotlin
   viewModelOf(::MinhaTelaViewModel)
   ```
3. **Adicione a aba** no `enum class AbaNavegacao` em [RotaAtelie.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/navegacao/RotaAtelie.kt).
4. **Conecte no centralizador** em [NavegadorCentral.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/apresentacao/navegacao/NavegadorCentral.kt):
   - Instancie `val minhaTelaViewModel: MinhaTelaViewModel = koinViewModel()`
   - Colete `val estadoMinhaTela by minhaTelaViewModel.estado.collectAsStateWithLifecycle()`
   - Adicione o ramo `AbaNavegacao.MINHA_TELA -> TelaMinhaTela(...)` dentro do `when (controlador.abaAtual)`.

### Caso 6: "Quero mudar as cores (Vinho, Dourado, Fundo), bordas ou fontes do app inteiro"
- **Paleta hexadecimal e tokens:** [Cores.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/nucleo/tema/Cores.kt)
- **Esquema Material 3 (Light / Dark e Shapes):** [TemaAtelie.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/nucleo/tema/TemaAtelie.kt)
- **Fontes (`Bricolage Grotesque` e `Plus Jakarta Sans`):** [Tipografia.kt](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/nucleo/tema/Tipografia.kt)
