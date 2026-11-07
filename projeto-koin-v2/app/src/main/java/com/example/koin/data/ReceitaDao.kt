package com.example.koin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.koin.data.response.TodasInformacaoReceita
import java.util.Date

@Dao
interface ReceitaDao {

    @Query("SELECT * FROM receita WHERE id = :idReceita")
    suspend fun receitaEspecifica(idReceita: Int) : TodasInformacaoReceita?

    @Insert
    suspend fun cadastrarReceita(receita: Receita)

    @Query("UPDATE receita SET nomeCategoria = :novaCategoria, valor = :novoValor, descricao = :novaDescricao WHERE id = :idReceita")
    suspend fun atualizarReceita(novaCategoria: String, novoValor: Long, novaDescricao: String?, idReceita: Int) : Int

    @Query("DELETE FROM receita where id = :idReceita")
    suspend fun deletarReceita(idReceita: Int) : Int

     @Query("""SELECT COALESCE(SUM(valor), 0.0)
    FROM receita
    WHERE idFkUsuario = :idPassado
    AND dataReceitaCadastrada >= :dataInicio
    AND dataReceitaCadastrada < :dataFim""")
    suspend fun totalReceitasPeriodo(idPassado: Int, dataInicio: Date, dataFim: Date): Double

    @Query("DELETE FROM receita WHERE idFkUsuario = :idUsuario")
    suspend fun excluirTodasDoUsuario(idUsuario: Int)
}