# KOIN — Controle Financeiro Pessoal para Android

> Aplicativo Android nativo, escrito em Kotlin, para registrar receitas e despesas, acompanhar o saldo, definir metas de economia e limites de gasto por categoria, tudo armazenado localmente no aparelho.

Projeto acadêmico da disciplina de **Programação Móvel**. Código-fonte em `projeto-koin-v2/`.

---

## Sumário

1. [Nome do projeto](#1-nome-do-projeto)
2. [Apresentação e visão geral](#2-apresentação-e-visão-geral)
3. [Objetivo da aplicação](#3-objetivo-da-aplicação)
4. [Problema que o sistema busca resolver](#4-problema-que-o-sistema-busca-resolver)
5. [Principais funcionalidades](#5-principais-funcionalidades)
6. [Tecnologias utilizadas](#6-tecnologias-utilizadas)
7. [Arquitetura e organização do projeto](#7-arquitetura-e-organização-do-projeto)
8. [Documentação detalhada da pasta `data`](#8-documentação-detalhada-da-pasta-data)
9. [Documentação detalhada da pasta `dialog`](#9-documentação-detalhada-da-pasta-dialog)
10. [Documentação detalhada da pasta `response`](#10-documentação-detalhada-da-pasta-response)
11. [Documentação detalhada da pasta `utils`](#11-documentação-detalhada-da-pasta-utils)
12. [Documentação detalhada da pasta `security`](#12-documentação-detalhada-da-pasta-security)
13. [Documentação detalhada das Activities](#13-documentação-detalhada-das-activities)
14. [Documentação detalhada de `res/drawable`](#14-documentação-detalhada-de-resdrawable)
15. [Documentação detalhada de `res/layout`](#15-documentação-detalhada-de-reslayout)
16. [Fluxo de funcionamento da aplicação](#16-fluxo-de-funcionamento-da-aplicação)
17. [Segurança e proteção de dados](#17-segurança-e-proteção-de-dados)
18. [Valor de negócio e benefícios ao cliente](#18-valor-de-negócio-e-benefícios-ao-cliente)
19. [Considerações sobre manutenção e evolução](#19-considerações-sobre-manutenção-e-evolução)
20. [Conclusão](#20-conclusão)

---

## 1. Nome do projeto

**KOIN** — *"Sua vida financeira em equilíbrio."* (slogan exibido nas telas de login, cadastro e perfil).

> **Observação importante sobre o nome:** "KOIN" é o **nome do aplicativo**. O projeto **não utiliza a biblioteca Koin de injeção de dependências**: não existe nenhuma dependência `io.insert-koin` no `build.gradle.kts` nem qualquer `startKoin`/`module` no código. O acesso ao banco de dados é feito por um *singleton* próprio, `AppDatabase.getDatabase(context)`. Esta documentação descreve somente o que está implementado.

## 2. Apresentação e visão geral

O KOIN é um aplicativo de finanças pessoais que funciona **100% offline**. Cada usuário cria uma conta local, faz login e passa a registrar suas movimentações financeiras (receitas e despesas). A partir desses registros o aplicativo calcula o saldo, lista o histórico com busca e filtros, gera relatórios por período com gráfico de pizza e permite planejar metas de economia e limites mensais por categoria.

Todas as informações ficam em um banco **SQLite gerenciado pela biblioteca Room**, no próprio aparelho. Não há servidor, API, nem conexão com a internet no código.

| Item | Valor |
|---|---|
| Pacote / `applicationId` | `com.example.koin` |
| Versão do app | `1.0` (`versionCode = 1`) |
| `minSdk` / `targetSdk` / `compileSdk` | 24 / 37 / 37 |
| Linguagem | Kotlin (interface em XML, sem ViewBinding: `findViewById`) |
| Banco de dados | Room 2.8.4 sobre SQLite, arquivo `controle_gastos_database`, versão 11 |
| Telas (Activities) | 11 |
| Janelas inferiores (BottomSheets) | 4 |

## 3. Objetivo da aplicação

Oferecer ao usuário uma ferramenta simples, rápida e privada para:

- registrar entradas e saídas de dinheiro em poucos toques;
- saber, a qualquer momento, quanto dinheiro está **realmente disponível** (já descontado o que foi reservado em metas);
- entender para onde o dinheiro está indo, por período e por categoria;
- planejar objetivos (viagem, celular, reserva de emergência) com prazo e acompanhamento de progresso;
- controlar o quanto se pretende gastar por categoria em cada mês.

## 4. Problema que o sistema busca resolver

Pessoas que não controlam receitas e despesas costumam perder a noção do saldo real, gastar acima do planejado e adiar objetivos por falta de acompanhamento. Planilhas e anotações dispersas são trabalhosas e propensas a erro.

O KOIN centraliza tudo em um único aplicativo: o cadastro é feito em uma tela dedicada (`NovaMovimentacaoActivity`), os totais são calculados automaticamente pelo banco (consultas `SUM` nos DAOs), os valores são guardados como **inteiros em centavos** (evitando erros de arredondamento de ponto flutuante) e a interface destaca de imediato se o saldo está positivo, negativo ou zerado.

## 5. Principais funcionalidades

| Área | Funcionalidade | Onde está implementada |
|---|---|---|
| Conta | Cadastro com validação de e-mail e senha mínima de 8 caracteres | `CadastroActivity`, `Validacao.kt` |
| Conta | Login com verificação de senha por hash | `LoginActivity`, `GerenciadorSeguranca` |
| Conta | Alterar senha (exige a senha atual) | `EditarSenhaActivity` |
| Conta | Ver dados pessoais, sair da conta, **deletar a conta** com remoção de todos os dados | `PerfilActivity`, `VerInformcoesContaActivity` |
| Início | Saldo disponível, receitas e despesas do mês, últimas 3 movimentações, botão para ocultar valores | `MenuActivity` |
| Movimentações | Cadastro de despesa ou receita com valor mascarado, descrição, categoria e data | `NovaMovimentacaoActivity` |
| Movimentações | Histórico agrupado por data, filtro (todas/receitas/despesas), busca por nome ou categoria, **exclusão com confirmação** | `MovimentacaoActivity`, `RenderMovimentacoes.kt`, `TransacaoDao` |
| Relatórios | Totais por período (mês, trimestre, ano), indicador de saldo positivo/negativo, gráfico de pizza por categoria com legenda | `RelatoriosActivity` |
| Relatórios | Orçamento mensal: limites por categoria e barra de uso | `GerenciarLimitesBottomSheet`, `OrcamentoDao` |
| Metas | Criar, editar, excluir metas com ícone, valor-alvo e prazo opcional; guardar e retirar valores; resumo geral | `MetaActivity`, `NovaMetaBottomSheet`, `EditarMetaBottomSheet`, `GuardarMetaBottomSheet` |
| Metas | Sugestão de quanto guardar por mês até o prazo | `PrazoMeta.kt` |
| Sessão | Proteção das telas internas contra sessão perdida | `Sessao.kt` (`exigirSessao`) |

## 6. Tecnologias utilizadas

Somente tecnologias efetivamente declaradas em `app/build.gradle.kts` e `gradle/libs.versions.toml`:

| Tecnologia | Versão | Uso no projeto |
|---|---|---|
| **Kotlin** | via AGP 9.3.3 | Linguagem de todo o código |
| **Android SDK** | min 24, target/compile 37 | Plataforma; `Activity`, `Fragment`, `AlertDialog`, `PopupMenu` |
| **Gradle (Kotlin DSL)** | wrapper do projeto | Build (`build.gradle.kts`, `settings.gradle.kts`, `libs.versions.toml`) |
| **KSP** | 2.3.4 | Processador de anotações do Room (gera as implementações de DAO e banco) |
| **Room (runtime + ktx + compiler)** | 2.8.4 | Persistência local sobre **SQLite**; funções `suspend`, `withTransaction` |
| **Kotlin Coroutines** (via `activity-ktx`/`room-ktx`) | — | `lifecycleScope.launch`, `Dispatchers.Default/IO`, `Job` |
| **AndroidX AppCompat** | 1.8.0 | `AppCompatActivity`, `AlertDialog` |
| **AndroidX Activity KTX** | 1.13.0 | `enableEdgeToEdge()` |
| **AndroidX Core KTX** | 1.19.0 | `ViewCompat`, `WindowInsetsCompat` |
| **ConstraintLayout** | 2.2.2 | Layout da tela inicial (`activity_main.xml`) |
| **Material Components** | 1.14.0 | `BottomSheetDialogFragment`, `MaterialDatePicker`, `MaterialAlertDialogBuilder`, tema `Material3` |
| **MPAndroidChart** | v3.1.0 (JitPack) | Gráfico de pizza (`PieChart`) em Relatórios |
| **XML** | — | Todas as interfaces em `res/layout` e recursos em `res/drawable` |
| JUnit / Espresso | 4.13.2 / 3.7.0 | Declarados, mas só existem os testes-modelo vazios do Android Studio |

**Como trabalham juntas:** o XML descreve as telas; as Activities (Kotlin) ligam os elementos visuais aos DAOs do Room; o Room (via KSP) transforma interfaces anotadas em acesso SQLite; as corrotinas executam as consultas fora da thread principal e devolvem o resultado para a interface; o Material fornece componentes padronizados (janelas inferiores, seletor de data).

## 7. Arquitetura e organização do projeto

### 7.1 Estrutura de pastas

```
projeto-koin-v2/
├── build.gradle.kts · settings.gradle.kts · gradle/libs.versions.toml
└── app/
    ├── build.gradle.kts
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/example/koin/
        │   ├── MainActivity · LoginActivity · CadastroActivity · MenuActivity
        │   ├── MovimentacaoActivity · NovaMovimentacaoActivity · MetaActivity
        │   ├── RelatoriosActivity · PerfilActivity · EditarSenhaActivity
        │   ├── VerInformcoesContaActivity                       (Activities)
        │   ├── data/            Entidades, DAOs, banco, conversores, sessão
        │   │   └── response/    Resultados de consultas (não são tabelas)
        │   ├── dialog/          BottomSheets (metas e limites)
        │   ├── security/        Hash de senha
        │   └── utils/           Formatação, validação, navegação, renderização
        └── res/
            ├── drawable/        59 recursos (vetores, shapes, PNGs)
            ├── layout/          17 layouts XML
            ├── values/ values-night/ xml/ (temas, cores, regras de backup)
```

### 7.2 Padrão arquitetural realmente implementado

O projeto segue uma arquitetura **centrada em Activities com acesso direto aos DAOs**:

```
 Interface (XML)  ──►  Activity / BottomSheet (Kotlin)  ──►  DAO (Room)  ──►  SQLite
        ▲                       │   ▲                              │
        └───── atualiza views ──┘   └───── utils / security ───────┘
```

- **Não há** camada de *Repository*, *ViewModel* nem injeção de dependências. As telas criam o acesso com `AppDatabase.getDatabase(context)` e chamam os DAOs dentro de `lifecycleScope.launch`.
- A **sessão do usuário** (id, nome, e-mail) fica no objeto `DadosEntrePaginas`, em memória.
- A pasta `utils` concentra lógica reutilizável (formatação, validação, navegação, renderização de listas) e `security` isola o hash de senha, evitando duplicação nas telas.
- As telas internas compartilham uma **barra de navegação inferior** (Início, Movimentos, "+", Relatórios, Metas) repetida em `activity_menu`, `activity_movimentacao`, `activity_relatorios` e `activity_meta`.

### 7.3 Decisões técnicas relevantes

- **Valores monetários em centavos (`Long`)**: `Despesas.valorDespesa`, `Receita.valor`, `Meta.valorAlvo`, `Meta.valorGuardado` e `Orcamento.valorLimite`. A formatação para reais é feita só na exibição (`FormatadorMonetario`).
- **Saldo disponível** = total de receitas − total de despesas − total guardado nas metas (`MenuActivity.onResume`). Guardar em uma meta não cria uma "despesa" falsa: o valor reservado é descontado por cálculo.
- **Isolamento por usuário**: todas as tabelas de dados trazem `idFkUsuario`, e as consultas de leitura, alteração e exclusão filtram por ele.
- **Banco versão 11** com `fallbackToDestructiveMigration(true)`: ao mudar a estrutura de tabelas o banco é recriado (adequado ao contexto acadêmico; ver [seção 19](#19-considerações-sobre-manutenção-e-evolução)).

---

## 8. Documentação detalhada da pasta `data`

Pasta: `app/src/main/java/com/example/koin/data/`

É a **camada de persistência** do aplicativo. Reúne as 5 entidades (tabelas), os 6 DAOs, a configuração do banco Room, o conversor de datas e o objeto que mantém a sessão. **Não existem repositórios** nem fontes de dados remotas: as telas consomem os DAOs diretamente, e todas as operações CRUD são feitas em SQLite local.

A organização por responsabilidade (uma entidade e um DAO por assunto) mantém as consultas SQL em um único lugar, fora das telas. Isso facilita corrigir uma regra de cálculo uma só vez e reduz inconsistências entre telas.

**Mapa das tabelas**

| Entidade | Tabela | Campos principais | DAO |
|---|---|---|---|
| `Usuario` | `usuario` | `id`, `nome`, `email` (único), `senha` (hash) | `UsuarioDao` |
| `Despesas` | `despesas` | `id`, `nomeCategoria`, `valorDespesa`, `descricao`, `dataDespesaCadastrada`, `idFkUsuario` | `DespesasDao` |
| `Receita` | `receita` | `id`, `nomeCategoria`, `valor`, `descricao`, `dataReceitaCadastrada`, `idFkUsuario` | `ReceitaDao` |
| `Meta` | `meta` | `id`, `nome`, `valorAlvo`, `valorGuardado`, `icone`, `cor`, `dataCriacao`, `prazo`, `idFkUsuario` | `MetaDao` |
| `Orcamento` | `orcamento` | `id`, `nomeCategoria`, `valorLimite`, `idFkUsuario` (único por usuário + categoria) | `OrcamentoDao` |

> Os campos `idFkUsuario` **não são chave estrangeira declarada** (não há `@ForeignKey`): o vínculo com o usuário é por convenção. Por isso a exclusão de conta remove os dados de cada tabela manualmente (ver `PerfilActivity.deletandoConta`).

### 8.1 `AppData.kt`

- **Responsabilidade:** declara o banco de dados Room (`AppDatabase`), reunindo as cinco entidades (`Usuario`, `Despesas`, `Receita`, `Orcamento`, `Meta`) e expondo os seis DAOs (`usuarioDao`, `despesasDao`, `receitaDao`, `transacaoDao`, `orcamentoDao`, `metaDao`). É o ponto único de acesso aos dados.
- **Funcionamento:** a anotação `@Database` define `version = 11` e `exportSchema = false`; `@TypeConverters(Converters::class)` habilita a conversão de `Date`. O `companion object` implementa um *singleton* com `@Volatile INSTANCE` e bloco `synchronized`, construindo o banco `controle_gastos_database` com `Room.databaseBuilder(context.applicationContext, ...)` e `fallbackToDestructiveMigration(true)`.
- **Integração:** chamado por todas as Activities e BottomSheets por `AppDatabase.getDatabase(this)`. Usa o contexto da aplicação, o que evita vazar uma Activity.
- **Valor de negócio:** garante que todo o app leia e grave no mesmo banco, o que mantém as telas coerentes entre si (um lançamento feito em uma tela aparece imediatamente nas outras).
- **Segurança/confiabilidade:** o *singleton* sincronizado evita corrida na criação do banco. Ponto de atenção: a migração destrutiva apaga os dados quando a versão muda, e o arquivo do banco **não é criptografado**.

### 8.2 `Converters.kt`

- **Responsabilidade:** ensinar o Room a gravar e ler o tipo `java.util.Date`, que o SQLite não suporta nativamente.
- **Funcionamento:** dois métodos anotados com `@TypeConverter`: `deTimestampParaDate(value: Long?)` cria um `Date` a partir de milissegundos, e `deDateParaTimestamp(date: Date?)` devolve `date?.time`. Ambos aceitam `null`, o que permite campos opcionais como `Meta.prazo`.
- **Integração:** registrado em `AppDatabase` e usado por `Despesas.dataDespesaCadastrada`, `Receita.dataReceitaCadastrada`, `Meta.dataCriacao`, `Meta.prazo` e por `UltimaTransacao.dataTransacao`. Também permite comparar datas diretamente nas consultas de período (`>= :dataInicio AND < :dataFim`).
- **Valor de negócio:** datas guardadas como número permitem filtrar por mês, trimestre e ano e ordenar o histórico corretamente.

### 8.3 `DadosEntrePaginas.kt`

- **Responsabilidade:** manter os dados do usuário logado (`id: Long?`, `nome`, `email`) enquanto o aplicativo está aberto, para que qualquer tela saiba de quem são os dados exibidos.
- **Funcionamento:** é um `object` (singleton) com três propriedades e a função `limpar()`, que devolve `id = null` e textos vazios. O `LoginActivity` preenche; `direcionandoLimpandoSessao` chama `limpar()` ao sair.
- **Integração:** lido em praticamente todas as telas e BottomSheets para obter `(DadosEntrePaginas.id ?: 0L).toInt()`. `exigirSessao()` (em `Sessao.kt`) usa o valor para decidir se a tela pode abrir.
- **Valor de negócio:** evita pedir login a cada tela e separa os dados de cada usuário.
- **Segurança:** guarda **apenas** id, nome e e-mail (nunca a senha). Por ficar só em memória, é perdido se o sistema encerrar o processo; nesse caso `exigirSessao()` redireciona ao login. Não há token nem expiração por tempo.

### 8.4 `Usuario.kt`

- **Responsabilidade:** entidade da tabela `usuario`, com a conta de quem usa o aplicativo.
- **Funcionamento:** `data class` com `id` (chave primária autoincrementada), `nome`, `email` (`@ColumnInfo(name = "email")`) e `senha`. Declara um **índice único** em `email` (`Index(value = ["email"], unique = true)`), e o campo `senha` guarda o hash gerado por `GerenciadorSeguranca`, nunca a senha digitada.
- **Integração:** criada em `CadastroActivity`, lida por `LoginActivity` (via `buscarPorEmail`) e atualizada em `EditarSenhaActivity`.
- **Valor de negócio:** garante uma conta por e-mail e permite que cada pessoa tenha seus próprios dados no mesmo aparelho.
- **Segurança:** o índice único impede cadastros duplicados (a violação é tratada com `SQLiteConstraintException` no cadastro). O formato do campo `senha` é `iteracoes:sal:hash`.

### 8.5 `UsuarioDao.kt`

- **Responsabilidade:** operações de persistência da conta: buscar, inserir, atualizar senha e deletar.
- **Funcionamento:** `buscarPorEmail(emailPassado)` (`SELECT * ... WHERE email = :emailPassado LIMIT 1`, retorna `Usuario?`), `buscarSenhaPorId(usuarioId)` (retorna apenas o hash), `inserir(usuario)` (`@Insert`), `atualizarSenha(novaSenha, usuarioId)` e `deletar(usuarioId)`. Todas são `suspend` e usam parâmetros vinculados (`:nome`), não concatenação de texto.
- **Integração:** `LoginActivity` e `CadastroActivity` (autenticação e criação), `EditarSenhaActivity` (confere a senha atual e grava a nova) e `PerfilActivity` (exclusão da conta).
- **Valor de negócio:** concentra o ciclo de vida da conta em cinco operações simples.
- **Segurança:** o login não compara senhas em SQL: o DAO devolve o hash e a verificação é feita em código por `GerenciadorSeguranca.verificar`. Como as consultas usam parâmetros vinculados, entradas como `' OR 1=1` não alteram o SQL.

### 8.6 `Despesas.kt`

- **Responsabilidade:** entidade da tabela `despesas`, que representa uma saída de dinheiro.
- **Funcionamento:** `id`, `nomeCategoria`, `valorDespesa: Long` (em **centavos**), `descricao: String?`, `dataDespesaCadastrada: Date = Date()` e `idFkUsuario: Int`. A data tem valor padrão de "agora".
- **Integração:** criada em `NovaMovimentacaoActivity` e consultada pelos DAOs para totais, relatórios e histórico (`TransacaoDao`).
- **Valor de negócio:** a categoria permite saber onde o dinheiro é gasto; a descrição opcional dá contexto ("Compra no supermercado").
- **Segurança/integridade:** o valor inteiro em centavos evita erros de arredondamento.

### 8.7 `DespesasDao.kt`

- **Responsabilidade:** gravar, excluir e totalizar despesas.
- **Funcionamento:** `cadastrarDespesa` (`@Insert`); `totalDespesasPeriodo(idPassado, dataInicio, dataFim): Double` (`SUM` com `COALESCE` e intervalo `>= inicio AND < fim`); `totalPorCategoriaPeriodo(...)` (agrupa por `nomeCategoria` e devolve `List<TotalCategoria>`); `totalDespesas(idUsuario): Long` (total de todo o histórico); `excluirDespesa(idDespesa, idUsuario)` (`DELETE ... WHERE id AND idFkUsuario`, devolve linhas afetadas); `excluirTodasDoUsuario(idUsuario)`.
- **Integração:** `NovaMovimentacaoActivity` (cadastro), `MenuActivity` (totais do mês e saldo), `RelatoriosActivity` e `GerenciarLimitesBottomSheet` (totais por categoria), `GuardarMetaBottomSheet` (cálculo de saldo disponível), `MovimentacaoActivity` (exclusão) e `PerfilActivity` (limpeza ao deletar a conta).
- **Valor de negócio:** os totais são calculados pelo banco, o que mantém Início e Relatórios sempre em acordo.
- **Segurança:** a exclusão exige o `idUsuario` junto com o id da despesa: um usuário não consegue apagar o registro de outro. Os intervalos usam `<` no fim, o que evita contar o primeiro instante do mês seguinte.

### 8.8 `Receita.kt`

- **Responsabilidade:** entidade da tabela `receita`, que representa uma entrada de dinheiro.
- **Funcionamento:** espelha `Despesas`: `id`, `nomeCategoria`, `valor: Long` (centavos), `descricao: String? = null`, `dataReceitaCadastrada: Date = Date()` e `idFkUsuario`.
- **Integração:** criada em `NovaMovimentacaoActivity`; lida por `ReceitaDao` e `TransacaoDao`.
- **Valor de negócio:** separa claramente o que entra do que sai, base do cálculo de saldo.
- **Segurança/integridade:** valor em centavos (`Long`) pela mesma razão da despesa.

### 8.9 `ReceitaDao.kt`

- **Responsabilidade:** gravar, excluir e totalizar receitas.
- **Funcionamento:** `cadastrarReceita` (`@Insert`), `excluirReceita(idReceita, idUsuario)`, `totalReceitasPeriodo(...): Double` (por intervalo de datas), `totalReceitas(idUsuario): Long` (todo o histórico) e `excluirTodasDoUsuario`.
- **Integração:** `NovaMovimentacaoActivity`, `MenuActivity` (receita do mês e saldo), `RelatoriosActivity` (receita e saldo do período), `GuardarMetaBottomSheet` (saldo disponível), `MovimentacaoActivity` (exclusão) e `PerfilActivity`.
- **Valor de negócio:** alimenta os cartões de receita e o saldo do usuário.
- **Segurança:** exclusão condicionada ao dono do registro, como em `DespesasDao`.

### 8.10 `TransacaoDao.kt`

- **Responsabilidade:** oferecer **uma única listagem** que mistura receitas e despesas, com filtro por tipo, busca por texto e limite de linhas.
- **Funcionamento:** a função `listar(user_id, tipo, busca, limite)` executa uma consulta com `UNION ALL` entre `receita` e `despesas`, padronizando as colunas (`id`, `nomeCategoria`, `valor`, `descricao`, `idFkUsuario`, `data_transacao`, `tipo`). Sobre o resultado aplica: `(:tipo = 'todas' OR tipo = :tipo)`, busca por `nomeCategoria` **ou** `descricao` com `LIKE '%' || :busca || '%'`, `ORDER BY data_transacao DESC, id DESC` e `LIMIT :limite` (o valor `-1` significa "sem limite" no SQLite). Retorna `List<UltimaTransacao>`.
- **Integração:** `MenuActivity` (últimas 3), `MovimentacaoActivity` (top 3 e lista completa conforme o filtro) e `RenderMovimentacoes.kt` (desenha o resultado).
- **Valor de negócio:** uma só consulta alimenta a tela inicial, o histórico, os filtros e a busca, o que reduz duplicação e mantém a ordenação consistente.
- **Segurança:** o texto digitado na busca entra como parâmetro vinculado, não como parte do SQL.

### 8.11 `Meta.kt`

- **Responsabilidade:** entidade da tabela `meta`, o objetivo de economia do usuário.
- **Funcionamento:** `nome`, `valorAlvo` e `valorGuardado` (centavos), `icone` (emoji em texto), `cor` (`"verde"`, `"laranja"` ou `"azul"`), `dataCriacao`, `prazo: Date?` (opcional) e `idFkUsuario`.
- **Integração:** criada por `NovaMetaBottomSheet`, editada por `EditarMetaBottomSheet`, alterada por `GuardarMetaBottomSheet` e desenhada por `MetaActivity` com apoio de `TemaMeta.kt` e `PrazoMeta.kt`.
- **Valor de negócio:** transforma um sonho em número acompanhável (quanto falta, em que data).
- **Integridade:** `valorGuardado` começa em `0` e só é alterado por `MetaDao.ajustarValorGuardado`.

### 8.12 `MetaDao.kt`

- **Responsabilidade:** CRUD das metas e dos valores reservados.
- **Funcionamento:** `listar(idUsuario)` (ordenada por `id`), `contar`, `totalGuardado` (soma de todos os `valorGuardado`), `inserir`, `atualizar` (nome, alvo, ícone e prazo), `ajustarValorGuardado(id, idUsuario, delta)` com `SET valorGuardado = MAX(0, valorGuardado + :delta)`, `excluir` e `excluirTodasDoUsuario`.
- **Integração:** `MetaActivity` (listar/excluir), `NovaMetaBottomSheet` (`contar` para escolher a cor e `inserir`), `EditarMetaBottomSheet` (`atualizar`), `GuardarMetaBottomSheet` (`ajustarValorGuardado` e `totalGuardado`) e `MenuActivity` (saldo).
- **Valor de negócio:** permite guardar e retirar dinheiro de uma meta sem mexer nos lançamentos de receita e despesa.
- **Segurança/integridade:** toda alteração filtra por `idFkUsuario`, e o `MAX(0, ...)` impede que o valor guardado fique negativo, mesmo se a tela falhar na validação.

### 8.13 `Orcamento.kt`

- **Responsabilidade:** entidade da tabela `orcamento`, o limite mensal de gasto de uma categoria.
- **Funcionamento:** `nomeCategoria`, `valorLimite` (centavos) e `idFkUsuario`. Declara um **índice único composto** `(idFkUsuario, nomeCategoria)`: cada usuário tem no máximo um limite por categoria.
- **Integração:** gravada por `GerenciarLimitesBottomSheet` e usada por `RelatoriosActivity` para calcular o percentual usado.
- **Valor de negócio:** estabelece o teto de gasto que o usuário se comprometeu a respeitar.
- **Integridade:** o índice único impede limites duplicados para a mesma categoria.

### 8.14 `OrcamentoDao.kt`

- **Responsabilidade:** listar, salvar e remover limites por categoria.
- **Funcionamento:** `listarOrcamentos(idUsuario)` (ordem alfabética), `salvarOrcamento` (`@Insert(onConflict = REPLACE)`: grava ou substitui o limite existente), `excluirPorCategoria(idUsuario, nomeCategoria)` e `excluirTodasDoUsuario`.
- **Integração:** `GerenciarLimitesBottomSheet` (dentro de `withTransaction`), `RelatoriosActivity` (`listarOrcamentos`) e `PerfilActivity` (limpeza).
- **Valor de negócio:** o usuário define e ajusta limites rapidamente, e a limpeza de um limite (campo vazio) o remove.
- **Integridade:** o `REPLACE` junto do índice único garante um só limite por categoria.

---

## 9. Documentação detalhada da pasta `dialog`

Pasta: `app/src/main/java/com/example/koin/dialog/`

Reúne as quatro janelas inferiores (`BottomSheetDialogFragment`, do Material Components) usadas na parte de **Metas** e **Orçamento**. Todas usam o tema `Koin.BottomSheetDialog` (definido em `res/values/themes.xml`), abrem **totalmente expandidas** (`skipCollapsed = true`, `STATE_EXPANDED`) e ajustam o layout quando o teclado aparece (`SOFT_INPUT_ADJUST_RESIZE`).

**Como se comunicam com as telas:** quando uma operação termina com sucesso, o BottomSheet chama `parentFragmentManager.setFragmentResult(...)` com uma chave própria, e a Activity que o abriu registrou um `setFragmentResultListener` para a mesma chave e recarrega a lista. Assim, a tela atualiza sem que o BottomSheet conheça a Activity.

| BottomSheet | Chave de resultado | Quem escuta |
|---|---|---|
| `NovaMetaBottomSheet` | `meta_salva` | `MetaActivity` |
| `EditarMetaBottomSheet` | `meta_editada` | `MetaActivity` |
| `GuardarMetaBottomSheet` | `meta_valor_atualizado` | `MetaActivity` |
| `GerenciarLimitesBottomSheet` | `limites_salvos` | `RelatoriosActivity` |

**Proteções comuns às quatro janelas:** conferem se existe usuário logado (`idUsuario == 0` mostra "Sessão expirada. Faça login novamente."); desabilitam o botão de confirmação enquanto gravam (evita toque duplo); só mexem na tela se ainda estiverem anexadas (`isAdded`); e tratam exceções com mensagem ao usuário.

### 9.1 `NovaMetaBottomSheet.kt`

- **Finalidade:** criar uma nova meta de economia.
- **Funcionamento:** usa o layout `bottom_sheet_nova_meta`. O usuário informa o nome, o valor-alvo (campo com **máscara monetária** `#,##0.00` em pt-BR, limitado a 12 dígitos), o **prazo opcional** (componente `CampoPrazo`) e escolhe um entre 12 ícones em emoji (`ICONES`: 🎯 📱 ✈️ 🛡️ 🏠 🚗 🎓 🎁 💻 💍 🏖️ 💰). O ícone escolhido ganha o destaque `bg_icone_meta_selecionado`. Valida: nome obrigatório, valor maior que zero e no máximo `9_999_999_999` centavos. A cor da meta **alterna automaticamente** entre `verde`, `laranja` e `azul` (`CORES_META[contar(idUsuario) % 3]`) e a meta nasce com `valorGuardado = 0`.
- **Eventos:** toque em "Criar meta" ou ação "concluído" do teclado; seleção de ícone; abertura e limpeza do prazo. O ícone e o prazo escolhidos sobrevivem à rotação de tela (`onSaveInstanceState`).
- **Integração:** aberta por `MetaActivity` (botão "+ Criar nova meta"); grava por `MetaDao.inserir`; usa `CampoPrazo` (`PrazoMeta.kt`) e `CORES_META` (`TemaMeta.kt`).
- **Benefício ao usuário:** cria o objetivo sem sair da tela de metas, com campos já formatados e mensagens de erro no próprio campo.
- **Segurança/validação:** rejeita valores vazios, zero, ou acima do limite antes de tocar no banco.

### 9.2 `EditarMetaBottomSheet.kt`

- **Finalidade:** alterar uma meta existente (nome, valor-alvo, prazo e ícone).
- **Funcionamento:** **reaproveita o mesmo layout** `bottom_sheet_nova_meta` e muda os textos para "Editar meta", "Altere o nome, o valor, o prazo ou o ícone. O valor já guardado não muda." e "Salvar alterações". Recebe a meta por `arguments` (fábrica `novo(meta)`), pré-preenche os campos apenas na primeira criação (`savedInstanceState == null`) e grava com `MetaDao.atualizar`. Se nenhuma linha for alterada, informa "Meta não encontrada.".
- **Eventos:** botão "Salvar alterações", seleção de ícone, prazo e ação "concluído" do teclado.
- **Integração:** aberta pelo menu "Editar" do card da meta em `MetaActivity`; devolve `meta_editada`.
- **Benefício ao usuário:** corrige uma meta sem precisar excluí-la e recriá-la, preservando o valor já guardado.
- **Segurança/integridade:** a alteração filtra por `idFkUsuario` e **não toca em `valorGuardado`**: o dinheiro reservado só muda pela janela "Guardar".

### 9.3 `GuardarMetaBottomSheet.kt`

- **Finalidade:** **guardar** dinheiro em uma meta ou **retirar** dinheiro dela.
- **Funcionamento:** layout `bottom_sheet_guardar_meta`, com duas abas ("Guardar" e "Retirar"). Mostra o ícone, o nome e o resumo "R$ X de R$ Y · faltam R$ Z" (ou "meta atingida"). Valida o valor (maior que zero, máximo `9_999_999_999`) e converte o texto para centavos com `BigDecimal` e `RoundingMode.HALF_UP`. Para **retirar**, não permite tirar mais do que está guardado. Para **guardar**, não permite ultrapassar o valor máximo e, **dentro da corrotina**, calcula o saldo disponível (`totalReceitas − totalDespesas − totalGuardado`) e bloqueia se o valor for maior ("Saldo disponível: R$ …" ou "Você não tem saldo disponível para guardar"). Em seguida aplica `ajustarValorGuardado(metaId, idUsuario, delta)`, com `delta` positivo ou negativo. Mensagens finais: "Valor guardado!", "Valor retirado da meta." ou "Parabéns, meta concluída! 🎉".
- **Eventos:** troca de aba, botão de confirmação, ação "concluído" do teclado. O modo escolhido sobrevive à rotação.
- **Integração:** aberta ao tocar no card da meta em `MetaActivity` (fábrica `novo(meta)`); lê `ReceitaDao`, `DespesasDao` e `MetaDao`; devolve `meta_valor_atualizado`.
- **Benefício ao usuário:** impede o erro comum de "guardar dinheiro que não existe" e dá retorno imediato do progresso.
- **Segurança/integridade:** a regra de saldo e a do valor retirado protegem a consistência dos números. A consulta de saldo e a gravação são **duas chamadas separadas** (não ficam na mesma transação).

### 9.4 `GerenciarLimitesBottomSheet.kt`

- **Finalidade:** definir o **limite mensal de gasto por categoria de despesa**.
- **Funcionamento:** layout `bottom_sheet_gerenciar_limites`; para cada categoria de `Categorias.despesa` infla uma linha `item_limite_categoria` com o nome, o texto "Gasto este mês: R$ …" (obtido de `totalPorCategoriaPeriodo` no mês atual) e um campo de valor com máscara monetária. Ao salvar, percorre as linhas: campo **vazio** (ou zero) remove o limite existente; valor inválido mostra "Valor inválido"; acima do máximo mostra "Valor muito alto"; os demais viram `Orcamento`. A gravação é feita dentro de `db.withTransaction { ... }`, de modo que **ou grava tudo, ou nada**.
- **Eventos:** botão "Salvar limites" e ação "concluído" no último campo.
- **Integração:** aberta por `RelatoriosActivity` ("Gerenciar limites"); usa `OrcamentoDao` e `DespesasDao`; devolve `limites_salvos`, o que faz `RelatoriosActivity` recalcular a barra de orçamento.
- **Benefício ao usuário:** define todos os limites em um só lugar, vendo quanto já gastou em cada categoria.
- **Segurança/integridade:** a transação evita ficar com limites pela metade se ocorrer uma falha; os limites são sempre do usuário logado.

---


## 10. Documentação detalhada da pasta `response`

Pasta: `app/src/main/java/com/example/koin/data/response/`

Contém *data classes* que **não são tabelas**: são o formato do resultado de consultas SQL com colunas calculadas ou combinadas. O Room preenche esses objetos automaticamente, ligando o nome de cada coluna do `SELECT` ao nome do atributo. Isso padroniza o que as telas recebem, de modo que a interface não precisa conhecer detalhes do SQL.

### 10.1 `TotalCategoria.kt`

- **Responsabilidade:** carregar o resultado de "quanto foi gasto em cada categoria" no período.
- **Funcionamento:** `data class TotalCategoria(nomeCategoria: String, total_categoria: Double)`. O atributo `total_categoria` tem o mesmo nome do alias `AS total_categoria` da consulta `DespesasDao.totalPorCategoriaPeriodo`, e é por essa igualdade que o Room faz a ligação. O valor está em centavos, mas é do tipo `Double` porque vem de `SUM(... 0.0)`.
- **Integração:** `RelatoriosActivity` usa a lista para montar o gráfico de pizza (`prepararFatias`) e para calcular o uso do orçamento; `GerenciarLimitesBottomSheet` usa para mostrar "Gasto este mês" por categoria.
- **Valor de negócio:** sustenta o gráfico de despesas por categoria e a comparação com os limites.

### 10.2 `UltimaTransacao.kt`

- **Responsabilidade:** representar uma movimentação (receita **ou** despesa) em formato único, para listagens.
- **Funcionamento:** `id`, `nomeCategoria`, `valor: Long` (centavos), `descricao: String?`, `idFkUsuario`, `dataTransacao: Date` (mapeada para a coluna `data_transacao` por `@ColumnInfo`) e `tipo: String` (`"receita"` ou `"despesa"`).
- **Integração:** devolvida por `TransacaoDao.listar`; consumida por `RenderMovimentacoes.kt` (desenha cada linha), `MenuActivity`, `MovimentacaoActivity` (inclusive na exclusão, que usa `id` e `tipo` para escolher o DAO certo, já que as duas tabelas têm sequências de `id` independentes).
- **Valor de negócio:** permite exibir receitas e despesas em uma única linha do tempo, ordenada por data.

---


## 11. Documentação detalhada da pasta `utils`

Pasta: `app/src/main/java/com/example/koin/utils/`

Funções e objetos reaproveitados por várias telas. Cada arquivo resolve **um** problema (formatar dinheiro, validar, navegar, desenhar uma lista), o que evita copiar a mesma lógica em cada Activity e permite corrigir um comportamento em um só ponto.

### 11.1 `Categorias.kt`

- **Responsabilidade:** ser a **lista oficial de categorias** do aplicativo.
- **Funcionamento:** `object Categorias` com `despesa` (9 itens: Alimentação, Transporte, Moradia, Contas, Saúde, Educação, Lazer, Compras, Outros) e `receita` (5 itens: Salário, Freelance, Investimentos, Presente, Outros), e a função `doTipo(tipo)`, que devolve a lista conforme seja `"despesa"` ou outro valor.
- **Onde é usado:** `NovaMovimentacaoActivity` (seletor de categoria) e `GerenciarLimitesBottomSheet` (uma linha por categoria de despesa).
- **Benefício:** mudar ou acrescentar uma categoria exige alterar um só arquivo e mantém as telas coerentes. As categorias padronizadas permitem somar e agrupar corretamente nos relatórios.

### 11.2 `ConverterValor.kt`

- **Responsabilidade:** transformar o texto do campo de valor em **centavos** (`Long`).
- **Funcionamento:** `ConverterValor.paraCentavos(valor: String)` descarta tudo que não é dígito (`R$`, pontos, vírgula, espaços) e converte o restante com `toLongOrNull() ?: 0L`. Funciona junto com a máscara monetária: o texto exibido "R$ 123,45" vira `12345`.
- **Onde é usado:** `NovaMovimentacaoActivity.validarFormulario`.
- **Benefício:** guarda o dinheiro como inteiro e evita problemas de arredondamento. Um texto vazio ou inválido vira `0`, que a tela recusa ("Informe um valor maior que zero").

### 11.3 `CoresCategorias.kt`

- **Responsabilidade:** definir **uma cor para cada categoria** de despesa.
- **Funcionamento:** `corDaCategoria(categoria: String): Int` normaliza o nome (`trim().lowercase()`), aceita com e sem acento ("alimentação"/"alimentacao") e devolve um `Color.rgb(...)`; categorias desconhecidas recebem um cinza.
- **Onde é usado:** `RelatoriosActivity` (fatias do gráfico e bolinhas da legenda).
- **Benefício:** a mesma categoria tem sempre a mesma cor em qualquer período, o que facilita a leitura do gráfico.

### 11.4 `Direcionar.kt`

- **Responsabilidade:** abrir outra tela com uma única chamada.
- **Funcionamento:** `Activity.direcionando(pagAtual, pageProx)` é uma função de extensão que cria `Intent(pagAtual, pageProx)` e chama `startActivity`.
- **Onde é usado:** na navegação de todas as telas (barra inferior, perfil, login → menu etc.).
- **Benefício:** reduz o código repetido de `Intent` e padroniza a navegação.

### 11.5 `FormatadorMonetario.kt`

- **Responsabilidade:** exibir dinheiro no formato brasileiro.
- **Funcionamento:** `object FormatadorMonetario` guarda um `NumberFormat.getCurrencyInstance(Locale("pt", "BR"))` e `formatar(centavos: Long)` converte com `BigDecimal.valueOf(centavos, 2)` (sem passar por `Double`), produzindo, por exemplo, `R$ 1.234,56`. Aceita valores negativos.
- **Onde é usado:** `MenuActivity`, `RelatoriosActivity`, `RenderMovimentacoes.kt`, `PrazoMeta.kt` e `MovimentacaoActivity` (texto da confirmação de exclusão).
- **Benefício:** todas as telas mostram o dinheiro do mesmo jeito. O uso de `BigDecimal` mantém a precisão.

### 11.6 `MesAtual.kt`

- **Responsabilidade:** calcular o intervalo do mês corrente.
- **Funcionamento:** `periodoDoMesAtual(): Pair<Date, Date>` devolve o primeiro instante do mês (dia 1, 00:00:00.000) e o primeiro instante do mês seguinte. O intervalo é **fechado no início e aberto no fim**, como esperam as consultas `>= inicio AND < fim`.
- **Onde é usado:** `MenuActivity` (receita e despesa do mês), `RelatoriosActivity` (orçamento) e `GerenciarLimitesBottomSheet` ("Gasto este mês").
- **Benefício:** filtra por data completa (mês **e ano**), o que evita misturar o mesmo mês de anos diferentes.

### 11.7 `PrazoMeta.kt`

- **Responsabilidade:** regras de **prazo das metas**: texto informativo e seleção de data. Reúne dois elementos.
- **Funcionamento:**
  - `data class InfoPrazo(texto, vencido)` e `infoDoPrazo(meta)`: sem prazo devolve `null`; se a meta já foi atingida mostra "Prazo: dd/MM/yyyy"; se o prazo passou, "Prazo vencido em …" (`vencido = true`); senão calcula os meses restantes (mínimo 1) e sugere quanto guardar: `(faltante + meses − 1) / meses` centavos (arredondado para cima), em "Guarde cerca de R$ X por mês até …" ou "Guarde R$ X até …".
  - `class CampoPrazo`: controla o campo "Prazo (opcional)" dos BottomSheets. Abre um `MaterialDatePicker` que só aceita **datas de hoje em diante** (`DateValidatorPointForward`), converte entre UTC e horário local para não "perder um dia" (`paraUtcMeiaNoite`, `deUtcParaMeioDiaLocal`), mostra "Sem prazo" ou a data e oferece "Limpar". Guarda e restaura o estado na rotação (`guardarEstado`/`restaurarEstado`).
- **Onde é usado:** `MetaActivity` (texto do prazo no card), `NovaMetaBottomSheet` e `EditarMetaBottomSheet` (`CampoPrazo`).
- **Benefício:** transforma o prazo em ação concreta ("guarde R$ X por mês") e evita erros de fuso horário na escolha da data.

### 11.8 `RenderMovimentacoes.kt`

- **Responsabilidade:** **desenhar** a lista de movimentações nas telas, sem repetir o código em cada Activity.
- **Funcionamento:**
  - `renderizarTransacoes(context, container, lista)`: lista simples (usada para as "últimas 3"), sem lixeira.
  - `renderizarAgrupadoPorData(context, container, lista, aoExcluir)`: agrupa por dia (`dd 'de' MMMM 'de' yyyy`), cria um título por dia e mostra a **lixeira** em cada linha, chamando `aoExcluir` ao tocar.
  - `criarLinha(...)` (privada): infla `item_movimentacao`, define ícone, título (descrição ou categoria), subtítulo (categoria · data), valor com sinal e cor (`- R$ …` em vermelho `#C65353` para despesa; `+ R$ …` em verde `#16865B` para receita) e esconde o divisor da última linha.
  - `iconeDaCategoria(categoria, tipo)`: escolhe o ícone vetorial pela categoria (aceita sinônimos como "mercado"); categorias desconhecidas recebem `ic_outros_receita` ou `ic_outros` conforme o tipo.
  - Quando a lista está vazia, mostra "Nenhuma movimentação encontrada.".
- **Onde é usado:** `MenuActivity` e `MovimentacaoActivity`; `iconeDaCategoria` também é usado por `NovaMovimentacaoActivity`.
- **Benefício:** receitas e despesas têm a mesma aparência em toda a aplicação, e a ação de excluir só aparece onde faz sentido.

### 11.9 `Sessao.kt`

- **Responsabilidade:** controlar o **encerramento** e a **exigência** de sessão.
- **Funcionamento:** `Activity.direcionandoLimpandoSessao(pagAtual, pageProx)` chama `DadosEntrePaginas.limpar()` e abre a próxima tela com `FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK`, esvaziando o histórico de telas (o botão "voltar" não retorna a uma tela logada). `Activity.exigirSessao()` devolve `true` se há usuário logado; caso contrário limpa, abre o `LoginActivity`, chama `finish()` e devolve `false`. As telas internas a usam no `onCreate`: `if (!exigirSessao()) return`.
- **Onde é usado:** `PerfilActivity` (sair e deletar conta), `EditarSenhaActivity` (após trocar a senha) e as oito telas internas (`exigirSessao`).
- **Benefício:** se o Android encerrar o processo e reabrir uma tela interna sem usuário, o aplicativo leva ao login em vez de mostrar uma tela vazia com dados de "ninguém".
- **Segurança:** é o controle de acesso das telas internas e o fechamento de sessão; a limpeza de pilha impede voltar a telas com dados do usuário anterior.

### 11.10 `TemaMeta.kt`

- **Responsabilidade:** definir a **identidade visual** (cores) de cada meta.
- **Funcionamento:** `data class TemaMeta(fundoIcone: Int, corProgresso: Int, corTrilha: Int)`, a lista `CORES_META = listOf("verde", "laranja", "azul")` e `temaDaMeta(cor)`, que devolve o fundo do ícone (`bg_icone_meta_verde/laranja/azul`), a cor da barra de progresso e a cor da trilha. Valores desconhecidos usam o tema verde.
- **Onde é usado:** `MetaActivity` (card da meta), `GuardarMetaBottomSheet` (ícone) e `NovaMetaBottomSheet` (`CORES_META`).
- **Benefício:** o usuário distingue as metas pela cor e o código das telas não precisa conhecer os códigos de cor.

### 11.11 `Validacao.kt`

- **Responsabilidade:** regras de validação de **cadastro e troca de senha**.
- **Funcionamento:** `TAMANHO_MINIMO_SENHA = 8`, `emailValido(email)` (usa `Patterns.EMAIL_ADDRESS` do Android) e `senhaValida(senha)` (`length >= 8`).
- **Onde é usado:** `CadastroActivity` (e-mail e senha) e `EditarSenhaActivity` (nova senha).
- **Benefício:** a regra fica em um só lugar, então mudar o tamanho mínimo é alterar uma constante.
- **Segurança:** barra e-mails malformados e senhas muito curtas. **Não** exige letras, números ou símbolos (só tamanho).

### 11.12 `Voltar.kt`

- **Responsabilidade:** encerrar a tela atual de forma padronizada.
- **Funcionamento:** `Activity.voltando()` apenas chama `finish()`.
- **Onde é usado:** em `MainActivity` (após a tela de abertura), `LoginActivity` (após o login) e `CadastroActivity` (ao voltar para o login), para que a tela anterior saia da pilha.
- **Benefício:** nomeia a intenção ("voltar") e evita deixar telas de login acumuladas na pilha.

---

## 12. Documentação detalhada da pasta `security`

Pasta: `app/src/main/java/com/example/koin/security/`

A pasta contém **um** arquivo. Ela concentra o tratamento de **senhas**: o aplicativo nunca guarda a senha digitada, só um resumo (*hash*) dela. Esta seção descreve apenas o que o código faz; as limitações estão na [seção 17](#17-segurança-e-proteção-de-dados).

### 12.1 `GerenciadorSeguranca.kt`

- **Responsabilidade:** gerar e conferir o *hash* das senhas dos usuários.
- **Funcionamento:** `object GerenciadorSeguranca` usa **PBKDF2 com HMAC-SHA1** (`PBKDF2WithHmacSHA1`, padrão disponível desde a API 24, o `minSdk` do projeto):
  - `gerarHash(senha)`: cria um **sal aleatório de 16 bytes** com `SecureRandom`, deriva um hash de **256 bits** com **120.000 iterações** e devolve o texto `iteracoes:sal:hash` (sal e hash em Base64).
  - `verificar(senha, armazenado)`: separa o texto em três partes (se o formato não tiver exatamente 3 partes, devolve `false`), recalcula o hash com o mesmo sal e número de iterações e compara com `MessageDigest.isEqual`, **comparação de tempo constante**.
  - `derivar` e `codificar` (privadas) fazem a derivação (`PBEKeySpec` + `SecretKeyFactory`) e a codificação Base64.
- **Integração:** `CadastroActivity.gerarHash` ao criar a conta; `LoginActivity.verificar` no acesso; `EditarSenhaActivity` usa `verificar` na senha atual e `gerarHash` na nova. Os cálculos rodam em `Dispatchers.Default`, para não travar a tela, já que o PBKDF2 é lento de propósito.
- **Valor de negócio:** protege a confiança do usuário: se o banco do aparelho for copiado, as senhas não aparecem em texto.
- **Segurança:**
  - sal individual: duas pessoas com a mesma senha têm hashes diferentes;
  - muitas iterações: encarece tentativas de adivinhar a senha por força bruta;
  - o número de iterações vai junto no texto, o que permite aumentá-lo no futuro sem invalidar contas antigas;
  - comparação de tempo constante.
  - **Limites:** SHA-1 como função interna do HMAC (não é problema prático em PBKDF2, mas SHA-256 seria preferível em `minSdk` ≥ 26); o aplicativo não limita tentativas de login; e nada disso criptografa o restante do banco.

---

## 13. Documentação detalhada das Activities

Pasta: `app/src/main/java/com/example/koin/` (raiz do pacote). Todas as 11 Activities estão declaradas em `AndroidManifest.xml` com `windowSoftInputMode="adjustResize"`; somente `MainActivity` é a porta de entrada (`LAUNCHER`, `exported="true"`) e as demais são `exported="false"`, ou seja, **não podem ser abertas por outros aplicativos**.

**Padrões comuns:** todas chamam `enableEdgeToEdge()` e aplicam o espaçamento das barras do sistema (`ViewCompat.setOnApplyWindowInsetsListener` no elemento `main`); as oito telas internas (Menu, Movimentação, Nova movimentação, Relatórios, Meta, Perfil, Editar senha e Dados pessoais) usam `if (!exigirSessao()) return`; as consultas ao banco rodam em `lifecycleScope.launch`; e não há ViewModel, repository nem diálogo de terceiros: a lógica fica na própria Activity e as janelas são `AlertDialog`, `PopupMenu` ou os BottomSheets de `dialog/`.

**Mapa de navegação**

```
MainActivity ──► LoginActivity ◄──► CadastroActivity
                      │
                      ▼
 ┌────────────── MenuActivity ──────────────┐
 │  (barra inferior em Menu, Movimentação,  │
 │   Relatórios e Meta: navega entre elas)  │
 ▼                ▼               ▼          ▼
MovimentacaoActivity  RelatoriosActivity  MetaActivity  NovaMovimentacaoActivity ("+")
                                                         
 Foto de perfil (em todas) ──► PerfilActivity ──► EditarSenhaActivity
                                       ├──────► VerInformcoesContaActivity
                                       └──────► Login (sair) / Cadastro (deletar conta)
```

### 13.1 `MainActivity.kt` — tela de abertura

- **Responsabilidade:** exibir a tela de abertura (*splash*) do aplicativo e encaminhar ao login.
- **Funcionamento:** carrega `activity_main` (uma `ImageView` com `koin_splash.png`), espera 3 segundos com `delay(3000)` dentro de `lifecycleScope.launch`, abre `LoginActivity` e chama `voltando()` para sair da pilha.
- **Navegação:** → `LoginActivity`.
- **Experiência do usuário:** apresenta a marca antes do acesso. O aplicativo **sempre** pede login ao abrir, pois a sessão não é guardada em disco.

### 13.2 `LoginActivity.kt` — acesso à conta

- **Responsabilidade:** autenticar o usuário.
- **Funcionamento:** lê e-mail e senha (`etEmail`, `etSenha`); se algum estiver vazio mostra "Preencha todos os campos!". Desabilita o botão (`btnEntrar`), busca o usuário com `buscarPorEmail(email.lowercase())` e confere a senha com `GerenciadorSeguranca.verificar` em `Dispatchers.Default`. Se o usuário não existe **ou** a senha não confere, mostra a mesma mensagem genérica "E-mail ou senha inválidos! Tente novamente" e reabilita o botão. Se confere, limpa a sessão anterior, grava `id`, `nome` e `email` em `DadosEntrePaginas`, abre `MenuActivity` e finaliza o login.
- **Componentes visuais:** logo, dois `EditText`, botão "Entrar" e link "Criar conta" (`activity_login`).
- **Navegação:** → `MenuActivity` (sucesso) ou → `CadastroActivity` (link).
- **Segurança:** a mensagem única para e-mail inexistente e senha errada não revela quais e-mails estão cadastrados; o botão bloqueado durante a verificação evita toques repetidos. Não há bloqueio por número de tentativas.
- **Experiência:** acesso em um passo, com retorno imediato dos erros.

### 13.3 `CadastroActivity.kt` — criação de conta

- **Responsabilidade:** cadastrar um novo usuário.
- **Funcionamento:** lê nome, e-mail, senha e confirmação. Valida, nesta ordem: campos preenchidos, e-mail válido (`emailValido`), senha com pelo menos 8 caracteres (`senhaValida`) e senha igual à confirmação. Gera o hash em `Dispatchers.Default` (`gerarHash`), salva o e-mail em minúsculas e insere com `UsuarioDao.inserir`. Captura `SQLiteConstraintException` para avisar "Este e-mail já está cadastrado!" (por causa do índice único). Em caso de sucesso, mostra "Cadastro realizado com sucesso!" e volta ao login.
- **Componentes visuais:** logo, quatro `EditText` (nome, e-mail, senha, confirmação), botão "Criar Conta" e link "Entrar" (`activity_cadastro`).
- **Navegação:** → `LoginActivity`.
- **Segurança:** a senha nunca é gravada em texto; o e-mail é normalizado para não permitir duplicatas por diferença de maiúsculas; qualquer falha de banco mostra mensagem genérica e reabilita o botão.
- **Experiência:** orienta com mensagens específicas para cada erro de preenchimento.

### 13.4 `MenuActivity.kt` — tela inicial (Início)

- **Responsabilidade:** dar uma **visão geral** das finanças.
- **Funcionamento:** no `onCreate` liga as views, a navegação e o botão de olho (`btnVisibilidadeSaldo`). No `onResume` (toda vez que a tela volta ao primeiro plano) consulta o banco e calcula: `receitaMes` e `despesaMes` (por `periodoDoMesAtual()`) e o **saldo disponível** = `totalReceitas − totalDespesas − totalGuardado` (todo o histórico menos o reservado em metas). A cor do cartão de saldo muda: verde (`fundo_saldo`) se positivo, vermelho (`fundo_vermelho`) se negativo, cinza (`fundo_saldo_zerado`) se zero. Lista as **3 últimas movimentações** (`TransacaoDao.listar(..., "todas", "", 3)`) por `renderizarTransacoes`. O botão de olho alterna entre os valores e `••••` (`saldoOculto`), mantém o estado ao girar a tela e ao voltar para a tela.
- **Componentes visuais:** saudação "Olá, nome!", cartão de saldo com receitas/despesas do mês, cartões de Entradas e Saídas, lista "Últimas movimentações" com "Ver todas" e a barra inferior.
- **Navegação:** foto de perfil → `PerfilActivity`; barra inferior → `MovimentacaoActivity`, `RelatoriosActivity`, `MetaActivity`; botão "+" → `NovaMovimentacaoActivity`; "Ver todas" → `MovimentacaoActivity`.
- **Experiência:** responde de relance "quanto eu tenho agora?". O modo "ocultar" ajuda a usar o aplicativo em público.

### 13.5 `MovimentacaoActivity.kt` — histórico de movimentações

- **Responsabilidade:** consultar, filtrar, buscar e **excluir** movimentações.
- **Funcionamento:** possui três filtros (`todas`, `receita`, `despesa`) que valem para o bloco "Últimas 3 transações" e para a lista completa. A busca (`etBuscar`, "Buscar por nome ou categoria") atualiza a lista **a cada caractere digitado** (`TextWatcher`) e só afeta a lista completa. Cada consulta cancela a anterior (`jobTop3?.cancel()`, `jobLista?.cancel()`), evitando resultado desatualizado. A lista completa é agrupada por data (`renderizarAgrupadoPorData`) e cada linha tem uma **lixeira**. Ao tocar, `confirmarExclusao` abre um `AlertDialog` ("Excluir movimentação — Deseja excluir a despesa "X" de R$ …? Essa ação não pode ser desfeita.") com "Cancelar" e "Excluir" em vermelho. Confirmada a ação, `excluirTransacao` usa o `tipo` da linha para chamar `excluirDespesa` ou `excluirReceita` (com o `idUsuario`) e recarrega as duas listas. O `onResume` recarrega tudo para refletir lançamentos novos.
- **Componentes visuais:** campo de busca, três botões de filtro (`filtro_ativo`/`filtro_inativo`), blocos "Últimas 3 transações" e "Movimentações anteriores", barra inferior.
- **Navegação:** barra inferior → Início, Relatórios, Metas; "+" → `NovaMovimentacaoActivity`; foto de perfil → `PerfilActivity`.
- **Segurança/integridade:** a exclusão exige confirmação, é limitada ao dono do registro (`WHERE id AND idFkUsuario`) e avisa quando nada foi apagado ("Movimentação não encontrada.").
- **Experiência:** achar um lançamento antigo e corrigir um erro de digitação passa a ser rápido.

### 13.6 `NovaMovimentacaoActivity.kt` — cadastro de receita/despesa

- **Responsabilidade:** registrar uma movimentação.
- **Funcionamento:** alterna entre as abas **Despesa** (vermelha) e **Receita** (verde); ao trocar o tipo, a categoria selecionada é limpa, os textos mudam e o cartão de valor muda de cor (`fundo_vermelho`/`fundo_saldo`). O campo de valor tem **máscara monetária** (`configurarMascaraMonetaria`, formato de moeda pt-BR). A categoria é escolhida em um `MaterialAlertDialogBuilder` com a lista de `Categorias.doTipo(tipo)`. A data é escolhida em `MaterialDatePicker` que **não aceita datas futuras** (`DateValidatorPointBackward`), com tratamento de UTC; mostra "Hoje" e "Ontem" por extenso. Ao salvar, `validarFormulario` exige: valor maior que zero (`ConverterValor.paraCentavos`), descrição com no máximo **60 caracteres** (opcional) e categoria escolhida. Grava `Despesas` ou `Receita` com `idFkUsuario`, mostra "Despesa salva!"/"Receita salva!" e fecha a tela. Tipo, categoria e data sobrevivem à rotação (`onSaveInstanceState`).
- **Componentes visuais:** abas, cartão de valor, descrição, seletor de categoria com ícone, seletor de data e botão "Salvar movimentação" (com pequena animação de toque).
- **Navegação:** fecha com `finish()` (botão `×` ou após salvar), voltando à tela de origem, cujo `onResume` atualiza os números.
- **Segurança/integridade:** impede valor zero, data futura, texto longo demais e salvamento sem sessão ("Sessão expirada. Faça login novamente."); o botão fica desabilitado durante a gravação.
- **Experiência:** cadastro rápido, com cores que indicam o tipo e máscara que evita erro de digitação do valor.

### 13.7 `MetaActivity.kt` — metas de economia

- **Responsabilidade:** acompanhar e gerenciar as metas.
- **Funcionamento:** o `onResume` chama `carregarMetas()`, que lista as metas do usuário e monta um card (`item_meta`) para cada uma: ícone com a cor do tema, nome, "R$ guardado de R$ alvo", barra de progresso, percentual ("Comece a guardar um pouco por mês.", "x% concluído" ou "Meta concluída!") e o texto de prazo (vermelho `#C0392B` quando vencido). O topo mostra o **total guardado**, o total planejado e o percentual geral. Tocar no card abre `GuardarMetaBottomSheet`; o botão de opções (`ic_mais_opcoes`) abre um `PopupMenu` com **Editar** (abre `EditarMetaBottomSheet`) e **Excluir** (abre `AlertDialog` "Excluir meta — Deseja excluir a meta "X"? O valor guardado nela volta para o seu saldo."). Três `setFragmentResultListener` recarregam a lista quando um BottomSheet conclui.
- **Componentes visuais:** cartão de resumo, lista de cards, mensagem "Você ainda não tem metas...", botão "+ Criar nova meta" e a barra inferior.
- **Navegação:** barra inferior → Início, Movimentações, Relatórios; "+" → `NovaMovimentacaoActivity`; foto → `PerfilActivity`.
- **Segurança/integridade:** a exclusão exige confirmação e só afeta metas do usuário logado; evita abrir o mesmo BottomSheet duas vezes (`findFragmentByTag`).
- **Experiência:** visualiza o progresso e o que falta, e é lembrado de quanto guardar por mês.

### 13.8 `RelatoriosActivity.kt` — relatórios e orçamento

- **Responsabilidade:** analisar receitas, despesas e gastos por categoria em um período.
- **Funcionamento:** três filtros de período: **Mês** (mês atual), **Trimestre** (mês atual e os dois anteriores) e **Ano** (janeiro ao fim do ano atual), implementados em `obterPeriodo`. Para o período escolhido, `atualizarGraficoPorPeriodo` calcula receitas, despesas e saldo, e carrega os totais por categoria. O saldo mostra um indicador: **seta `↗` verde** quando é positivo ou zero e **seta `↓` vermelha** quando é negativo (`atualizarIndicadorSaldo`). O **gráfico de pizza** (`PieChart`, MPAndroidChart) mostra as categorias com buraco central e animação; a legenda lista até 6 linhas, agrupando o excedente em "Demais" (`prepararFatias`), com cor por categoria (`corDaCategoria`) e percentual (`<1%` quando muito pequeno). O bloco **Orçamento utilizado** soma os limites definidos, compara com o gasto do **mês atual** nas categorias com limite e muda a cor da barra: verde (< 80%), amarelo (≥ 80%) e vermelho (≥ 100%). O botão "Gerenciar limites" abre `GerenciarLimitesBottomSheet`; ao salvar, o orçamento é recalculado. O `onResume` atualiza gráfico e orçamento. O período escolhido sobrevive à rotação.
- **Componentes visuais:** filtros de período, três cartões (receitas, despesas, saldo), gráfico, legenda, barra de orçamento e a navegação inferior.
- **Navegação:** barra inferior → Início, Movimentações, Metas; "+" → `NovaMovimentacaoActivity`; foto → `PerfilActivity`.
- **Segurança/integridade:** as consultas cancelam a anterior (`consultaRelatorioJob?.cancel()`) e tratam erros com mensagem ("Erro ao carregar os relatórios.").
- **Experiência:** mostra onde o dinheiro está indo e alerta visualmente quando o limite está próximo.

### 13.9 `PerfilActivity.kt` — perfil e gestão da conta

- **Responsabilidade:** mostrar o perfil e oferecer as ações de conta.
- **Funcionamento:** exibe nome e e-mail de `DadosEntrePaginas`. Ações: **Editar senha** → `EditarSenhaActivity`; **Dados pessoais** → `VerInformcoesContaActivity`; **Sair da conta** → `direcionandoLimpandoSessao(..., LoginActivity)` (limpa sessão e pilha); **Deletar sua conta** → `AlertDialog` "Deseja deletar sua conta? Você tem certeza que deseja executar esta ação?" (Sim/Não). Se confirmado, `deletandoConta` (em `Dispatchers.IO`) apaga **todas as despesas, receitas, metas e orçamentos do usuário** e, por fim, o próprio usuário, e então abre `CadastroActivity` limpando a sessão.
- **Componentes visuais:** foto-ícone, nome/e-mail, cartão "MINHA CONTA" e botões "Sair" e "Deletar" (vermelhos).
- **Navegação:** → `EditarSenhaActivity`, `VerInformcoesContaActivity`, `LoginActivity`, `CadastroActivity`; `×` fecha.
- **Segurança/integridade:** a exclusão da conta exige confirmação e remove os dados de todas as tabelas (não ficam registros órfãos, já que não há chave estrangeira declarada). "Sair" não pede confirmação.
- **Experiência:** controle total da conta e direito de apagar os próprios dados.

### 13.10 `EditarSenhaActivity.kt` — troca de senha

- **Responsabilidade:** alterar a senha do usuário logado.
- **Funcionamento:** pede senha atual, nova senha e confirmação. Valida preenchimento, tamanho mínimo da nova senha (`senhaValida`) e igualdade com a confirmação. Em seguida, em segundo plano, busca o hash atual (`buscarSenhaPorId`) e o confere (`verificar`); se não bater, mostra "Senha atual incorreta!". Se estiver correto, gera o novo hash (`gerarHash`), grava com `atualizarSenha`, mostra "Senha atualizada com sucesso!", espera 1,5 s e **encerra a sessão**, levando ao login.
- **Componentes visuais:** três campos de senha, botão "Alterar senha" e `×` (`activity_editar_senha`).
- **Navegação:** → `LoginActivity` (após trocar); `×` volta ao perfil.
- **Segurança:** exige a senha atual (impede que alguém com o aparelho desbloqueado troque a senha sem conhecê-la); a senha nova é gravada só como hash com novo sal; a sessão é encerrada para obrigar um novo login.
- **Experiência:** processo curto e com mensagens claras.

### 13.11 `VerInformcoesContaActivity.kt` — dados pessoais

- **Responsabilidade:** mostrar nome e e-mail da conta, somente leitura.
- **Funcionamento:** lê `DadosEntrePaginas.nome` e `email` e preenche `tvNome` e `tvEmail`. Não consulta o banco. Protegida por `exigirSessao()`.
- **Componentes visuais:** cartão com os rótulos "NOME" e "E-MAIL" (`activity_ver_informcoes_conta`).
- **Navegação:** chegada pelo perfil; o botão voltar do sistema retorna.
- **Experiência:** confere com qual conta está logado.
- **Observação:** o nome da classe e do layout contém um erro de digitação (`Informcoes`, sem "a"); foi mantido para não alterar referências existentes.

---

## 14. Documentação detalhada de `res/drawable`

Pasta: `app/src/main/res/drawable/` — **59 arquivos** (47 vetores/shapes XML e PNGs). Todas as cores seguem a paleta do aplicativo: verde principal `#16865B`, vermelho `#C65353`, cinza `#89958D` e fundos claros (`#E3F3E9`, `#FBEAEA`).

Agrupamento: **botões, cartões e fundos** (`botao_koin`, `card_koin`, `fundo_*`, `bg_*`, `filtro_*`, `barra_busca`), **ícones de categoria** (`ic_*`), **ícones de interface e navegação** (`icone_*`, `ic_lixeira`, `ic_mais_opcoes`) e **imagens** (`*.png`). Os ícones de categoria são escolhidos por `iconeDaCategoria` (em `RenderMovimentacoes.kt`).

> O app é compatível com tema escuro apenas no nível do tema (`values-night/themes.xml`); os drawables usam cores fixas.

### `barra_busca.xml`

- **Tipo e aparência:** Shape XML. Caixa com cantos de 16dp, fundo branco e contorno fino cinza.
- **Finalidade:** Dar aparência de campo de entrada à busca, aos campos de valor e limite e aos botões não selecionados (aba "Guardar/Retirar", ícones de meta não escolhidos).
- **Onde é usado:** `EditarMetaBottomSheet.kt`, `GuardarMetaBottomSheet.kt`, `NovaMetaBottomSheet.kt`, `activity_menu.xml`, `activity_meta.xml`, `activity_movimentacao.xml`, `activity_nova_movimentacao.xml`, `activity_relatorios.xml`, `bottom_sheet_guardar_meta.xml`, `bottom_sheet_nova_meta.xml`, `item_limite_categoria.xml`.
- **Contribuição:** Mantém os campos de digitação e as opções inativas com a mesma identidade visual.

### `bg_botao_editar_perfil.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos arredondados (14dp) em verde bem claro (`#E3F3E9`).
- **Finalidade:** Fundo do botão "Editar senha" do perfil, uma ação secundária e discreta.
- **Onde é usado:** `activity_perfil.xml`.
- **Contribuição:** Diferencia ações secundárias (verde claro) das primárias (verde sólido `botao_koin`).

### `bg_card_perfil.xml`

- **Tipo e aparência:** Shape XML. Cartão de cantos de 20dp, fundo branco e borda de 1dp (`#E0E7E1`).
- **Finalidade:** Agrupar visualmente os blocos de informação das telas de conta.
- **Onde é usado:** `activity_editar_senha.xml`, `activity_perfil.xml`, `activity_ver_informcoes_conta.xml`.
- **Contribuição:** Cria hierarquia visual e separa grupos de dados sem sombras pesadas.

### `bg_deletar_conta.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 14dp em vinho escuro (`#8E1E1E`).
- **Finalidade:** Fundo do botão "Deletar sua conta".
- **Onde é usado:** `activity_perfil.xml`.
- **Contribuição:** A cor escura e distinta dos demais botões sinaliza uma ação destrutiva, reduzindo cliques acidentais.

### `bg_icone_categoria.xml`

- **Tipo e aparência:** Shape XML. Forma oval cinza (`#757575`).
- **Finalidade:** Fundo circular genérico de ícone de categoria.
- **Onde é usado:** sem referência no código atual (recurso reserva).
- **Contribuição:** Hoje não é usado; os ícones de categoria já trazem seu próprio fundo.

### `bg_icone_meta_azul.xml`

- **Tipo e aparência:** Shape XML. Círculo azul (`#1565C0`).
- **Finalidade:** Fundo do ícone (emoji) de metas com o tema azul.
- **Onde é usado:** `TemaMeta.kt`.
- **Contribuição:** Permite diferenciar metas por cor.

### `bg_icone_meta_laranja.xml`

- **Tipo e aparência:** Shape XML. Círculo laranja (`#EF6C00`).
- **Finalidade:** Fundo do ícone (emoji) de metas com o tema laranja.
- **Onde é usado:** `TemaMeta.kt`.
- **Contribuição:** Permite diferenciar metas por cor.

### `bg_icone_meta_selecionado.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 14dp com borda verde de 2dp e fundo verde claro.
- **Finalidade:** Destacar qual ícone está escolhido na criação/edição de meta.
- **Onde é usado:** `EditarMetaBottomSheet.kt`, `NovaMetaBottomSheet.kt`.
- **Contribuição:** Dá retorno imediato da seleção do usuário.

### `bg_icone_meta_verde.xml`

- **Tipo e aparência:** Shape XML. Círculo verde (`#2E7D32`).
- **Finalidade:** Fundo do ícone (emoji) de metas com o tema verde (padrão).
- **Onde é usado:** `TemaMeta.kt`.
- **Contribuição:** Permite diferenciar metas por cor.

### `bg_icone_perfil.xml`

- **Tipo e aparência:** Shape XML. Círculo cinza claro (`#EEEEEE`).
- **Finalidade:** Moldura circular da imagem de perfil.
- **Onde é usado:** `activity_perfil.xml`, `activity_ver_informcoes_conta.xml`.
- **Contribuição:** Dá um acabamento de "avatar" às telas de conta.

### `bg_sair_perfil.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 14dp em rosa claro (`#FBEAEA`).
- **Finalidade:** Fundo do botão "Sair da conta".
- **Onde é usado:** `activity_perfil.xml`.
- **Contribuição:** Cor suave de alerta: indica uma ação que encerra a sessão, mas sem a gravidade de excluir a conta.

### `botao_koin.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 14dp em verde da marca (`#16865B`).
- **Finalidade:** Fundo dos botões principais (entrar, criar conta, salvar, "+" da navegação, confirmar).
- **Onde é usado:** `GuardarMetaBottomSheet.kt`, `activity_cadastro.xml`, `activity_editar_senha.xml`, `activity_login.xml`, `activity_menu.xml`, `activity_meta.xml`, `activity_movimentacao.xml`, `activity_nova_movimentacao.xml`, `activity_relatorios.xml`, `bottom_sheet_gerenciar_limites.xml`, `bottom_sheet_guardar_meta.xml`, `bottom_sheet_nova_meta.xml`.
- **Contribuição:** É o recurso mais reaproveitado do projeto: padroniza o botão primário e a identidade visual.

### `calendario.png`

- **Tipo e aparência:** Imagem (PNG). Imagem PNG de calendário.
- **Finalidade:** Ícone do campo "Data do registro".
- **Onde é usado:** `activity_nova_movimentacao.xml`.
- **Contribuição:** Torna reconhecível que o campo abre um calendário.

### `card_koin.xml`

- **Tipo e aparência:** Shape XML. Forma branca para cartões.
- **Finalidade:** Fundo dos cartões de conteúdo (formulários de login e cadastro, listas e blocos).
- **Onde é usado:** `activity_cadastro.xml`, `activity_login.xml`, `activity_menu.xml`, `activity_movimentacao.xml`, `activity_relatorios.xml`, `item_meta.xml`.
- **Contribuição:** Separa o conteúdo do fundo da tela e mantém consistência entre as telas.

### `filtro_ativo.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 14dp em verde (`#16865B`).
- **Finalidade:** Aparência da opção selecionada nos filtros (Todas/Receitas/Despesas e Mês/Trimestre/Ano).
- **Onde é usado:** `MovimentacaoActivity.kt`, `RelatoriosActivity.kt`, `activity_movimentacao.xml`, `activity_relatorios.xml`.
- **Contribuição:** Mostra, sem texto adicional, qual filtro está em uso.

### `filtro_inativo.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 14dp, fundo branco e borda de 1dp.
- **Finalidade:** Aparência das opções não selecionadas dos filtros e abas; também preenche o ícone da categoria enquanto nenhuma foi escolhida.
- **Onde é usado:** `MovimentacaoActivity.kt`, `NovaMovimentacaoActivity.kt`, `RelatoriosActivity.kt`, `activity_movimentacao.xml`, `activity_nova_movimentacao.xml`, `activity_relatorios.xml`.
- **Contribuição:** Contrasta com `filtro_ativo` para indicar o estado de cada opção.

### `funco_saldo_negativo.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 24dp em vermelho vivo (`#FF4D4D`).
- **Finalidade:** Fundo inicial da aba "Despesa" no XML da nova movimentação (pré-visualização do layout).
- **Onde é usado:** `activity_nova_movimentacao.xml`.
- **Contribuição:** Em execução a aba passa a usar `fundo_vermelho`. O nome contém um erro de digitação ("funco").

### `fundo_koin.xml`

- **Tipo e aparência:** Shape XML. Caixa de cantos de 12dp, fundo branco e borda fina (`#DDE5DF`).
- **Finalidade:** Fundo dos campos de texto das telas de login, cadastro e alteração de senha.
- **Onde é usado:** `activity_cadastro.xml`, `activity_editar_senha.xml`, `activity_login.xml`.
- **Contribuição:** Dá aos formulários de acesso um visual uniforme.

### `fundo_koin_img.png`

- **Tipo e aparência:** Imagem (PNG). Imagem PNG de fundo (cerca de 578 KB).
- **Finalidade:** Imagem decorativa de fundo das telas de login e cadastro.
- **Onde é usado:** `activity_cadastro.xml`, `activity_login.xml`.
- **Contribuição:** Reforça a identidade visual na entrada do aplicativo.

### `fundo_rosa.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 24dp em coral (`#E97867`).
- **Finalidade:** Fundo de cartão alternativo.
- **Onde é usado:** sem referência no código atual (recurso reserva).
- **Contribuição:** Hoje não é referenciado no código.

### `fundo_saldo.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 24dp em verde (`#16865B`).
- **Finalidade:** Fundo do cartão de saldo positivo (início), do resumo de metas e da aba/cartão de receita na nova movimentação.
- **Onde é usado:** `MenuActivity.kt`, `NovaMovimentacaoActivity.kt`, `activity_menu.xml`, `activity_meta.xml`, `activity_nova_movimentacao.xml`.
- **Contribuição:** O verde comunica situação positiva ou entrada de dinheiro.

### `fundo_saldo_zerado.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 24dp em cinza (`#707070`).
- **Finalidade:** Fundo do cartão de saldo quando o saldo é exatamente zero.
- **Onde é usado:** `MenuActivity.kt`.
- **Contribuição:** Diferencia o saldo zero dos estados positivo e negativo.

### `fundo_verde_claro.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 24dp em verde claro (`#7BC8A4`).
- **Finalidade:** Fundo de cartão alternativo.
- **Onde é usado:** sem referência no código atual (recurso reserva).
- **Contribuição:** Hoje não é referenciado no código.

### `fundo_vermelho.xml`

- **Tipo e aparência:** Shape XML. Retângulo de cantos de 24dp em vermelho (`#C65353`).
- **Finalidade:** Fundo do cartão de saldo negativo (início) e da aba/cartão de despesa na nova movimentação.
- **Onde é usado:** `MenuActivity.kt`, `NovaMovimentacaoActivity.kt`.
- **Contribuição:** O vermelho comunica saldo negativo ou saída de dinheiro.

### `grafico_despesas.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em vermelho escuro (`#C62828`).
- **Finalidade:** Ícone de gráfico de despesas.
- **Onde é usado:** sem referência no código atual (recurso reserva).
- **Contribuição:** Hoje não é referenciado (o gráfico real é desenhado pelo MPAndroidChart).

### `ic_alimentacao.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo claro avermelhado e traço `#B8625A`.
- **Finalidade:** Ícone da categoria de despesa **Alimentação** (também "mercado" e "supermercado").
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Permite reconhecer a categoria de cada lançamento sem ler o texto.

### `ic_carteira.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo verde claro e traço `#1C7F57`.
- **Finalidade:** Ícone da categoria de receita **Salário** (também "trabalho").
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Receitas usam a paleta verde; despesas, a avermelhada.

### `ic_cat_compras.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo claro avermelhado e traço `#B8625A`.
- **Finalidade:** Ícone da categoria de despesa **Compras**.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Facilita a leitura rápida do histórico.

### `ic_contas.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo claro avermelhado e traço `#B8625A`.
- **Finalidade:** Ícone da categoria de despesa **Contas**.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Facilita a leitura rápida do histórico.

### `ic_educacao.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo claro avermelhado e traço `#B8625A`.
- **Finalidade:** Ícone da categoria de despesa **Educação**.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Facilita a leitura rápida do histórico.

### `ic_freelance.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo verde claro e traço `#1C7F57`.
- **Finalidade:** Ícone da categoria de receita **Freelance**.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Receitas usam a paleta verde.

### `ic_investimentos.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo verde claro e traço `#1C7F57`.
- **Finalidade:** Ícone da categoria de receita **Investimentos**.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Receitas usam a paleta verde.

### `ic_launcher_background.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 108dp com fundo verde (`#3DDC84`) e grade clara.
- **Finalidade:** Camada de fundo do ícone adaptativo do aplicativo no launcher.
- **Onde é usado:** `ic_launcher.xml`, `ic_launcher_round.xml`.
- **Contribuição:** Faz parte do ícone padrão do Android Studio, referenciado pelos ícones adaptativos.

### `ic_launcher_foreground.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 108dp com gradiente (modelo padrão do Android Studio).
- **Finalidade:** Camada frontal do ícone de aplicativo padrão.
- **Onde é usado:** sem referência no código atual (recurso reserva).
- **Contribuição:** Não é usada diretamente: o ícone do aplicativo vem dos recursos `mipmap`.

### `ic_lazer.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo claro avermelhado e traço `#B8625A`.
- **Finalidade:** Ícone da categoria de despesa **Lazer**.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Facilita a leitura rápida do histórico.

### `ic_lixeira.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em vermelho (`#C65353`), desenho de lixeira.
- **Finalidade:** Botão de excluir em cada linha da lista completa de movimentações.
- **Onde é usado:** `item_movimentacao.xml`.
- **Contribuição:** Indica claramente a ação de remoção; a cor vermelha reforça o risco.

### `ic_mais_opcoes.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em cinza (`#89958D`), três pontos.
- **Finalidade:** Botão de opções (Editar/Excluir) do cartão de cada meta.
- **Onde é usado:** `item_meta.xml`.
- **Contribuição:** Ícone padrão de "mais opções", que o usuário já conhece.

### `ic_moradia.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo claro avermelhado e traço `#B8625A`.
- **Finalidade:** Ícone da categoria de despesa **Moradia**.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Facilita a leitura rápida do histórico.

### `ic_outros.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo claro avermelhado e traço `#B8625A`.
- **Finalidade:** Ícone da categoria de despesa **Outros** e de categorias desconhecidas.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Garante que toda despesa tenha um ícone.

### `ic_outros_receita.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo verde claro e traço `#1C7F57`.
- **Finalidade:** Ícone da categoria de receita **Outros** e de categorias de receita desconhecidas.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Garante que toda receita tenha um ícone.

### `ic_presente.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo verde claro e traço `#1C7F57`.
- **Finalidade:** Ícone da categoria de receita **Presente**.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Receitas usam a paleta verde.

### `ic_saude.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo claro avermelhado e traço `#B8625A`.
- **Finalidade:** Ícone da categoria de despesa **Saúde**.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Facilita a leitura rápida do histórico.

### `ic_transporte.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp com fundo claro avermelhado e traço `#B8625A`.
- **Finalidade:** Ícone da categoria de despesa **Transporte**.
- **Onde é usado:** `RenderMovimentacoes.kt`.
- **Contribuição:** Facilita a leitura rápida do histórico.

### `icone_busca.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em cinza (`#89958D`), lupa.
- **Finalidade:** Ícone do campo "Buscar por nome ou categoria".
- **Onde é usado:** `activity_movimentacao.xml`.
- **Contribuição:** Indica a função de busca.

### `icone_email.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em verde (`#16865B`), envelope.
- **Finalidade:** Ícone do campo de e-mail (login e cadastro).
- **Onde é usado:** `activity_cadastro.xml`, `activity_login.xml`.
- **Contribuição:** Identifica o campo e dá consistência visual ao formulário.

### `icone_inicio.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em verde (`#16865B`), casa.
- **Finalidade:** Item "Início" da barra de navegação **quando a tela atual é o início**.
- **Onde é usado:** `activity_menu.xml`.
- **Contribuição:** O verde indica a aba em que o usuário está.

### `icone_inicio_inativo.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em cinza (`#89958D`), casa.
- **Finalidade:** Item "Início" da barra de navegação nas demais telas.
- **Onde é usado:** `activity_meta.xml`, `activity_movimentacao.xml`, `activity_relatorios.xml`.
- **Contribuição:** O cinza indica que a aba não está ativa.

### `icone_meta_ativo.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp em verde (`#16865B`).
- **Finalidade:** Item "Metas" da barra de navegação **quando a tela atual é Metas**.
- **Onde é usado:** `activity_meta.xml`.
- **Contribuição:** Indica a aba atual.

### `icone_metas.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 48dp em cinza (`#89958D`).
- **Finalidade:** Item "Metas" da barra de navegação nas demais telas.
- **Onde é usado:** `activity_menu.xml`, `activity_movimentacao.xml`, `activity_relatorios.xml`.
- **Contribuição:** Indica a aba inativa.

### `icone_movimentacao.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em cinza (`#89958D`).
- **Finalidade:** Item "Movimentos" da barra de navegação nas demais telas.
- **Onde é usado:** `activity_menu.xml`, `activity_meta.xml`, `activity_relatorios.xml`.
- **Contribuição:** Indica a aba inativa.

### `icone_movimentacao_ativo.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em verde (`#16865B`).
- **Finalidade:** Item "Movimentos" da barra de navegação quando a tela atual é Movimentações.
- **Onde é usado:** `activity_movimentacao.xml`.
- **Contribuição:** Indica a aba atual.

### `icone_perfil.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 56dp, silhueta verde sobre fundo verde claro.
- **Finalidade:** Imagem de perfil exibida no topo das telas e no perfil.
- **Onde é usado:** `activity_menu.xml`, `activity_meta.xml`, `activity_movimentacao.xml`, `activity_perfil.xml`, `activity_relatorios.xml`, `activity_ver_informcoes_conta.xml`.
- **Contribuição:** Atalho visual e reconhecível para a área de conta.

### `icone_relatorio_ativo.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em verde (`#16865B`).
- **Finalidade:** Item "Relatórios" da barra de navegação quando a tela atual é Relatórios.
- **Onde é usado:** `activity_relatorios.xml`.
- **Contribuição:** Indica a aba atual.

### `icone_relatorios.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em cinza (`#89958D`).
- **Finalidade:** Item "Relatórios" da barra de navegação nas demais telas.
- **Onde é usado:** `activity_menu.xml`, `activity_meta.xml`, `activity_movimentacao.xml`.
- **Contribuição:** Indica a aba inativa.

### `icone_senha.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em verde (`#16865B`), cadeado.
- **Finalidade:** Ícone dos campos de senha (login, cadastro e alteração de senha).
- **Onde é usado:** `activity_cadastro.xml`, `activity_editar_senha.xml`, `activity_login.xml`.
- **Contribuição:** Identifica o campo e transmite ideia de proteção.

### `icone_usuario.xml`

- **Tipo e aparência:** Vetor (`VectorDrawable`). Vetor de 24dp em verde (`#16865B`), pessoa.
- **Finalidade:** Ícone do campo de nome no cadastro.
- **Onde é usado:** `activity_cadastro.xml`.
- **Contribuição:** Identifica o campo.

### `koin_splash.png`

- **Tipo e aparência:** Imagem (PNG). Imagem PNG de abertura (cerca de 10 MB).
- **Finalidade:** Imagem exibida na tela de abertura por 3 segundos.
- **Onde é usado:** `activity_main.xml`.
- **Contribuição:** Apresenta a marca. O arquivo é muito grande para uma imagem de tela; reduzir seu tamanho diminuiria o aplicativo.

### `logo_transparente.png`

- **Tipo e aparência:** Imagem (PNG). Imagem PNG do logotipo, com fundo transparente.
- **Finalidade:** Logotipo exibido nas telas de login, cadastro, alteração de senha e dados pessoais.
- **Onde é usado:** `activity_cadastro.xml`, `activity_editar_senha.xml`, `activity_login.xml`, `activity_ver_informcoes_conta.xml`.
- **Contribuição:** Mantém a marca presente nas telas de conta.

### `principal_koin.xml`

- **Tipo e aparência:** Shape XML. Forma com gradiente do verde escuro (`#126B4A`) ao verde da marca (`#16865B`).
- **Finalidade:** Fundo em degradê de destaque.
- **Onde é usado:** sem referência no código atual (recurso reserva).
- **Contribuição:** Hoje não é referenciado no código.

---

## 15. Documentação detalhada de `res/layout`

Pasta: `app/src/main/res/layout/` — **17 arquivos**: 11 telas (`activity_*`), 3 janelas inferiores (`bottom_sheet_*`) e 3 itens reutilizáveis (`item_*`).

Organização geral: as telas de **acesso e conta** (`login`, `cadastro`, `editar_senha`, `perfil`, `ver_informcoes_conta`) usam `ScrollView`, para o teclado não esconder campos; as telas **principais** (`menu`, `movimentacao`, `meta`, `relatorios`) são colunas com conteúdo rolável e a **barra de navegação inferior** fixa. As listas não são `RecyclerView`: o Kotlin infla um item (`item_*`) para cada registro dentro de um `LinearLayout`. As telas não usam `ViewBinding`; o Kotlin acessa as views por `findViewById` com os ids listados abaixo. O elemento raiz de cada tela tem o id `main`, usado para aplicar o espaçamento das barras do sistema.

### `activity_cadastro.xml`

- **O que representa:** a tela de cadastro (`CadastroActivity`).
- **Principais elementos:** `ScrollView` com logo, título "Crie a sua conta", campos `etNome`, `etEmail`, `etSenha`, `etConfirmarSenha`, botão `btnEntrar` ("Criar Conta") e link `tvLogar`.
- **Organização:** raiz `ScrollView` com 20 elementos. Mesmo padrão visual do login, o que torna a passagem de uma tela para a outra natural.
- **Relação com o código:** inflado por `CadastroActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `activity_editar_senha.xml`

- **O que representa:** a tela de troca de senha (`EditarSenhaActivity`).
- **Principais elementos:** `ScrollView` com título, `×`, logo e cartão com três campos rotulados (`etSenhaAtual`, `etSenha`, `etConfirmarSenha`) e botão `btAlterarSenha`.
- **Organização:** raiz `ScrollView` com 16 elementos. Rótulos em maiúsculas ("SENHA ATUAL", "NOVA SENHA", "CONFIRMAR SENHA") deixam claro o que digitar em cada campo.
- **Relação com o código:** inflado por `EditarSenhaActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `activity_login.xml`

- **O que representa:** a tela de login (`LoginActivity`).
- **Principais elementos:** `ScrollView` com logo (`ivLogoKoin`), título "Bem-vindo de volta!", campos `etEmail` e `etSenha` com ícones, botão `btnEntrar`, separador "ou" e link `tvCriarConta`.
- **Organização:** raiz `ScrollView` com 18 elementos. A rolagem evita que o teclado esconda o botão; poucos campos reduzem a chance de erro.
- **Relação com o código:** inflado por `LoginActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `activity_main.xml`

- **O que representa:** a tela de abertura (`MainActivity`).
- **Principais elementos:** Um `ConstraintLayout` (`main`) com uma única `ImageView` ocupando a tela, com `koin_splash.png`.
- **Organização:** raiz `ConstraintLayout` com 2 elementos. Estrutura mínima: sem botões, apenas a marca durante a espera de 3 segundos.
- **Relação com o código:** inflado por `MainActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `activity_menu.xml`

- **O que representa:** a tela inicial (`MenuActivity`).
- **Principais elementos:** `LinearLayout` com `ScrollView` de conteúdo: saudação (`tvNomeUsuario`), foto de perfil, cartão de saldo (`cardSaldo`, `tvSaldoAtual`, botão `btnVisibilidadeSaldo`, receitas e despesas do mês), cartões de entradas e saídas (`tvEntradas`, `tvSaidas`), lista `containerUltimas` com "Ver todas" e barra de navegação inferior.
- **Organização:** raiz `LinearLayout` com 51 elementos. O saldo vem primeiro e em destaque; a barra inferior fica fixa e leva às demais seções.
- **Relação com o código:** inflado por `MenuActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `activity_meta.xml`

- **O que representa:** a tela de metas (`MetaActivity`).
- **Principais elementos:** Título "Minhas metas", cartão-resumo (`tvTotalGuardado`, `tvObjetivoTotal`, `progressoTotalMetas`, `tvPercentualTotal`), lista `containerMetas`, mensagem `tvSemMetas`, botão `btnCriarMeta` e barra inferior.
- **Organização:** raiz `LinearLayout` com 34 elementos. O resumo geral vem antes da lista; o botão de criar fica no fim e o estado vazio orienta o primeiro passo. Os valores de exemplo do XML (`R$ 1.850,00` etc.) são substituídos em execução.
- **Relação com o código:** inflado por `MetaActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `activity_movimentacao.xml`

- **O que representa:** a tela de histórico (`MovimentacaoActivity`).
- **Principais elementos:** Título "Movimentações", campo de busca `etBuscarMovimentacao`, três filtros (`filtroTodas`, `filtroReceitas`, `filtroDespesas`), bloco "Últimas 3 transações" (`containerMovimentacoes`), bloco "Movimentações anteriores" (`containerTodas`) e barra inferior.
- **Organização:** raiz `LinearLayout` com 36 elementos. Busca e filtros ficam acima da lista, a atualização é imediata, e o histórico completo fica abaixo do resumo.
- **Relação com o código:** inflado por `MovimentacaoActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `activity_nova_movimentacao.xml`

- **O que representa:** a tela de cadastro de movimentação (`NovaMovimentacaoActivity`).
- **Principais elementos:** `ScrollView` com título e botão fechar (`btFechar`), abas `btDespesa`/`btReceita`, cartão de valor (`cardValor`, `etValor`), `etDescricao`, seletor de categoria (`btCategoria`) e de data (`btData`) e botão `btSalvarMovimentacao`.
- **Organização:** raiz `ScrollView` com 25 elementos. Sequência de cima para baixo (tipo → valor → descrição → categoria → data → salvar) guia o preenchimento.
- **Relação com o código:** inflado por `NovaMovimentacaoActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `activity_perfil.xml`

- **O que representa:** a tela de perfil (`PerfilActivity`).
- **Principais elementos:** `ScrollView` com botão fechar, imagem de perfil, nome (`tvNomePerfil`), e-mail (`tvEmailPerfil`), botão "Editar senha", cartão "MINHA CONTA" com "Dados pessoais" (`llDadosPessoais`), "Sair da conta" e "Deletar sua conta".
- **Organização:** raiz `ScrollView` com 24 elementos. As ações comuns ficam no topo; a ação destrutiva fica isolada, em cor própria, no fim.
- **Relação com o código:** inflado por `PerfilActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `activity_relatorios.xml`

- **O que representa:** a tela de relatórios (`RelatoriosActivity`).
- **Principais elementos:** Filtros `btFiltroMes`, `btFiltroTrimestre`, `btFiltroAno`; cartões de receitas, despesas e saldo (`tvRelatorioReceitas`, `tvRelatorioDespesas`, `tvSetaSaldo`, `tvRelatorioSaldo`); gráfico `chartDespesas` (`PieChart`), total e legenda (`containerLegendaCategorias`); bloco de orçamento (`tvPercentualOrcamento`, `progressoOrcamento`, `tvResumoOrcamento`), botão `btGerenciarLimites` e barra inferior.
- **Organização:** raiz `LinearLayout` com 58 elementos. Do resumo numérico para o detalhe visual: totais, gráfico e, por fim, o orçamento. Textos fixos de exemplo são trocados por valores reais ao carregar.
- **Relação com o código:** inflado por `RelatoriosActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `activity_ver_informcoes_conta.xml`

- **O que representa:** a tela de dados pessoais (`VerInformcoesContaActivity`).
- **Principais elementos:** `ScrollView` com logo e cartão com dois blocos somente de leitura: "NOME" (`tvNome`) e "E-MAIL" (`tvEmail`).
- **Organização:** raiz `ScrollView` com 21 elementos. Apresentação simples, sem campos editáveis. O nome do arquivo tem um erro de digitação (`informcoes`).
- **Relação com o código:** inflado por `VerInformcoesContaActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `bottom_sheet_gerenciar_limites.xml`

- **O que representa:** a janela de limites mensais (`GerenciarLimitesBottomSheet`).
- **Principais elementos:** Título, explicação ("Deixe em branco para não ter limite."), lista rolável `containerLimites` e botão `btSalvarLimites`.
- **Organização:** raiz `LinearLayout` com 7 elementos. As linhas são geradas em código a partir de `item_limite_categoria`; o texto de ajuda evita dúvida sobre como remover um limite.
- **Relação com o código:** inflado por `GerenciarLimitesBottomSheet.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `bottom_sheet_guardar_meta.xml`

- **O que representa:** a janela de guardar/retirar valor (`GuardarMetaBottomSheet`).
- **Principais elementos:** Ícone, nome e resumo da meta, abas "Guardar" e "Retirar" (`tvModoGuardar`, `tvModoRetirar`), campo `etValorGuardar` com prefixo "R$" e botão `btConfirmarGuardar`.
- **Organização:** raiz `ScrollView` com 16 elementos. Mostra o contexto da meta antes do valor e alterna entre as duas operações sem sair da janela.
- **Relação com o código:** inflado por `GuardarMetaBottomSheet.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `bottom_sheet_nova_meta.xml`

- **O que representa:** a janela de criação **e de edição** de meta (`NovaMetaBottomSheet` e `EditarMetaBottomSheet`).
- **Principais elementos:** Título e subtítulo (`tvTituloMetaSheet`, `tvSubtituloMetaSheet`), campos `etNomeMeta` e `etValorMeta`, seletor de prazo (`llPrazoMeta`, `tvPrazoMetaSheet`, `btLimparPrazo`), faixa rolável de ícones (`containerIcones`) e botão `btCriarMetaSheet`.
- **Organização:** raiz `ScrollView` com 19 elementos. O mesmo layout atende criar e editar: o Kotlin só troca os textos, o que evita duplicação.
- **Relação com o código:** inflado por `EditarMetaBottomSheet.kt`, `NovaMetaBottomSheet.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `item_limite_categoria.xml`

- **O que representa:** uma linha da janela de limites.
- **Principais elementos:** Nome da categoria (`tvNomeCategoriaLimite`), texto "Gasto este mês" (`tvGastoCategoriaLimite`) e campo `etLimiteCategoria` com prefixo "R$".
- **Organização:** raiz `LinearLayout` com 7 elementos. Ver o gasto ao lado do campo ajuda a escolher um limite realista.
- **Relação com o código:** inflado por `GerenciarLimitesBottomSheet.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `item_meta.xml`

- **O que representa:** o cartão de uma meta, inflado por `MetaActivity`.
- **Principais elementos:** Ícone (`tvIconeMeta`), nome, "guardado de alvo" (`tvValorMeta`), barra `progressoMeta`, percentual (`tvPercentualMeta`), prazo (`tvPrazoMeta`) e botão de opções (`btnOpcoesMeta`).
- **Organização:** raiz `LinearLayout` com 9 elementos. Todas as informações de uma meta em um cartão tocável; o botão de opções fica separado do toque do cartão.
- **Relação com o código:** inflado por `MetaActivity.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

### `item_movimentacao.xml`

- **O que representa:** uma linha de movimentação, inflada por `RenderMovimentacoes.kt`.
- **Principais elementos:** Ícone da categoria (`ivCategoria`), título (`tvTitulo`), subtítulo (`tvSubtitulo`), valor (`tvValor`), lixeira (`ivExcluir`, oculta por padrão) e divisor (`divisor`).
- **Organização:** raiz `LinearLayout` com 9 elementos. É reutilizado no início (sem lixeira) e no histórico (com lixeira): uma só definição visual para os dois.
- **Relação com o código:** inflado por `RenderMovimentacoes.kt`.
- **Experiência do usuário:** reúne o que o usuário precisa em cada etapa, com textos objetivos e botões com cor padronizada (`botao_koin`), reduzindo dúvidas e erros de uso.

---

## 16. Fluxo de funcionamento do sistema

1. **Abertura:** `MainActivity` exibe `koin_splash.png` por 3 segundos e abre `LoginActivity`.
2. **Cadastro:** `CadastroActivity` valida nome, e-mail e senha (`Validacao.kt`), gera o hash com `GerenciadorSeguranca` e grava o usuário via `UsuarioDao`. O e-mail é gravado em minúsculas e é único no banco.
3. **Login:** `LoginActivity` busca o usuário pelo e-mail, confere a senha com `GerenciadorSeguranca.verificar` e guarda os dados em `DadosEntrePaginas` (memória). Em caso de erro, mostra uma mensagem genérica.
4. **Início:** `MenuActivity` calcula, pelos DAOs, receitas e despesas do mês, o saldo disponível (receitas − despesas − valor guardado em metas) e lista as últimas movimentações. O saldo pode ser ocultado.
5. **Movimentações:** `NovaMovimentacaoActivity` grava uma despesa (`DespesasDao`) ou receita (`ReceitaDao`). `MovimentacaoActivity` busca e filtra o histórico (`TransacaoDao`) e permite excluir um registro após confirmação.
6. **Metas:** `MetaActivity` cria, edita e exclui metas pelas janelas inferiores; guardar ou retirar valor atualiza `valorGuardado` dentro de uma transação, e o valor guardado é descontado do saldo disponível.
7. **Relatórios:** `RelatoriosActivity` soma por período (mês, trimestre, ano), desenha o gráfico por categoria e compara o gasto do mês com os limites definidos em `GerenciarLimitesBottomSheet`.
8. **Conta:** `PerfilActivity` leva a `EditarSenhaActivity` (exige a senha atual) e a `VerInformcoesContaActivity`; permite sair (limpa a sessão) ou excluir a conta (apaga também os dados do usuário).
9. **Sessão perdida:** se o sistema encerrar o processo e a memória for apagada, as telas internas chamam `exigirSessao()`, que volta ao login.

---

## 17. Segurança e proteção de dados

### Implementado no código

| Prática | Onde |
|---|---|
| Senha nunca gravada em texto: PBKDF2WithHmacSHA1, 120.000 iterações, sal aleatório de 16 bytes, hash de 256 bits, formato `iteracoes:sal:hash` | `GerenciadorSeguranca.kt` |
| Comparação do hash em tempo constante (`MessageDigest.isEqual`) | `GerenciadorSeguranca.kt` |
| Validação de e-mail (`Patterns.EMAIL_ADDRESS`) e senha com no mínimo 8 caracteres | `Validacao.kt`, telas de cadastro e troca de senha |
| E-mail em minúsculas e único (índice único) | `Usuario.kt`, `UsuarioDao.kt` |
| Mensagem de login genérica (não revela se o e-mail existe) | `LoginActivity.kt` |
| Troca de senha exige a senha atual | `EditarSenhaActivity.kt` |
| Consultas de exclusão/atualização filtram pelo dono do registro (`idFkUsuario`) | DAOs |
| Telas internas exigem sessão ativa e voltam ao login se ela não existir | `Sessao.kt` |
| Telas internas não são exportadas no manifesto (só `MainActivity` é a porta de entrada) | `AndroidManifest.xml` |
| Exclusão da conta remove também os dados do usuário | `UsuarioDao.kt` |
| Operações compostas em transação (`withTransaction`) | `MetaActivity.kt` e demais |

### Limitações conhecidas (não implementadas)

- **Banco sem criptografia:** os dados financeiros ficam em texto claro no SQLite local.
- **`allowBackup="true"`:** o backup do Android pode incluir o banco.
- **Sem limite de tentativas de login.**
- **Sessão só em memória:** não há token nem persistência de login.
- **PBKDF2 com SHA-1:** escolhido por compatibilidade com `minSdk 24`; SHA-256 exige API 26.
- **Sem chaves estrangeiras** declaradas: a ligação com o usuário é um `Int` simples, e a limpeza dos dados na exclusão depende do código.
- **`fallbackToDestructiveMigration`:** uma mudança de versão do banco apaga os dados.

### Boas práticas recomendadas

Criptografar o banco (por exemplo, SQLCipher), desativar ou restringir o backup, limitar tentativas de login, declarar `@ForeignKey` com `onDelete = CASCADE` e escrever migrações reais antes de qualquer uso fora do contexto acadêmico.

---

## 18. Valor de negócio

| Funcionalidade (implementada) | Valor para o usuário |
|---|---|
| Cadastro e login com senha protegida | Cada pessoa vê apenas os próprios dados |
| Receitas e despesas por categoria | Registro organizado do dia a dia financeiro |
| Saldo do mês no início, com opção de ocultar | Visão imediata da situação e privacidade em público |
| Histórico com busca, filtros e exclusão com confirmação | Encontrar e corrigir lançamentos com segurança |
| Metas com prazo e depósitos/retiradas | Planejar objetivos e acompanhar o progresso |
| Relatórios por mês, trimestre e ano, com gráfico | Entender para onde o dinheiro vai |
| Limites mensais por categoria | Controlar gastos e perceber excessos |
| Valores em centavos (`Long`) e formatação em reais | Cálculos sem erros de arredondamento |

---

## 19. Manutenção e evolução

**Organização que facilita manter:** pacotes separados por função (`data`, `dialog`, `utils`, `security`, `activities`), regras repetidas centralizadas em `utils` e recursos visuais reaproveitados (`botao_koin`, `item_*`).

**Pontos de atenção:**

- Remover os drawables sem referência: `bg_icone_categoria`, `fundo_rosa`, `fundo_verde_claro`, `grafico_despesas`, `principal_koin` e `ic_launcher_foreground`.
- Reduzir `koin_splash.png` (cerca de 10 MB).
- Corrigir os nomes com erro de digitação (`funco_saldo_negativo`, `VerInformcoesConta`/`activity_ver_informcoes_conta`).
- Mover textos fixos para `strings.xml`, hoje com apenas `app_name`.
- Testes: os arquivos `ExampleUnitTest` e `ExampleInstrumentedTest` são apenas modelos; não há testes reais.

**Evoluções possíveis (não implementadas):** repositórios e ViewModels, injeção de dependência, migrações do Room, criptografia do banco, exportação de relatórios e persistência segura da sessão.

---

## 20. Conclusão

O KOIN é um aplicativo Android nativo, em Kotlin, que entrega um fluxo completo de finanças pessoais: conta protegida, receitas e despesas, metas, relatórios e limites por categoria, tudo salvo localmente com Room. A arquitetura é simples e direta (Activity → DAO → SQLite), adequada ao escopo acadêmico, e a documentação acima descreve cada arquivo do projeto conforme o código real. As limitações de segurança e de arquitetura estão declaradas de forma transparente, com caminhos claros para evolução.
