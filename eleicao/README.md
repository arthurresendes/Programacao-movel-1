# P1 - Projeto eleições Pokémon - Geração I

---

## Data

Diretorio responsavel por fazer criação e devido controle ao banco de dados
Arquivos:
  - AppData.kt: Responsavel por criar as tabelas e o banco utilizando o RoomDatabase
  - Endereco.kt: Responsavel por definir os atrbitus da tabela localizações, com os seguintes atributos: id incremental, entrevistadoId, latitude, longitude e o endereco
  - Entrevistado.kt: Defini os atributos para tabela entrevistados onde armazena os valores: nome, telefone, data e id incremental
  - respostas.kt: Salva respostas dos entrevistados, com os seguintes valores: id incremental, problemas separados por virgula, voto (estimulado) e candidatoEspontaneo. Obs: Não armazenamos um campo para ligar com o idCandidato, pois voto é confidencial
  - VotoContagem.kt: Data classe responsavel por armazenar a quntidade por voto
  - PesquisaAtual.kt: Responsavel por garantir que dados quando for ser preenchido estejam vazios e pronto para nova resposta
  - EnderecoDAO.kt: Faz inserção de localização e Query de deletar localização
  - EntrevistadoComLocalizacao.kt: Data class base com os atributos do entrevistado
  - EntrevistadoDAO.kt: Utilizado para inserir entrevistado, selecionar todos os entrevistados, contagem de entrevistados, deletar entrevistados e fazer busca por meio de join entre tabelas para pegar localização de tal entrevistado
  - RespostaDAO.kt: Inserção de uma resposta, contar total de respostas, deletar respostas armazenadas, query para agrupar uma contagem por voto, query onde pega os top 5 dos candidatos espontaneo mais votados e uma query onde seleciona os problemas da tabela de respostas
