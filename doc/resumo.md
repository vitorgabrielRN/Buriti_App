1. Diagnóstico dos Módulos e Erros de Build
1.1. Módulos Configurados

No arquivo settings.gradle.kts, há 3 módulos Gradle ativos:

:androidApp (Ponto de entrada Android, com.android.application)
:sharedLogic (Módulo KMP contendo banco Room, domínio e resquícios de ViewModel/State)
:sharedUI (Módulo KMP/Compose contendo telas, tema, ViewModels e App.kt)
(Diretório webApp/ existe na raiz, mas está fora do settings.gradle.kts e desconectado do build Gradle).
1.2. Problemas Estruturais e de Configuração Encontrados
Target js desativado + código-fonte jsMain órfão:
Em sharedLogic/build.gradle.kts, o target js { ... } está comentado porque o androidx.room:room-runtime:2.8.1 não suporta Kotlin/JS, porém o arquivo Platform.js.kt e o optIn("kotlin.js.ExperimentalJsExport") permanecem ativos no arquivo.
Os módulos :sharedLogic e :sharedUI compilam apenas para android (não há targets desktopMain/jvmMain ou iosMain configurados).
Violação de fronteira entre :sharedLogic e :sharedUI (dependências implícitas e pacotes errados):
CadastroViewModel.kt reside dentro de :sharedLogic usando androidx.lifecycle.ViewModel e SavedStateHandle, embora lifecycle-viewmodel não esteja declarado nas dependências de :sharedLogic (compila apenas de forma transitiva via AndroidX/Compose).
Clientes.kt mistura Entidade Room (@Entity(tableName = "cliente")) e @TypeConverter dentro da camada dominio, acoplando o domínio puro ao Room e ao kotlinx.serialization.json.
BudgetCalculatorUseCase.kt importa diretamente classes da camada data (AdditionalFeeEntity, PriceTierEntity, ProductEntity, ProductSupplierOfferEntity).
Banco de Dados com Esquema Duplicado (Legado vs. Novo):
BuritiDatabase.kt (versão 3) mantém simultaneamente tabelas legadas (cliente, product_presets, quotes) e as tabelas novas (clients, products, suppliers, product_supplier_offers, price_tiers, additional_fees, orders, app_settings).
Pacotes Inconsistentes e Mistura EN/PT-BR ("Portuglês"):
Pacotes em PascalCase e misturados: br.com.grafica.calculadora.Repository, .State, .viewModel, .ui.componentes, .ui.components, .dominio, .data.
Entidades e propriedades misturadas: ClienteDao vs ClientDao, OrcamentoModels.kt contendo enums em inglês (ProductCategory, DeliveryMethod, PaymentTerms, PipelineStage, OrderPaymentStatus), GraficaRepository com métodos em inglês (saveOrderWithClient, observeAllProducts).
Código Morto / Duplicado / Obsoleto:
Modelos duplicados: Clientes, Pedidos, Produto, TipoPapel (formatoPapel, Gramatura) e ProductPresetEntity/QuoteEntity coexistem sem uso ou em paralelo com ProductEntity/OrderEntity.
Telas e ViewModels obsoletos (não ligados ao fluxo atual do App.kt):
FormularioUsuario.kt (840 linhas) — usado apenas por AppPreview().
OrcamentosScreen.kt (438 linhas).
CalculadoraViewModel.kt (354 linhas) e OrcamentosViewModel.kt (91 linhas) — ainda registrados em UiModule.kt, mas substituídos por OrderViewModel.
FormularioPapel.kt (99 linhas) e CadastroCliente.kt (63 linhas).
Greeting.kt, GreetingUtil.kt, Platform.kt, Platform.android.kt, Platform.js.kt (template KMP).
CadastroState.kt, CadastroViewModel.kt, ClienteRepository.kt e ClienteDao.kt.
2. Mapa de Decomposição das God Classes
Classe / Arquivo Atual	Linhas	Problemas e Violações de SOLID	Novas Classes em PT-BR após Divisão
PainelGerencialScreen.kt	1026	Mistura 4 sub-telas CRUD completas (Produtos, Fornecedores, Faixas, Taxas e Meta) com estados locais de formulário e layout no mesmo arquivo.	TelaCadastros.kt + 4 subcomponentes coesos (SecaoCadastroProdutos.kt, SecaoCadastroFornecedores.kt, SecaoCadastroFaixasPreco.kt, SecaoTaxasEMeta.kt) + CadastrosViewModel.kt + CadastrosEstadoUi.kt + CadastrosEventoUi.kt
NovaCalculadoraScreen.kt	909	Arquivo monolítico com 6 seções de formulário, busca local de produtos, cards de comparativo, tabela de faixas, taxas e comprovante acoplados a ProductEntity / OrderUiState.	TelaCalculadoraOrcamento.kt + subcomponentes (SecaoDadosCliente.kt, SecaoCatalogoProduto.kt, SecaoComparativoFornecedores.kt, SecaoMedidasEFaixas.kt, SecaoPrazosETaxas.kt, CartaoComprovanteOrcamento.kt) + CalculadoraOrcamentoViewModel.kt + CalculadoraEstadoUi.kt + CalculadoraEventoUi.kt
SeedRepository.kt	873	God Seeder: mistura configurações, taxas, 29 produtos com ofertas da GM PRINT / BURITI PRINTS e pedidos de exemplo num único método gigante (ensureSeedData).	SeedInicialBancoDados.kt orquestrando 3 seeders focados: SeedTaxasEConfiguracoes.kt, SeedCatalogoEFornecedores.kt, SeedClientesEPedidos.kt
FormularioUsuario.kt	840	Tela legada gigante da v1 sem uso ativo no App.kt.	Removida (código morto substituído por TelaCalculadoraOrcamento).
OrderViewModel.kt	657	Maior God ViewModel: gerencia simultaneamente Dashboard, Kanban, Tabela de Pedidos, Calculadora, Autocomplete, Editor de Pedido, CRUD de Produtos, Fornecedores, Ofertas, Faixas, Taxas e Configurações.	Dividido em 5 ViewModels UDF especializados:
• InicioViewModel.kt (InicioEstadoUi, InicioEventoUi)
• QuadroAtelieViewModel.kt (QuadroAtelieEstadoUi, QuadroAtelieEventoUi)
• RegistroPedidosViewModel.kt (RegistroPedidosEstadoUi, RegistroPedidosEventoUi)
• CalculadoraOrcamentoViewModel.kt (CalculadoraEstadoUi, CalculadoraEventoUi)
• CadastrosViewModel.kt (CadastrosEstadoUi, CadastrosEventoUi)
OrcamentosScreen.kt	438	Tela legada da v1 desconectada da navegação principal.	Removida (substituída por TelaRegistroPedidos.kt).
OrderEditDialog.kt	394	Modal de edição contém lógica de recálculo financeiro (recalculatedSubtotal) diretamente no Composable e acopla OrderEntity.	DialogoEdicaoPedido.kt (apenas UI/eventos) delegando recálculo para GerarOrcamentoCompletoUseCase via RegistroPedidosViewModel.
DashboardScreen.kt	370	Consome OrderUiState monolítico e mistura cards de ação e card de demanda no mesmo arquivo.	TelaInicio.kt + CartaoAtalhosProducao.kt + CartaoDemandaAtiva.kt
BudgetCalculatorUseCase.kt	365	UseCase acumula 3 responsabilidades (comparativo, cálculo por faixa/m², cálculo de taxas) e depende diretamente das entidades Room (ProductEntity, PriceTierEntity, etc.).	Dividido em 5 UseCases puros em Kotlin (sem Room):
• CalcularItemOrcamentoUseCase.kt
• CompararFornecedoresUseCase.kt
• CalcularTaxasAdicionaisUseCase.kt
• GerarOrcamentoCompletoUseCase.kt
• AvancarEtapaPedidoUseCase.kt
CalculadoraViewModel.kt	354	ViewModel legado da v1 sem uso no App.kt.	Removido (substituído por CalculadoraOrcamentoViewModel.kt).
GlassComponents.kt	340	Mistura formatador de moeda, componente genérico GlassCard, cards de negócio (StudioRevenueGlassCard, MonthlyGoalWineCard) e pílulas de status.	Separado em nucleo:
• FormatadorMoeda.kt (formatarMoedaBr)
• CartaoGlass.kt (CartaoGlass)
• BadgesStatus.kt (PilulaStatusPagamento, PilulaEtapaPipeline)
• CartaoFaturamentoEstudio.kt e CartaoMetaMensal.kt
PedidosTableScreen.kt	333	Tela tabular + linhas + chips no mesmo arquivo consumindo OrderUiState global.	TelaRegistroPedidos.kt + TabelaPedidosComponentes.kt
KanbanScreen.kt	294	Quadro + colunas + cards de demanda no mesmo arquivo consumindo OrderEntity.	TelaQuadroAtelie.kt + ColunaEtapaKanban.kt + CartaoDemandaKanban.kt
GraficaRepository.kt	294	Repositório God Interface/Impl com 25+ métodos cobrindo Clientes, Presets, Quotes, Produtos, Fornecedores, Ofertas, Faixas, Taxas, Pedidos e Configurações.	Dividido em 5 interfaces em dominio.repositorio + 5 impls em dados.repositorio:
• ClienteRepositorio / ClienteRepositorioImpl
• ProdutoRepositorio / ProdutoRepositorioImpl
• FornecedorRepositorio / FornecedorRepositorioImpl
• TaxaRepositorio / TaxaRepositorioImpl
• PedidoRepositorio / PedidoRepositorioImpl
Entities.kt & GraficaDaos.kt	274 / 171	Todas as 10 entidades em 1 arquivo e 10 DAOs em 1 arquivo, com nomes em inglês e misturados com tabelas legadas.	Entidades e DAOs separados por agregado em dados.local.entidade e dados.local.dao, com Mappers em dados.local.mapeador e nomes de tabelas/colunas padronizados.
3. Árvore de Diretórios e Tabela Completa de Tradução (EN → PT-BR)
3.1. Estratégia de Banco de Dados (Room) na Tradução

