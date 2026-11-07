package com.example.koin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.koin.data.response.TodasInformacaoDespesa
import com.example.koin.data.response.TotalCategoria
import java.util.Date

@Dao
interface DespesasDao{
    @Query("SELECT * FROM despesas WHERE id = :idDespesa")
    suspend fun despesaEspecifica(idDespesa: Int) : TodasInformacaoDespesa?
    @Insert
    suspend fun cadastrarDespesa(despesas: Despesas)

    @Query("UPDATE despesas SET nomeCategoria = :novaCategoria,valorDespesa = :novoValor, descricao = :novaDescricao WHERE id = :idDespesa")
    suspend fun atualizarDespesa(novaCategoria: String, novoValor: Long, novaDescricao: String ,idDespesa: Int): Int

    @Query("DELETE FROM despesas where id = :idDespesa")
    suspend fun deletarDespesa(idDespesa: Int) : Int

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

    @Query("DELETE FROM despesas WHERE idFkUsuario = :idUsuario")
    suspend fun excluirTodasDoUsuario(idUsuario: Int)
}