package com.example.koin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrcamentoDao {

    @Query("""
        SELECT * FROM orcamento
        WHERE idFkUsuario = :idUsuario
        ORDER BY nomeCategoria ASC""")
    suspend fun listarOrcamentos(idUsuario: Int): List<Orcamento>

    @Query("""
        SELECT COALESCE(SUM(valorLimite), 0.0)
        FROM orcamento
        WHERE idFkUsuario = :idUsuario""")
    suspend fun totalLimites(idUsuario: Int): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarOrcamento(orcamento: Orcamento)

    @Query("""
        DELETE FROM orcamento
        WHERE id = :idOrcamento
        AND idFkUsuario = :idUsuario""")
    suspend fun excluirOrcamento(idOrcamento: Int, idUsuario: Int)


    @Query("""
        DELETE FROM orcamento
        WHERE idFkUsuario = :idUsuario
        AND nomeCategoria = :nomeCategoria""")
    suspend fun excluirPorCategoria(idUsuario: Int, nomeCategoria: String)

    @Query("DELETE FROM orcamento WHERE idFkUsuario = :idUsuario")
    suspend fun excluirTodasDoUsuario(idUsuario: Int)
}