Como já existe o BuritiDatabase (versão 3) com fallbackToDestructiveMigration(true) e carga inicial automática (SeedInicialBancoDados), a estratégia recomendada é:

Incrementar a versão do banco para versão 4 (BancoDadosAtelie), removendo as tabelas legadas mortas (cliente, product_presets, quotes) e padronizando as tabelas em português (clientes, produtos, fornecedores, ofertas_fornecedor, faixas_preco, taxas_adicionais, pedidos, configuracoes_atelie) com @ColumnInfo explícitos em PT-BR e Mappers paraDominio() / paraEntidade() isolando Room da camada dominio.
Caso haja necessidade de preservar dados já gravados em dispositivos de produção antes da entrega final, pode ser adicionada uma Migration(3, 4) renomeando as colunas/tabelas; caso contrário, fallbackToDestructiveMigration(true) recria o banco limpo e executa o SeedInicialBancoDados automaticamente.
3.2. Árvore de Diretórios Alvo (PT-BR)
text
calculadora/
├── androidApp/
│   └── src/main/kotlin/br/com/grafica/calculadora/
│       ├── AplicacaoCalculadora.kt              (antigo CalculadoraApplication.kt)
│       └── AtividadePrincipal.kt                (antigo MainActivity.kt)
│
├── sharedLogic/
│   └── src/
│       ├── androidMain/kotlin/br/com/grafica/calculadora/dados/local/
│       │   └── ConstrutorBancoDados.android.kt  (antigo DatabaseBuilder.android.kt)
│       └── commonMain/kotlin/br/com/grafica/calculadora/
│           ├── dominio/
│           │   ├── modelo/
│           │   │   ├── Cliente.kt
│           │   │   ├── Produto.kt
│           │   │   ├── Fornecedor.kt
│           │   │   ├── OfertaFornecedor.kt
│           │   │   ├── FaixaPreco.kt
│           │   │   ├── TaxaAdicional.kt
│           │   │   ├── ItemOrcamento.kt
│           │   │   ├── Orcamento.kt
│           │   │   ├── Pedido.kt
│           │   │   ├── ConfiguracaoEstudio.kt
│           │   │   └── EnumeradoresDominio.kt   (ModalidadeProducao, ModoPrecificacao, TipoTaxa,
│           │   │                                 EtapaPipeline, StatusPagamento, MetodoEntrega, PrioridadePrazo)
│           │   ├── repositorio/
│           │   │   ├── ClienteRepositorio.kt
│           │   │   ├── ProdutoRepositorio.kt
│           │   │   ├── FornecedorRepositorio.kt
│           │   │   ├── TaxaRepositorio.kt
│           │   │   └── PedidoRepositorio.kt
│           │   └── casouso/
│           │       ├── CalcularItemOrcamentoUseCase.kt
│           │       ├── CompararFornecedoresUseCase.kt
│           │       ├── CalcularTaxasAdicionaisUseCase.kt
│           │       ├── GerarOrcamentoCompletoUseCase.kt
│           │       └── AvancarEtapaPedidoUseCase.kt
│           ├── dados/
│           │   ├── local/
│           │   │   ├── BancoDadosAtelie.kt      (antigo BuritiDatabase.kt)
│           │   │   ├── ConstrutorBancoDados.kt  (antigo DatabaseBuilder.kt)
│           │   │   ├── ConversoresRoom.kt       (extraído de Clientes.kt)
│           │   │   ├── entidade/
│           │   │   │   ├── ClienteEntidade.kt
│           │   │   │   ├── ProdutoEntidade.kt
│           │   │   │   ├── FornecedorEntidade.kt
│           │   │   │   ├── OfertaFornecedorEntidade.kt
│           │   │   │   ├── FaixaPrecoEntidade.kt
│           │   │   │   ├── TaxaAdicionalEntidade.kt
│           │   │   │   ├── PedidoEntidade.kt
│           │   │   │   └── ConfiguracaoEstudioEntidade.kt
│           │   │   ├── dao/
│           │   │   │   ├── ClienteDao.kt
│           │   │   │   ├── ProdutoDao.kt
│           │   │   │   ├── FornecedorDao.kt
│           │   │   │   ├── TaxaAdicionalDao.kt
│           │   │   │   └── PedidoDao.kt
│           │   │   └── mapeador/
│           │   │       ├── ClienteMapeador.kt
│           │   │       ├── CatalogoMapeador.kt
│           │   │       ├── TaxaMapeador.kt
│           │   │       └── PedidoMapeador.kt
│           │   ├── seed/
│           │   │   ├── SeedInicialBancoDados.kt
│           │   │   ├── SeedTaxasEConfiguracoes.kt
│           │   │   ├── SeedCatalogoEFornecedores.kt
│           │   │   └── SeedClientesEPedidos.kt
│           │   └── repositorio/
│           │       ├── ClienteRepositorioImpl.kt
│           │       ├── ProdutoRepositorioImpl.kt
│           │       ├── FornecedorRepositorioImpl.kt
│           │       ├── TaxaRepositorioImpl.kt
│           │       └── PedidoRepositorioImpl.kt
│           └── di/
│               └── ModuloLogica.kt              (antigo AppModule.kt)
│
└── sharedUI/
    └── src/commonMain/kotlin/br/com/grafica/calculadora/
        ├── Aplicativo.kt                        (antigo App.kt)
        ├── di/
        │   └── ModuloApresentacao.kt            (antigo UiModule.kt)
        ├── nucleo/
        │   ├── tema/
        │   │   ├── Cores.kt
        │   │   ├── Tipografia.kt
        │   │   └── TemaAtelie.kt                (antigo Theme.kt)
        │   ├── componentes/
        │   │   ├── CartaoGlass.kt
        │   │   ├── CartaoFaturamentoEstudio.kt
        │   │   ├── CartaoMetaMensal.kt
        │   │   ├── BadgesStatus.kt
        │   │   └── CamposTexto.kt
        │   └── utilitarios/
        │       └── FormatadorMoeda.kt
        └── apresentacao/
            └── telas/
                ├── inicio/
                │   ├── TelaInicio.kt
                │   ├── InicioViewModel.kt
                │   ├── InicioEstadoUi.kt
                │   ├── InicioEventoUi.kt
                │   └── componentes/
                │       ├── CartaoAtalhosProducao.kt
                │       └── CartaoDemandaAtiva.kt
                ├── quadro/
                │   ├── TelaQuadroAtelie.kt
                │   ├── QuadroAtelieViewModel.kt
                │   ├── QuadroAtelieEstadoUi.kt
                │   ├── QuadroAtelieEventoUi.kt
                │   └── componentes/
                │       ├── ColunaEtapaKanban.kt
                │       └── CartaoDemandaKanban.kt
                ├── pedidos/
                │   ├── TelaRegistroPedidos.kt
                │   ├── RegistroPedidosViewModel.kt
                │   ├── RegistroPedidosEstadoUi.kt
                │   ├── RegistroPedidosEventoUi.kt
                │   └── componentes/
                │       ├── TabelaPedidosComponentes.kt
                │       └── DialogoEdicaoPedido.kt
                ├── calculadora/
                │   ├── TelaCalculadoraOrcamento.kt
                │   ├── CalculadoraOrcamentoViewModel.kt
                │   ├── CalculadoraEstadoUi.kt
                │   ├── CalculadoraEventoUi.kt
                │   └── componentes/
                │       ├── SecaoDadosCliente.kt
                │       ├── SecaoCatalogoProduto.kt
                │       ├── SecaoComparativoFornecedores.kt
                │       ├── SecaoMedidasEFaixas.kt
                │       ├── SecaoPrazosETaxas.kt
                │       └── CartaoComprovanteOrcamento.kt
                └── cadastros/
                    ├── TelaCadastros.kt
                    ├── CadastrosViewModel.kt
                    ├── CadastrosEstadoUi.kt
                    ├── CadastrosEventoUi.kt
                    └── componentes/
                        ├── SecaoCadastroProdutos.kt
                        ├── SecaoCadastroFornecedores.kt
                        ├── SecaoCadastroFaixasPreco.kt
                        └── SecaoTaxasEMeta.kt
