package com.example.koin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.koin.data.response.TodasInformacaoDespesa
import com.example.koin.data.response.TotalCategoria

@Dao
interface DespesasDao{
    @Query("SELECT * FROM despesas WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa")
    suspend fun todasInformacoesDespesasMes(idPassado: Int, mesPesquisa: String) : TodasInformacaoDespesa?

    @Query("SELECT * FROM despesas WHERE id = :idDespesa")
    suspend fun despesaEspecifica(idDespesa: Int) : TodasInformacaoDespesa?

    @Query("SELECT COALESCE(SUM(valorDespesa), 0.0) as total_despesa FROM despesas WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa")
    suspend fun totalDespesaMes(idPassado: Int, mesPesquisa: String) : Double

    @Query("SELECT nomeCategoria,COALESCE(SUM(valorDespesa), 0.0) as total_categoria FROM despesas WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa GROUP BY nomeCategoria")
    suspend fun totalPorCategoriaMes(idPassado: Int, mesPesquisa: String) : TotalCategoria?

    @Insert
    suspend fun cadastrarDespesa(despesas: Despesas)

    @Query("UPDATE despesas SET nomeCategoria = :novaCategoria,valorDespesa = :novoValor, descricao = :novaDescricao WHERE id = :idDespesa")
    suspend fun atualizarDespesa(novaCategoria: String, novoValor: Double, novaDescricao: String ,idDespesa: Int): Int

    @Query("DELETE FROM despesas where id = :idDespesa")
    suspend fun deletarDespesa(idDespesa: Int) : Int
}