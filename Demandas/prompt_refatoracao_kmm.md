# PROMPT PARA REFATORAÇÃO DE APLICAÇÃO KMP/KMM E UI (JETPACK COMPOSE/COMPOSE MULTIPLATFORM)

---

## 🎯 OBJETIVO
Você atuará como um **Arquiteto de Software Mobile e Especialista em Kotlin Multiplatform (KMP) / Compose Multiplatform**. Seu objetivo é refatorar a aplicação atual, corrigindo acoplamentos na arquitetura, separando as camadas do projeto (`sharedLogic` e `sharedUI`), genérica de componentes, aplicando o padrão **MVVM**, além de implementar novas regras de negócio e melhorias de UI/UX.

---

## 🏗️ 1. CONTEXTO DE ARQUITETURA (KMP / MVVM)

### Problemas Atuais Identificados:
- Lógica de navegação misturada com componentes no `sharedUI`.
- Camada de Domínio (Domain) e Repositórios (Data) alocados no mesmo arquivo.
- Componentes de interface rígidos, sem parametrização genérica para reuso.
- Falta de separação clara entre **State**, **Intent/Action** e **UI**.

### Diretrizes de Reestruturação Solicitadas:
1. **Camada de Domínio (`sharedLogic`)**:
   - Isolamento de **UseCases/Interactors** (ex: `CalculateStickersSheetUseCase`, `LockBudgetPricingUseCase`).
   - Declaração clara de interfaces de **Repositories** separadas das suas implementações de **Data Sources**.
   - Definição clara dos modelos de domínio (Entities).

2. **Camada de UI e Apresentação (`sharedUI`)**:
   - Padrão **MVVM (Model-View-ViewModel)** puro com observação de estado via `StateFlow` / `UIState`.
   - Transferência de toda a lógica de roteamento/navegação para `NavHost` centralizado ou handlers desacoplados.
   - Refatoração dos Composables para torná-los **Stateless** (recebem dados via parâmetro e emitem eventos/lambdas).
   - Componentização modular e genérica (Botões, Inputs, Cards de Demandas, etc.).

---

## 📐 2. NOVAS REGRAS DE NEGÓCIO E CALCULO DE INSUMOS

### A. Calculadora de Adesivos por Folha A3:
- **Entrada**: 
  - Tamanho do adesivo (ex: $6\text{ cm}$).
  - Quantidade total de adesivos desejada pelo cliente.
- **Lógica de Cálculo**:
  - Calcular quantos adesivos de tamanho $X$ cabem em uma folha formato A3 ($420\text{ mm} \times 297\text{ mm}$ ou área útil definida).
  - Calcular a quantidade mínima de folhas A3 necessárias para suprir a demanda total de adesivos:
    $$\text{Folhas Necessárias} = \left\lceil \frac{\text{Quantidade Total de Adesivos}}{\text{Adesivos por Folha A3}} \right\rceil$$
- **Saída**: Exibir no orçamento/resumo a quantidade total de adesivos, número de folhas A3 consumidas e o cálculo total.

### B. Prazos e Custos de Produção:
- **Simplificação de Prazos**: Remover a distinção separada de "Tempo de Produção" no resultado final da entrega. O **Prazo de Entrega** passa a ser único e representa o prazo total consolidado (Prazo Normal).
- **Congelamento de Valores Únicos de Orçamento**: Quando um orçamento/pedido for **Fechado/Aprovado**, o sistema deve snapshotar (gravar) os valores unitários dos custos de produção do momento do fechamento, garantindo que alterações futuras na tabela de preços padrão não alterem orçamentos já fechados.

---

## 🖼️ 3. ALTERAÇÕES DE TERMINOLOGIA E UI/UX

1. **Alteração de Nomenclatura**:
   - Substituir em toda a aplicação o termo **"Ateliê"** por **"Demanda"** (e derivados, ex: *Gaveta de Demandas*, *Fila de Demandas*).

2. **Tela Inicial & Gaveta de Demandas (Bottom Sheet)**:
   - As **Demandas Prioritárias** na Home devem ser movidas para dentro de um **Draggable Bottom Sheet** (Gaveta arrastável de baixo para cima).
   - O Bottom Sheet deve possuir estados (Recolhido, Expansão Parcial e Expandido Total).

3. **Navegação e Bottom Bar**:
   - Redesenhar a **Bottom Bar**: retirar visual poluído, remover numerações dos atalhos de fluxo de produção.
   - Utilizar **ícones neutros, modernos e limpos** (ex: estilo Material 3 / Lucide minimalist).

---

## 📝 4. ENTREGÁVEIS ESPERADOS NA SUA RESPOSTA

Por favor, elabore um documento técnico em formato **Markdown (`.md`)** contendo:

1. **Diagrama e Explicação da Arquitetura MVVM em KMP**:
   - Estrutura detalhada de diretórios do `sharedLogic` e `sharedUI`.
   - Fluxo de dados unidirecional (UDF): `UI State` $\leftarrow$ `ViewModel` $\leftarrow$ `UseCase` $\leftarrow$ `Repository`.

2. **Guia e Roteiro de Refatoração dos Composables**:
   - Passo a passo de como desacoplar a navegação da UI.
   - Exemplos de refatoração para transformar componentes rígidos em componentes genéricos/reutilizáveis.
   - Especificação do `Draggable Bottom Sheet` para as Demandas Prioritárias na Tela Inicial.

3. **Implementação em Código (Snippets Kotlin / Compose Multiplatform)**:
   - Exemplo da regra do cálculo de folhas A3 (`CalculateStickersSheetUseCase`).
   - Exemplo do ViewModel e UIState para gerenciar o cálculo e as Demandas.
   - Exemplo do layout da Bottom Bar moderna e limpa.