3.3. Tabela Completa de Tradução (DE → PARA)
Origem (Atual EN / Misto)	Destino (Padronizado PT-BR)
Entrypoint & DI	
CalculadoraApplication	AplicacaoCalculadora
MainActivity	AtividadePrincipal
App() / StudioTab	Aplicativo() / AbaNavegacao
appModule / uiModule	moduloLogica / moduloApresentacao
Domínio & Modelos	
ClientEntity	Cliente (Domínio) / ClienteEntidade (Room)
ProductEntity	Produto (Domínio) / ProdutoEntidade (Room)
SupplierEntity	Fornecedor (Domínio) / FornecedorEntidade (Room)
ProductSupplierOfferEntity	OfertaFornecedor / OfertaFornecedorEntidade
PriceTierEntity	FaixaPreco / FaixaPrecoEntidade
AdditionalFeeEntity	TaxaAdicional / TaxaAdicionalEntidade
OrderEntity	Pedido / PedidoEntidade
AppSettingsEntity	ConfiguracaoEstudio / ConfiguracaoEstudioEntidade
SupplierComparisonOption	OpcaoComparativoFornecedor
BudgetCalculationResult	Orcamento (com ItemOrcamento)
ProductionMode (PROPRIA, TERCEIRIZADA)	ModalidadeProducao (PROPRIA, TERCEIRIZADA)
PricingMode (TIERED_QUANTITY, FIXED_UNIT_OR_BATCH, AREA_M2)	ModoPrecificacao (FAIXA_QUANTIDADE, UNITARIO_OU_LOTE, AREA_M2)
FeeType (URGENCY, EMERGENCY, DELIVERY, NIGHT_SHIFT, CUSTOM)	TipoTaxa (URGENCIA, EMERGENCIA, ENTREGA, ADICIONAL_NOTURNO, PERSONALIZADA)
PipelineStage (ORCAMENTO, ARTE, PRODUCAO, PRONTO)	EtapaPipeline (ORCAMENTO, ARTE, PRODUCAO, PRONTO)
OrderPaymentStatus (A_COBRAR, MEIO_A_MEIO, PAGO_100)	StatusPagamento (A_COBRAR, MEIO_A_MEIO, PAGO_100)
DeliveryMethod (RETIRADA, ENTREGA)	MetodoEntrega (RETIRADA, ENTREGA)
DeadlinePriorityMode (NORMAL, URGENCIA, EMERGENCIA)	PrioridadePrazo (NORMAL, URGENCIA, EMERGENCIA)
Casos de Uso & Repositórios	
BudgetCalculatorUseCase.buildSupplierComparison	CompararFornecedoresUseCase
BudgetCalculatorUseCase (cálculo item)	CalcularItemOrcamentoUseCase
BudgetCalculatorUseCase (taxas)	CalcularTaxasAdicionaisUseCase
BudgetCalculatorUseCase.calculateBudget	GerarOrcamentoCompletoUseCase
OrderViewModel.advanceOrderStage	AvancarEtapaPedidoUseCase
GraficaRepository / GraficaRepositoryImpl	ClienteRepositorio, ProdutoRepositorio, FornecedorRepositorio, TaxaRepositorio, PedidoRepositorio (+ *Impl)
SeedRepository	SeedInicialBancoDados
BuritiDatabase	BancoDadosAtelie
Núcleo & Telas (UI)	
GraficaTheme / BrandPrimary / BrandTertiary	TemaAtelie / CorVinhoPrimaria / CorDouradoTerciaria
GlassCard / StudioRevenueGlassCard / MonthlyGoalWineCard	CartaoGlass / CartaoFaturamentoEstudio / CartaoMetaMensal
PaymentStatusPill / PipelineStagePill	PilulaStatusPagamento / PilulaEtapaPipeline
formatCurrencyBr	formatarMoedaBr
DashboardScreen	TelaInicio
KanbanScreen	TelaQuadroAtelie
PedidosTableScreen	TelaRegistroPedidos
OrderEditDialog	DialogoEdicaoPedido
NovaCalculadoraScreen	TelaCalculadoraOrcamento
PainelGerencialScreen	TelaCadastros
4. Roteiro de Execução Seguro em Etapas

