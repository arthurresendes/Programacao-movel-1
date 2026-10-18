package com.example.koin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReceitaDao {

    @Query("SELECT * FROM receita WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa")
    suspend fun todasInformacoesReceitasMes(idPassado: Int, mesPesquisa: String)

    @Query("SELECT * FROM receita WHERE id = :idReceita")
    suspend fun receitaEspecifica(idReceita: Int)

    @Query("SELECT COALESCE(SUM(valor), 0.0) as total_receita FROM receita WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa")
    suspend fun totalReceitaMes(idPassado: Int,mesPesquisa: String)

    @Query("SELECT nomeCategoria,COALESCE(SUM(valor), 0.0) as total_categoria FROM receita WHERE idFkUsuario = :idPassado AND nomeMes = :mesPesquisa GROUP BY nomeCategoria")
    suspend fun totalPorCategoriaMes(idPassado: Int,mesPesquisa: String)

    @Insert
    suspend fun cadastrarReceita(receita: Receita)

    @Query("UPDATE receita SET nomeCategoria = :novaCategoria,valor = :novoValor WHERE id = :idReceita")
    suspend fun atualizarReceita(novaCategoria: String, novoValor: Double,idReceita: Int)

    @Query("DELETE FROM receita where id = :idReceita")
    suspend fun deletarReceita(idReceita: Int)

}