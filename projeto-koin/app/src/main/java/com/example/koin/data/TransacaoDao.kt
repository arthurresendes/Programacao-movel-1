package com.example.koin.data

import androidx.room.Dao
import androidx.room.Query
import com.example.koin.data.response.UltimaTransacao

@Dao
interface TransacaoDao {
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
}
