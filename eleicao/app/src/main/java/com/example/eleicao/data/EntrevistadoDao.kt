package com.example.eleicao.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EntrevistadoDao {

    @Insert
    suspend fun inserir(entrevistado: Entrevistado): Long

    @Query("SELECT * FROM entrevistados")
    suspend fun buscarTodos(): List<Entrevistado>

    @Query("SELECT COUNT(*) FROM entrevistados")
    suspend fun quantidadeEntrevistados(): Int

    @Query("DELETE FROM entrevistados")
    suspend fun deletarTodos()

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
}