Cada etapa abaixo mantém o projeto compilável ao final da sua execução:

Etapa 1 — Limpeza de Código Morto & Núcleo (Design System):

Remover arquivos órfãos e legados não utilizados (Platform.js.kt, Greeting*, CadastroState, CadastroViewModel, ClienteRepository, ClienteDao, FormularioUsuario, OrcamentosScreen, CalculadoraViewModel, OrcamentosViewModel, FormularioPapel, CadastroCliente).
Criar o pacote nucleo (Cores, Tipografia, TemaAtelie, CartaoGlass, BadgesStatus, FormatadorMoeda, etc.) e atualizar referências.
Validação: .\gradlew.bat :androidApp:assembleDebug.

Etapa 2 — Camada de Domínio Pura (PT-BR):

Criar os modelos de domínio puros em dominio.modelo, interfaces de repositório em dominio.repositorio e os 5 casos de uso de responsabilidade única em dominio.casouso.
Validação: .\gradlew.bat :sharedLogic:compileAndroidMain.

Etapa 3 — Camada de Dados, Room v4, Mappers e Seed Modularizado:

Criar entidades Room em dados.local.entidade, DAOs por agregado em dados.local.dao, Mappers em dados.local.mapeador, BancoDadosAtelie (v4), SeedInicialBancoDados particionado e as 5 implementações de repositório + ModuloLogica.
Validação: .\gradlew.bat :sharedLogic:compileAndroidMain.

