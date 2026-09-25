package com.example.eleicao.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EnderecoDao {

    @Insert
    suspend fun inserir (Localização: Endereco)

    @Query("DELETE FROM localizacoes")
    suspend fun deletarTodos()
}