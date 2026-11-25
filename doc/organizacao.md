# Organização do Projeto - Calculadora & Gestão de Orçamentos de Gráfica (Demanda-01)

> **Status do Projeto:** Refatoração Completa `Demanda-01.md` implementada (Glassmorphism, Tipografia Local, Motor de Cálculo Dinâmico, Catálogo Unificado, Kanban, Tabela Editável e Painel CRUD).  
> **Plataforma Principal:** Android (Kotlin Multiplatform / Compose Multiplatform).  
> **Arquitetura:** Clean Architecture + MVVM + StateFlow (100% Offline-First com Room v3 e Koin).  
> **Data:** Novembro de 2026

---

## 1. Visão Geral da Arquitetura

```
[ :androidApp ]  (Ponto de entrada Android, CalculadoraApplication, MainActivity)
      │
      ▼
  [ :sharedUI ]   (Design System Glassmorphism, Fontes Locais, 5 Telas Operacionais, OrderViewModel)
      │
      ▼
[ :sharedLogic ] (Catálogo Unificado Room v3, BudgetCalculatorUseCase, SeedRepository, GraficaRepository)
```

---

## 2. Estrutura de Diretórios e Arquivos Principais

```
calculadora/
├── androidApp/
│   └── src/main/kotlin/br/com/grafica/calculadora/
│       ├── CalculadoraApplication.kt       # Inicializa Koin (appModule + uiModule)
│       └── MainActivity.kt                 # Carrega o App() com GraficaTheme
│
├── sharedLogic/
│   └── src/commonMain/kotlin/br/com/grafica/calculadora/
│       ├── data/
│       │   ├── Entities.kt                 # ProductEntity, SupplierEntity, ProductSupplierOfferEntity,
│       │   │                               # PriceTierEntity, AdditionalFeeEntity, OrderEntity, AppSettingsEntity
│       │   ├── GraficaDaos.kt              # DAOs relacionais para Catálogo, Fornecedores, Faixas, Taxas e Pedidos
│       │   ├── BuritiDatabase.kt           # RoomDatabase v3 com suporte completo a todas as entidades
│       │   ├── SeedRepository.kt           # Carga inicial obrigatória (GM PRINT, BURITI PRINTS, Taxas e Pedidos)
│       │   └── GraficaRepository.kt        # Repositório com CRUD completo e persistência relacional
│       ├── dominio/
│       │   ├── OrcamentoModels.kt          # ProductionMode, PricingMode, FeeType, PipelineStage, OrderPaymentStatus
│       │   ├── BudgetCalculatorUseCase.kt  # Motor de Cálculo (Faixas de Qtd, m², Comparativo e Taxas)
│       │   └── Clientes.kt                 # TypeConverters para o Room
│       └── di/
│           └── AppModule.kt                # Koin DI para Database, DAOs, SeedRepository, UseCase e Repository
│
├── sharedUI/
│   └── src/commonMain/
│       ├── composeResources/font/          # Fontes locais importadas:
│       │   ├── bricolage_grotesque_*.ttf   # Bricolage Grotesque (Regular, Medium, SemiBold, Bold, ExtraBold)
│       │   └── plus_jakarta_sans_*.ttf     # Plus Jakarta Sans (Regular, Medium, SemiBold, Bold)
│       └── kotlin/br/com/grafica/calculadora/
│           ├── App.kt                      # Scaffold principal com NavigationBar de 5 abas e OrderEditDialog
│           ├── ui/
│           │   ├── theme/
│           │   │   └── Theme.kt            # Paleta (#540C3F, #1F0417, #FFFFFF, #E3AE01, #2ECC71) + Typography
│           │   ├── components/
│           │   │   ├── GlassComponents.kt  # GlassCard, StudioRevenueGlassCard, MonthlyGoalWineCard, Pills
│           │   │   └── OrderEditDialog.kt  # Modal de Edição Direta completa com recálculo em tempo real
│           │   ├── viewmodel/
│           │   │   └── OrderViewModel.kt   # Estado único OrderUiState gerenciando todas as 5 frentes
│           │   └── screen/
│           │       ├── DashboardScreen.kt          # Tela 1: Faturamento Glass + Meta Circular + Demandas Ativas
│           │       ├── KanbanScreen.kt             # Tela 2: Quadro do Ateliê (Orçamento, Arte, Produção, Pronto)
│           │       ├── PedidosTableScreen.kt       # Tela 3: Registro Tabular de Pedidos Editável com filtros e soma
│           │       ├── NovaCalculadoraScreen.kt    # Tela 4: Calculadora Dinâmica com Comparativo e Comprovante
│           │       └── PainelGerencialScreen.kt    # Tela 5: Painel Gerencial CRUD (Insumos, Fornecedores, Faixas, Taxas)
│           └── di/
│               └── UiModule.kt             # Koin DI registrando OrderViewModel
│
└── doc/
    ├── organizacao.md                      # Este documento de organização atualizado
    └── resumo_conversa_arquitetura.md      # Histórico de arquitetura
```
