# Documentação e Resumo da Arquitetura — Calculadora (Mobile Local)

> **Data:** 09/11/2026
> **Projeto:** `br.com.grafica.calculadora`
> **Escopo Inicial:** Foco 100% em Mobile (Android) rodando localmente (offline).

---

## 1. Resumo das Correções de Build e Configuração Inicial

1. **Correção do Opt-In do Compilador Kotlin (`ExperimentalJsExport`):**
   - **Problema:** O arquivo [`Greeting.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/Greeting.kt) usava `@JsExport` em `commonMain`, gerando avisos/problemas de compilação na etapa `:sharedLogic:compileCommonMainKotlinMetadata`.
   - **Solução:** Adicionado `all { languageSettings.optIn("kotlin.js.ExperimentalJsExport") }` no bloco `sourceSets` de [`sharedLogic/build.gradle.kts`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/build.gradle.kts#L34-L37).

2. **Configuração de Execução no Android Studio (`Run Configuration`):**
   - **Problema:** O projeto possuía apenas `.run/webApp.run.xml` configurado por padrão, o que acionava tasks de `npm`/JS ao clicar em Run.
   - **Solução:** Adicionada e selecionada a configuração `androidApp` (`AndroidRunConfigurationType`) em [`.idea/workspace.xml`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/.idea/workspace.xml).

---

## 2. Stack Adicionada no Gradle (Opção A — Completa Local)

Como o projeto utiliza **Kotlin Multiplatform (KMP)** com **Compose Multiplatform**, o **Dagger/Hilt** tradicional (que depende exclusivamente do runtime Android/JVM) foi substituído pelo **Koin**, junto com **Room (KMP/SQLite)** e **Kotlinx Serialization**:

### Bibliotecas Configuradas em [`gradle/libs.versions.toml`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/gradle/libs.versions.toml):
- **Injeção de Dependência (Substituto do Hilt):**
  - `koin-core`, `koin-android`, `koin-compose`, `koin-compose-viewmodel` (`4.1.1`)
- **Banco de Dados Local (Offline SQL):**
  - `androidx-room-runtime`, `androidx-room-compiler` (`2.8.1`)
  - `androidx-sqlite-bundled` (`2.6.0`)
  - Plugin `com.google.devtools.ksp` (`2.3.13`) e `androidx.room` (`2.8.1`)
- **Assincronismo e Serialização JSON:**
  - `kotlinx-coroutines-core` / `kotlinx-coroutines-android` (`1.10.2`)
  - `kotlinx-serialization-json` (`1.9.0`)

---

## 3. Fluxo de Implementação e Arquitetura dos 3 Módulos

```
[ :sharedLogic ]  --->  [ :sharedUI ]  --->  [ :androidApp ]
 (Regras, Cálculos,      (Telas Compose,       (MainActivity,
  Modelos, Dados)         ViewModels, Koin)      APK Android)
```

### A. [`:sharedLogic`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/build.gradle.kts) (Cérebro / Regras de Negócio)
- **Onde fica o código:** `sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/`
- **Responsabilidade:** Modelos de domínio (ex.: `Material`, `Orcamento`, `ItemCalculo`), fórmulas de cálculo da gráfica, serialização e contratos de repositório.
- **Regra de ouro:** Não conhece nada de tela (zero imports de Compose). Se as fórmulas de preço mudarem, você altera **somente aqui**.

### B. [`:sharedUI`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/build.gradle.kts) (Interface Visual, Banco Room e ViewModels)
- **Onde fica o código:** `sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/`
- **Responsabilidade:**
  - Telas em **Compose Multiplatform** (Material 3), começando por [`App.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/App.kt).
  - **ViewModels** (`androidx.lifecycle.ViewModel`) injetados via **Koin** (`koinViewModel()`).
  - **Banco de Dados Room (`@Database`, `@Dao`, `@Entity`)** para persistir insumos, preços e histórico de orçamentos localmente no aparelho.
- **Se o fluxo visual mudar:** Se você quiser mudar o layout ou dividir em abas/telas novas, altera apenas `:sharedUI`, mantendo `:sharedLogic` intacto.

