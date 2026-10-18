package com.example.koin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DespesasDao{
    @Query("SELECT * FROM despesas WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa")
    suspend fun todasInformacoesDespesasMes(idPassado: Int, mesPesquisa: String)

    @Query("SELECT * FROM despesas WHERE id = :idDespesa")
    suspend fun despesaEspecifica(idDespesa: Int)

    @Query("SELECT COALESCE(SUM(valorDespesa), 0.0) as total_despesa FROM despesas WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa")
    suspend fun totalDespesaMes(idPassado: Int, mesPesquisa: String)

    @Query("SELECT nomeCategoria,COALESCE(SUM(valorDespesa), 0.0) as total_categoria FROM despesas WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa GROUP BY nomeCategoria")
    suspend fun totalPorCategoriaMes(idPassado: Int, mesPesquisa: String)

    @Insert
    suspend fun cadastrarDespesa(despesas: Despesas)

    @Query("UPDATE despesas SET nomeCategoria = :novaCategoria,valorDespesa = :novoValor, descricao = :novaDescricao WHERE id = :idDespesa")
    suspend fun atualizarDespesa(novaCategoria: String, novoValor: Double, novaDescricao: String ,idDespesa: Int)

    @Query("DELETE FROM despesas where id = :idDespesa")
    suspend fun deletarDespesa(idDespesa: Int)
}