Etapa 4 — Camada de Apresentação UDF (ViewModels, Estados, Eventos e Telas) + Entrypoint:

Implementar em sequência os pares UDF (TelaInicio, TelaQuadroAtelie, TelaRegistroPedidos, TelaCalculadoraOrcamento, TelaCadastros) quebrados em subcomponentes pequenos, atualizar Aplicativo.kt, ModuloApresentacao.kt, AplicacaoCalculadora.kt e AtividadePrincipal.kt.
Validação final: .\gradlew.bat :androidApp:assembleDebug + .\gradlew.bat test.

Aguardando sua confirmação ("Aprovado, pode executar a Etapa 1") para iniciar a refatoração segura!

executa

Todas as 4 etapas da refatoração (limpeza de código morto, núcleo de design system, camada de domínio pura, camada de dados Room v4 + Mappers + Seed modularizado e as 5 telas em padrão UDF) foram aplicadas. O Gradle está finalizando a compilação completa (:androidApp:assembleDebug).

A refatoração arquitetural completa, decomposição das God Classes e tradução integral para Português (PT-BR) foram concluídas e validadas com sucesso!

Verificação de Build e Testes
.\gradlew.bat :androidApp:assembleDebug: BUILD SUCCESSFUL (22s).
.\gradlew.bat test: BUILD SUCCESSFUL (3s).
Resumo do Que Foi Executado por Etapa
1. Limpeza de Código Morto & Núcleo (nucleo)
Código morto removido: jsMain/Platform.js.kt, Greeting*, Platform*, CadastroState, CadastroViewModel, ClienteRepository, ClienteDao, FormularioUsuario, OrcamentosScreen, CalculadoraViewModel, OrcamentosViewModel, FormularioPapel, CadastroCliente, além das entidades/tabelas legadas duplicadas.
Novo Design System em PT-BR:
Cores.kt, Tipografia.kt, TemaAtelie.kt
FormatadorMoeda.kt
CartaoGlass.kt, CartaoFaturamentoEstudio.kt, CartaoMetaMensal.kt, BadgesStatus.kt, CamposTexto.kt
2. Camada de Domínio Pura (dominio)

