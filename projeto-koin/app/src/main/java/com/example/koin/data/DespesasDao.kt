package com.example.koin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DespesasDao{
    @Query("SELECT * FROM despesas WHERE idFkUsuario = :idPassado")
    suspend fun todasInformacoesDespesas(idPassado: Int)

    @Query("SELECT COALESCE(SUM(valorDespesa), 0.0) as total_despesa FROM despesas WHERE idFkUsuario = :idPassado")
    suspend fun totalDespesa(idPassado: Int)

    @Query("SELECT nomeCategoria,COALESCE(SUM(valorDespesa), 0.0) as total_categoria FROM despesas WHERE idFkUsuario = :idPassado GROUP BY nomeCategoria")
    suspend fun totalPorCategoria(idPassado: Int)

    @Insert
    suspend fun cadastrarDespesa(despesas: Despesas)

    @Query("UPDATE despesas SET nomeCategoria = :novaCategoria,valorDespesa = :novoValor, descricao = :novaDescricao WHERE id = :idDespesa")
    suspend fun atualizarDespesa(novaCategoria: String, novoValor: Double, novaDescricao: String ,idDespesa: Int)

    @Query("DELETE FROM despesas where id = :idDespesa")
    suspend fun deletarDespesa(idDespesa: Int)
}