# P1 - Projeto eleições Pokémon - Geração I

Este projeto é um aplicativo Android desenvolvido em Kotlin para simular uma pesquisa eleitoral com temática de Pokémon da Geração I. A aplicação coleta dados de entrevistados, registra a intenção de voto, salva problemas prioritários, registra a localização do usuário e apresenta gráficos com os resultados.

A estrutura foi pensada para funcionar como uma pesquisa de opinião, com fluxo de telas que vai desde o login até a geração de relatórios estatísticos.

---

## Visão geral da aplicação

A aplicação segue um fluxo simples:

1. Tela inicial de abertura
2. Login do usuário
3. Caso seja administrador:
   - acesso ao menu administrativo
   - consulta do total de entrevistados
   - visualização dos dados
   - limpeza dos dados salvos
4. Caso seja entrevistado:
   - digitar candidato espontâneo
   - escolher voto estimulado
   - selecionar 3 problemas principais
   - informar nome e telefone
   - registrar localização
   - salvar os dados no banco local
5. Visualização de relatórios:
   - top 5 candidatos espontâneos
   - votos estimulados
   - problemas mais citados

---

## Estrutura do projeto

```text
eleicao/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/eleicao/
│           │       ├── MainActivity.kt
│           │       ├── LoginActivity.kt
│           │       ├── AdminMenuActivity.kt
│           │       ├── EleitoresActivity.kt
│           │       ├── EspontaneoActivity.kt
│           │       ├── EstimuladaActivity.kt
│           │       ├── ProblemasActivity.kt
│           │       ├── DadosEntrevistadoActivity.kt
│           │       ├── ResultadoActivity.kt
│           │       ├── Redirecionar.kt
│           │       ├── Voltar.kt
│           │       └── data/
│           │           ├── AppData.kt
│           │           ├── Entrevistado.kt
│           │           ├── Resposta.kt
│           │           ├── Endereco.kt
│           │           ├── PesquisaAtual.kt
│           │           ├── EntrevistadoDao.kt
│           │           ├── RespostaDao.kt
│           │           ├── EnderecoDao.kt
│           │           ├── EntrevistadoComLocalizacao.kt
│           │           ├── VotoContagem.kt
│           │           └── ...
│           └── res/
│               └── layout/
│                   ├── activity_main.xml
│                   ├── activity_login.xml
│                   ├── activity_admin_menu.xml
│                   ├── activity_eleitores.xml
│                   ├── activity_espontaneo.xml
│                   ├── activity_estimulada.xml
│                   ├── activity_problemas.xml
│                   ├── activity_dados_entrevistado.xml
│                   └── activity_resultado.xml

Pasta main: lógica das Activities
A pasta main/java/com/example/eleicao contém as telas e os fluxos da aplicação. Cada arquivo .kt representa uma Activity ou utilitário.

1. MainActivity.kt
Responsável pela tela inicial da aplicação.

Inicializa a tela com activity_main.xml
Usa lifecycleScope e delay(3000) para manter a splash screen por 3 segundos
Após esse tempo, redireciona automaticamente para LoginActivity
Essa tela funciona como abertura do app, sendo semelhante a uma splash screen.

2. LoginActivity.kt
Controla o login do sistema.

Busca os campos de usuário e senha (EditText)
Valida as credenciais:
admin / admin
entrevistado / entrevistado
Caso seja usuário admin:
redireciona para AdminMenuActivity
Caso seja entrevistado:
redireciona para EspontaneoActivity
Caso contrário:
exibe mensagem de erro com Toast
Essa activity funciona como a porta de entrada do sistema.

3. AdminMenuActivity.kt
Tela administrativa do app.

Carrega o banco usando AppDatabase.getDatabase(this)
Exibe o total de entrevistados em tempo real
Possui três ações principais:
visualizar eleitores
visualizar resultados
limpar todos os dados
Usa coroutines para consultar o banco em segundo plano e atualizar a interface
Principais funções:

atualizarTotal(): consulta a quantidade de entrevistados
btLimpar.setOnClickListener: apaga os dados das tabelas entrevistados e respostas
4. EleitoresActivity.kt
Responsável por listar os entrevistados cadastrados.

Consulta o banco usando buscarEntrevistadosComLocalizacao()
Recupera nome, telefone e endereço
Exibe os resultados em um LinearLayout dentro de um ScrollView
Também mostra a data de finalização da pesquisa
Ela oferece ao administrador uma visão dos dados coletados e da localização associada a cada entrevista.

5. EspontaneoActivity.kt
Tela de pesquisa espontânea.

Usuário digita o nome do candidato que ele lembra sem ajuda
Valida se o campo não está vazio
Guarda esse valor em PesquisaAtual.candidatoEspontaneo
Redireciona para EstimuladaActivity
Essa etapa representa a primeira parte da intenção de voto.

6. EstimuladaActivity.kt
Tela de voto estimulado.

Exibe candidatos em cards ou blocos com imagem e texto
Permite a seleção de um candidato
Também oferece opções de voto:
Branco
Nulo
Não sei
Guarda a escolha em PesquisaAtual.voto
Redireciona para ProblemasActivity
Aqui a lógica visual é importante:

quando uma opção é selecionada, o item recebe destaque visual
o RadioGroup limpa a seleção anterior para manter consistência na interface
7. ProblemasActivity.kt
Tela para selecionar os três principais problemas do entrevistado.

Possui vários CheckBox para temas como:
Saúde
Violência
Economia
Educação
Corrupção
Desemprego
Fome
Desigualdade
Má administração
Salário
Valida que exatamente 3 itens sejam selecionados
Salva os problemas em PesquisaAtual.problemas
Redireciona para DadosEntrevistadoActivity
Essa parte é essencial para a análise de opinião pública.

8. DadosEntrevistadoActivity.kt
Tela final de coleta dos dados do entrevistado.

Recebe:
nome
telefone
Valida o telefone
Salva os dados temporários em PesquisaAtual
Solicita permissão de localização
Obtém a localização atual usando FusedLocationProviderClient
Converte latitude/longitude em endereço com Geocoder
Salva tudo no banco:
entrevistado
resposta
localização
Essa é a etapa final da pesquisa. Depois disso, os dados ficam persistidos localmente.

9. ResultadoActivity.kt
Tela de relatórios e gráficos.

Consulta o banco para obter:
quantidade total de respostas
top 5 candidatos espontâneos
contagem por voto estimulado
temas mais citados
Usa gráficos do MPAndroidChart:
BarChart para candidatos espontâneos
PieChart para votação estimulada
BarChart para problemas
A função contarTemas() separa os temas e conta quantas vezes apareceram
Essa tela permite ao administrador interpretar os resultados em formato visual.

10. Redirecionar.kt
Arquivo utilitário para navegação entre Activities.

Kotlin
fun Activity.direcionando(pagAtual: Activity, pageProx: Class<*>){
    val intent = Intent(pagAtual, pageProx)
    startActivity(intent)
}
Esse método encapsula a abertura de uma nova Activity, evitando repetição de código no projeto.

11. Voltar.kt
Arquivo utilitário para finalizar a Activity atual.

Kotlin
fun Activity.voltando(){
    finish()
}
Esse método é usado para voltar para a tela anterior.

Pasta data: explicação aprofundada
A pasta data contém a camada de persistência do aplicativo. Ela é responsável por armazenar e consultar os dados de forma organizada.

1. AppData.kt
Arquivo principal do banco Room.

Anotado com @Database
Define as entidades:
Entrevistado
Resposta
Endereco
Cria a base de dados SQLite:
nome: eleicao_database
Usa padrão Singleton para manter uma única instância do banco
Código principal:

Kotlin
@Database(
    entities = [Entrevistado::class, Resposta::class, Endereco::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()
Esse arquivo é o coração do armazenamento local do aplicativo.

2. Entrevistado.kt
Representa a tabela de entrevistados.

Kotlin
@Entity(tableName = "entrevistados")
data class Entrevistado(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val telefone: String,
    val dataFinalizacao: Long = System.currentTimeMillis()
)
Campos:

id: chave primária auto incrementada
nome: nome do entrevistado
telefone: telefone informado
dataFinalizacao: data e hora em milissegundos
Esse objeto representa um registro da pessoa entrevistada.

3. Resposta.kt
Arquivo responsável pela tabela de respostas.

Kotlin
@Entity(tableName = "respostas")
data class Resposta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val problemas: String,
    val voto: String,
    val candidatoEspontaneo: String
)
Armazena:

problemas selecionados
voto estimulado
candidato espontâneo
As respostas ficam separadas em uma tabela específica para que o sistema possa gerar gráficos e estatísticas.

4. Endereco.kt
Representa a tabela de localizações.

Kotlin
@Entity(tableName = "localizacoes")
data class Endereco(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val entrevistadoId: Int,
    val latitude: Double,
    val longitude: Double,
    val endereco: String
)
Campos:

entrevistadoId: referência ao entrevistado
latitude e longitude: localização exata
endereco: endereço em texto formatado
Essa tabela relaciona a pesquisa ao local em que a entrevista foi concluída.

5. PesquisaAtual.kt
Objeto singleton usado para guardar dados temporários durante a entrevista.

Kotlin
object PesquisaAtual {
    var candidatoEspontaneo: String = ""
    var voto: String = ""
    var problemas: String = ""
    var nome: String = ""
    var telefone: String = ""
}
Esse objeto funciona como memória temporária do fluxo da aplicação. Ele evita que os dados tenham de ser passados de uma tela para outra por meio de Intent.

Quando a pesquisa é salva, o objeto é limpo com:

Kotlin
fun limpar()
6. EntrevistadoDao.kt
Interface DAO para a entidade Entrevistado.

Responsabilidades:

inserir entrevista
buscar todos
contar a quantidade
apagar todos
montar a listagem com localização
Exemplo de query importante:

Kotlin
@Query("""
    SELECT 
        e.id AS id,
        e.nome AS nome,
        e.telefone AS telefone,
        e.dataFinalizacao AS dataFinalizacao,
        COALESCE(l.endereco, 'Endereço não cadastrado') AS endereco
    FROM entrevistados e
    LEFT JOIN localizacoes l ON e.id = l.entrevistadoId
    ORDER BY e.id DESC
""")
suspend fun buscarEntrevistadosComLocalizacao(): List<EntrevistadoComLocalizacao>
Essa query faz um JOIN entre as tabelas entrevistados e localizacoes, permitindo mostrar os dados do entrevistado junto com o endereço.

7. RespostaDao.kt
DAO responsável por consultar e contar as respostas da pesquisa.

Funções:

inserir uma resposta
contar quantas respostas existem
apagar todas as respostas
contar votos por categoria
listar os 5 candidatos espontâneos mais frequentes
listar todos os problemas
Exemplo:

Kotlin
@Query("""
    SELECT voto, COUNT(*) AS quantidade
    FROM respostas
    GROUP BY voto
""")
suspend fun contagemPorVoto(): List<VotoContagem>
Esse método permite calcular quantos votos cada opção recebeu.

8. EnderecoDao.kt
DAO para as localizações.

Responsabilidades:

inserir localização
apagar todos os dados de localização
Kotlin
@Dao
interface EnderecoDao {
    @Insert
    suspend fun inserir(Localização: Endereco)

    @Query("DELETE FROM localizacoes")
    suspend fun deletarTodos()
}
9. EntrevistadoComLocalizacao.kt
Classe que representa um dado combinando entrevistado + endereço.

Kotlin
data class EntrevistadoComLocalizacao(
    val id: Int,
    val nome: String,
    val telefone: String,
    val endereco: String,
    val dataFinalizacao: Long
)
Essa classe é usada para apresentação na tela de eleitores.

10. VotoContagem.kt
Classe auxiliar para representar a contagem de votos ou temas.

Kotlin
data class VotoContagem(
    val voto: String,
    val quantidade: Int
)
Ela é utilizada para alimentar os gráficos e as estatísticas.

Como o Room Database funciona no projeto
O Room é uma biblioteca do Android que facilita o acesso ao SQLite de forma mais segura e organizada.

Funcionamento no projeto
O fluxo é o seguinte:

AppDatabase.getDatabase(context) verifica se a instância do banco já existe
Caso não exista, cria uma nova instância com Room.databaseBuilder(...)
As entidades (Entrevistado, Resposta, Endereco) viram tabelas no banco
Os DAOs expõem operações como inserir, contar, buscar e deletar
As Activities acessam o banco em coroutines com Dispatchers.IO
O aplicativo não roda operações pesadas na thread principal
Exemplo do processo da pesquisa:

o usuário responde às perguntas
os dados ficam em PesquisaAtual
quando finaliza, DadosEntrevistadoActivity salva:
Entrevistado
Resposta
Endereco
ResultadoActivity consulta o banco e gera gráficos
Isso garante que os dados sejam persistidos mesmo quando o usuário fecha o app.

Explicação da pasta res/layout
A pasta res/layout contém todas as telas em XML. Cada arquivo representa uma Activity e define os componentes visuais da interface.

1. activity_main.xml
Tela inicial de abertura.

Componentes:

ConstraintLayout: layout principal
ImageView: imagem central da aplicação (pokemoin)
Função:

exibir a imagem de abertura do app
2. activity_login.xml
Tela de login.

Componentes:

ConstraintLayout
EditText: campos para usuário e senha
Button: botões para acessar e sair
TextView: títulos dos campos
ImageView: imagem do Pokémon
Função:

autenticar o usuário
permitir acesso ao painel administrativo ou ao fluxo de pesquisa
3. activity_admin_menu.xml
Menu do administrador.

Componentes:

ConstraintLayout
TextView: título e contador total
Button: opções de eleitores, resultados e limpar dados
ImageButton: botão de voltar
Função:

centralizar as funções do painel administrativo
4. activity_eleitores.xml
Tela de listagem dos entrevistados.

Componentes:

LinearLayout: container principal vertical
ImageButton: botão voltar
TextView: título da tela
ScrollView: permite rolar a lista
LinearLayout: container dinamicamente preenchido com cada entrevistado
Função:

mostrar todos os dados cadastrados de forma rolável
5. activity_espontaneo.xml
Tela de pesquisa espontânea.

Componentes:

ConstraintLayout
ImageButton: voltar
TextView: títulos e instruções
EditText: campo para digitar o nome do candidato
Button: confirmar
Função:

coletar a primeira resposta de intenção de voto
6. activity_estimulada.xml
Tela de voto estimulado.

Componentes:

ConstraintLayout
LinearLayout: blocos de candidatos
MaterialCardView: cards para os candidatos
ImageView: imagens dos Pokémon
TextView: nomes dos candidatos
RadioGroup
RadioButton: branco, nulo e não sei
Button: confirmar
Função:

permitir que o entrevistado escolha um candidato ou opção "branco/nulo/não sei"
7. activity_problemas.xml
Tela de seleção dos principais problemas.

Componentes:

ConstraintLayout
ScrollView: necessário para rolar vários itens
LinearLayout: layout vertical
TextView: título da tela
CheckBox: itens de problemas
Button: confirmar
Função:

coletar exatamente três problemas principais escolhidos pelo entrevistado
8. activity_dados_entrevistado.xml
Tela de coleta de dados pessoais.

Componentes:

ConstraintLayout
TextView: labels de nome e telefone
EditText: campos para nome e telefone
Button: finalizar pesquisa
Função:

receber os dados do entrevistado e concluir a coleta
9. activity_resultado.xml
Tela de relatórios.

Componentes:

ScrollView: para permitir a rolagem das estatísticas
LinearLayout: empilha as partes da tela
ImageButton: voltar
TextView: títulos das seções
BarChart: gráficos de barras
PieChart: gráfico circular
View: espaço final para ajuste visual
Função:

apresentar os resultados das pesquisas em gráficos
Componentes visuais mais utilizados no projeto
Durante o desenvolvimento, os layouts usam vários componentes do Android, dentre eles:

TextView: textos e títulos
EditText: entradas de texto
Button: ações do usuário
ImageView: imagens
ImageButton: botão com ícone
ScrollView: rolagem de conteúdo
LinearLayout: organização em linha ou coluna
ConstraintLayout: layout base
CheckBox: seleção de múltiplos itens
RadioGroup e RadioButton: seleção exclusiva
MaterialCardView: cards visuais
BarChart e PieChart: visualização de resultados
Conclusão
O projeto é uma aplicação Android completa de pesquisa eleitoral temática, com:

fluxo de telas bem definido
armazenamento local com Room Database
coleta de dados em múltiplas etapas
validações de formulário
uso de localização do aparelho
geração de relatórios e gráficos
A organização foi pensada para separar claramente:

interface (layout)
regras de negócio e navegação (Activities)
persistência de dados (data)
Esse é um projeto de fácil manutenção e extensão, sendo ideal para estudos de Android, Kotlin, Room, persistência local e UI com componentes visuais.

Observações finais
Esse aplicativo demonstra uma arquitetura simples e funcional:

o usuário percorre telas em sequência
os dados são montados em um objeto temporário
o banco local guarda as informações do entrevistado
os resultados são processados e visualizados em gráficos