100% livre de Android, Compose ou Room:

Modelos e Enums: EnumeradoresDominio.kt, Cliente.kt, Produto.kt, CatalogoETaxas.kt, OrcamentoEPedido.kt.
Contratos de Repositório: Repositorios.kt (ClienteRepositorio, ProdutoRepositorio, FornecedorRepositorio, TaxaRepositorio, PedidoRepositorio).
5 Casos de Uso (UseCases) desacoplados:
CalcularItemOrcamentoUseCase.kt
CompararFornecedoresUseCase.kt
CalcularTaxasAdicionaisUseCase.kt
GerarOrcamentoCompletoUseCase.kt
AvancarEtapaPedidoUseCase.kt
3. Camada de Dados (dados) — Room v4, Mappers & Seed Modularizado
Banco e Entidades PT-BR: BancoDadosAtelie.kt (v4), ConversoresRoom.kt, entidades em dados/local/entidade/, DAOs separados por agregado em dados/local/dao/ e mappers paraDominio() / paraEntidade() em dados/local/mapeador/.
Seed Modularizado: SeedInicialBancoDados.kt orquestrando SeedTaxasEConfiguracoes, SeedCatalogoEFornecedores (GM PRINT e BURITI PRINTS) e SeedClientesEPedidos.
DI Koin: ModuloLogica.kt.
4. Camada de Apresentação UDF (apresentacao) & Entrypoint
5 Fluxos UDF Isolados (Tela + ViewModel + EstadoUi + EventoUi + subcomponentes):
Início: TelaInicio.kt e InicioViewModel.kt
Quadro do Ateliê (Kanban): TelaQuadroAtelie.kt e QuadroAtelieViewModel.kt
Registro de Pedidos Editável: TelaRegistroPedidos.kt, DialogoEdicaoPedido.kt e RegistroPedidosViewModel.kt
Calculadora de Orçamentos: TelaCalculadoraOrcamento.kt e CalculadoraOrcamentoViewModel.kt
Painel Gerencial (CRUD): TelaCadastros.kt e CadastrosViewModel.kt
Entrypoints & Navegação: Aplicativo.kt, ModuloApresentacao.kt, AplicacaoCalculadora.kt, AtividadePrincipal.kt e AndroidManifest.xml.
compact 
tem como tirar esse usecase e colocar em repositorios? tipo anexar a repositorios já existentes? pq na minha visão desnecssario ter useCase neste projeto. vai atrapalhar quando for para a versão desktop não ter userCase? caso não, pode refatorar para implementar nos repositorios + viewmodel sla e crie as telas de navegation, tipo, Splash etc... um centralizador e tals.

