package com.example.koin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface ReceitaDao {
    @Insert
    suspend fun cadastrarReceita(receita: Receita)

    @Query("DELETE FROM receita WHERE id = :idReceita AND idFkUsuario = :idUsuario")
    suspend fun excluirReceita(idReceita: Int, idUsuario: Int): Int

     @Query("""SELECT COALESCE(SUM(valor), 0.0)
    FROM receita
    WHERE idFkUsuario = :idPassado
    AND dataReceitaCadastrada >= :dataInicio
    AND dataReceitaCadastrada < :dataFim""")
    suspend fun totalReceitasPeriodo(idPassado: Int, dataInicio: Date, dataFim: Date): Double

    @Query("SELECT COALESCE(SUM(valor), 0) FROM receita WHERE idFkUsuario = :idUsuario")
    suspend fun totalReceitas(idUsuario: Int): Long

    @Query("DELETE FROM receita WHERE idFkUsuario = :idUsuario")
    suspend fun excluirTodasDoUsuario(idUsuario: Int)
}