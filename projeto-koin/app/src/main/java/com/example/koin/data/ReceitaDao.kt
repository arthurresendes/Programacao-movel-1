package com.example.koin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.koin.data.response.TodasInformacaoReceita
import com.example.koin.data.response.TotalCategoria
import com.example.koin.data.response.UltimaTransacao

@Dao
interface ReceitaDao {

    @Query("SELECT * FROM receita WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa")
    suspend fun todasInformacoesReceitasMes(idPassado: Int, mesPesquisa: String) : TodasInformacaoReceita?

    @Query("SELECT * FROM receita WHERE id = :idReceita")
    suspend fun receitaEspecifica(idReceita: Int) : TodasInformacaoReceita?

    @Query("SELECT COALESCE(SUM(valor), 0.0) as total_receita FROM receita WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa")
    suspend fun totalReceitaMes(idPassado: Int,mesPesquisa: String) : Double

    @Query("SELECT nomeCategoria,COALESCE(SUM(valor), 0.0) as total_categoria FROM receita WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa GROUP BY nomeCategoria")
    suspend fun totalPorCategoriaMes(idPassado: Int,mesPesquisa: String) : TotalCategoria?

    @Query("""
        SELECT * FROM (
            SELECT id, nomeMes, nomeCategoria, valor, NULL AS descricao, idFkUsuario,
                   dataReceitaCadastrada AS data_transacao, 'receita' AS tipo
            FROM receita
            WHERE idFkUsuario = :user_id

            UNION ALL

            SELECT id, nomeMes, nomeCategoria, valorDespesa AS valor, descricao, idFkUsuario,
                   dataDespesaCadastrada AS data_transacao, 'despesa' AS tipo
            FROM despesas
            WHERE idFkUsuario = :user_id
        ) AS transacoes
        WHERE (:tipo = 'todas' OR tipo = :tipo)
          AND (nomeCategoria LIKE '%' || :busca || '%'
               OR IFNULL(descricao, '') LIKE '%' || :busca || '%')
        ORDER BY data_transacao DESC, id DESC
        LIMIT :limite
    """)
    suspend fun listar(user_id: Int, tipo: String, busca: String, limite: Int): List<UltimaTransacao>

    @Insert
    suspend fun cadastrarReceita(receita: Receita)

    @Query("UPDATE receita SET nomeCategoria = :novaCategoria,valor = :novoValor WHERE id = :idReceita")
    suspend fun atualizarReceita(novaCategoria: String, novoValor: Double,idReceita: Int) : Int

    @Query("DELETE FROM receita where id = :idReceita")
    suspend fun deletarReceita(idReceita: Int) : Int

}