### C. [`:androidApp`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/androidApp/build.gradle.kts) (Ponto de Entrada Android)
- **Onde fica o código:** [`MainActivity.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/androidApp/src/main/kotlin/br/com/grafica/calculadora/MainActivity.kt)
- **Responsabilidade:** Inicializar o contexto Android, o Koin e chamar `setContent { App() }`. Quase nunca precisa ser modificado.

---

## 4. Roteiro ("Norte") de Implementação Mobile

1. **Passo 1 — Modelos e Fórmulas (`:sharedLogic`):**
   - Criar classes de dados (`@Serializable data class ...`) para os insumos e parâmetros do orçamento.
   - Criar a classe `CalculadoraService` / `CalculadoraEngine` com as funções puras de cálculo.
2. **Passo 2 — Persistência Local com Room (`:sharedUI`):**
   - Criar as `@Entity` (ex.: `OrcamentoEntity`, `MaterialEntity`), `@Dao` e `AppDatabase`.
3. **Passo 3 — Injeção de Dependência com Koin (`:sharedUI`):**
   - Declarar o módulo Koin (`val appModule = module { ... }`) provendo o banco Room, repositórios, motor de cálculo e ViewModels.
4. **Passo 4 — ViewModel e Telas Compose (`:sharedUI`):**
   - Criar `CalculadoraViewModel` expondo `StateFlow<CalculadoraUiState>`.
   - Construir o formulário e resumo do orçamento em [`App.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/App.kt).

---

## 5. Implementação da Instanciação do Room + Koin

A estrutura de banco de dados e injeção de dependência foi implementada e compilada com sucesso em `:sharedLogic`:

1. **Módulo `:sharedLogic` (Domínio + Persistência Room KMP + Koin):**
   - [`Clientes.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/dominio/Clientes.kt) e [`Pedidos.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/dominio/Pedidos.kt): Entidade `@Entity(tableName = "cliente")` e conversores de JSON (`Converters`).
   - [`Produto.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/dominio/Produto.kt) e [`TipoPapel.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/dominio/TipoPapel.kt).
   - [`ClienteDao.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/data/ClienteDao.kt): DAO com operações completas (`Flow<List<Clientes>>`, `insert`, `update`, `delete`).
   - [`BuritiDatabase.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/data/BuritiDatabase.kt): Definição do `@Database` e `RoomDatabaseConstructor`.
   - [`DatabaseBuilder.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/data/DatabaseBuilder.kt) e [`DatabaseBuilder.android.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/androidMain/kotlin/br/com/grafica/calculadora/data/DatabaseBuilder.android.kt): Fábrica do banco de dados via SQLite bundled local (`buriti.db`).
   - [`AppModule.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedLogic/src/commonMain/kotlin/br/com/grafica/calculadora/di/AppModule.kt): Módulo Koin provendo `BuritiDatabase` e `ClienteDao`.

2. **Módulo `:androidApp` (Inicialização):**
   - [`CalculadoraApplication.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/androidApp/src/main/kotlin/br/com/grafica/calculadora/CalculadoraApplication.kt): Subclasse de `Application` registrada no [`AndroidManifest.xml`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/androidApp/src/main/AndroidManifest.xml) inicializando `startKoin { ... }` e o contexto do banco local.

---

## 6. Como Funcionam os `@Preview` no Compose Multiplatform

Para que o painel **Split / Design (Preview)** do Android Studio funcione em arquivos dentro de `:sharedUI/src/commonMain` (como [`FormularioUsuario.kt`](file:///c:/Users/malaq/AndroidStudioProjects/calculadora/sharedUI/src/commonMain/kotlin/br/com/grafica/calculadora/ui/screen/FormularioUsuario.kt)), é necessário seguir 3 regras:

1. **Usar o import unificado do Compose Multiplatform:**
   ```kotlin
   import androidx.compose.ui.tooling.preview.Preview
   ```
2. **Funções `@Preview` NÃO podem ter parâmetros obrigatórios sem valor padrão:**
   - Se o seu Composable recebe `label: String`, `paddingValues: PaddingValues`, `scope: CoroutineScope`, você deve:
     - Definir valores padrão (`label: String = "Cadastro"`, `paddingValues: PaddingValues = PaddingValues(0.dp)`), **OU**
     - Criar uma função separada sem parâmetros (ex.: `FormularioDescricaoPreview()`) que chama o seu Composable passando valores de teste.
3. **O projeto precisa compilar sem erros:**
   - O Android Studio não renderiza nenhum Preview se houver erro de compilação em qualquer módulo dependente (como o arquivo `BuritiDatabase.android.kt` que havia sido gerado manualmente pela IDE com `TODO()`, conflitando com o arquivo gerado automaticamente pelo KSP do Room).

---

## 7. Comandos Úteis (Terminal Windows)

- **Compilar APK Debug (Mobile):**
  ```powershell
  .\gradlew.bat :androidApp:assembleDebug
  ```
- **Rodar Testes Unitários da Lógica:**
  ```powershell
  .\gradlew.bat :sharedLogic:testAndroidHostTest
  ```

