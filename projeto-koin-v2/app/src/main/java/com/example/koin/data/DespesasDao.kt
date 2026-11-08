package com.example.koin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.koin.data.response.TotalCategoria
import java.util.Date

@Dao
interface DespesasDao{
    @Insert
    suspend fun cadastrarDespesa(despesas: Despesas)

    @Query("""SELECT COALESCE(SUM(valorDespesa), 0.0)
    FROM despesas
    WHERE idFkUsuario = :idPassado
    AND dataDespesaCadastrada >= :dataInicio
    AND dataDespesaCadastrada < :dataFim""")
    suspend fun totalDespesasPeriodo(idPassado: Int, dataInicio: Date, dataFim: Date): Double

    @Query("""SELECT nomeCategoria,COALESCE(SUM(valorDespesa), 0.0) AS total_categoria
    FROM despesas
    WHERE idFkUsuario = :idPassado
    AND dataDespesaCadastrada >= :dataInicio
    AND dataDespesaCadastrada < :dataFim
    GROUP BY nomeCategoria""")
    suspend fun totalPorCategoriaPeriodo(idPassado: Int, dataInicio: Date, dataFim: Date): List<TotalCategoria>

    @Query("SELECT COALESCE(SUM(valorDespesa), 0) FROM despesas WHERE idFkUsuario = :idUsuario")
    suspend fun totalDespesas(idUsuario: Int): Long

    @Query("DELETE FROM despesas WHERE id = :idDespesa AND idFkUsuario = :idUsuario")
    suspend fun excluirDespesa(idDespesa: Int, idUsuario: Int): Int

    @Query("DELETE FROM despesas WHERE idFkUsuario = :idUsuario")
    suspend fun excluirTodasDoUsuario(idUsuario: Int)
}