Aguardando a conclusão da compilação (:androidApp:assembleDebug) e dos testes (test) para validar a remoção dos UseCases e a nova estrutura de navegação centralizada com a tela de Splash.

Não atrapalha em nada a versão Desktop. Como toda a lógica de cálculo, comparação de fornecedores, taxas e transição de etapas continua em Kotlin puro dentro de commonMain (nos repositórios e implementações de sharedLogic), o futuro target jvm("desktop") vai reaproveitar exatamente os mesmos repositórios e ViewModels sem precisar de UseCase.

1. Remoção dos UseCase e incorporação aos Repositórios
Pacote removido: dominio/casouso (os 5 arquivos *UseCase.kt foram removidos).
Contratos atualizados em Repositorios.kt:
ProdutoRepositorio: recebeu calcularItemOrcamento(...), compararFornecedores(...) e gerarOrcamentoCompleto(...).
TaxaRepositorio: recebeu calcularTaxasAdicionais(...).
PedidoRepositorio: recebeu avancarEtapaPedido(...).
Implementações atualizadas:
ProdutoRepositorioImpl.kt
TaxaRepositorioImpl.kt
PedidoRepositorioImpl.kt
DI e ViewModels simplificados:
ModuloLogica.kt: removidos os bindings de UseCase e injetado TaxaRepositorio em ProdutoRepositorioImpl.
InicioViewModel.kt, QuadroAtelieViewModel.kt e CalculadoraOrcamentoViewModel.kt agora chamam diretamente os métodos dos repositórios.
2. Navegação Centralizada + Tela de Splash
Rotas e Abas: RotaAtelie.kt define RotaAtelie (Splash, Principal) e AbaNavegacao (substituindo o antigo pacote nucleo/navegation).
Controlador de Navegação: ControladorNavegacao.kt centraliza rotaAtual, abaAtual, histórico de abas (voltarAba()) e atalhos (concluirSplash(), navegarParaAba(), abrirCalculadora(), etc.).
Tela Splash: TelaSplash.kt implementa a abertura com degradê vinho (#1F0417 → #540C3F), destaque dourado (#E3AE01), animação de entrada e @Preview.
Centralizador: NavegadorCentral.kt gerencia a transição animada de TelaSplash para o container principal (Scaffold + NavigationBar + DialogoEdicaoPedido + abas).
Entrypoints corrigidos: Aplicativo.kt e AtividadePrincipal.kt (removido o parâmetro inválido em Aplicativo() e a chamada com erro de sintaxe em AtividadePrincipal).