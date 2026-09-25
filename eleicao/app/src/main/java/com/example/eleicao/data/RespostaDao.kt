package com.example.eleicao.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RespostaDao {

    @Insert
    suspend fun inserir(resposta: Resposta)

    @Query("SELECT COUNT(*) FROM respostas")
    suspend fun quantidadeRespostas(): Int

    @Query("DELETE FROM respostas")
    suspend fun deletarTodos()

    @Query("""
        SELECT voto, COUNT(*) AS quantidade
        FROM respostas
        GROUP BY voto
    """)
    suspend fun contagemPorVoto(): List<VotoContagem>

    @Query("""
        SELECT candidatoEspontaneo AS voto, COUNT(*) AS quantidade
        FROM respostas
        WHERE candidatoEspontaneo != ''
        GROUP BY candidatoEspontaneo
        ORDER BY quantidade DESC
        LIMIT 5
    """)
    suspend fun top5Espontaneos(): List<VotoContagem>

    @Query("SELECT problemas FROM respostas")
    suspend fun listarTodosProblemas(): List<String>
}