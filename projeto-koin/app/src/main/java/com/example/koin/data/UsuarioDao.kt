package com.example.koin.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.koin.data.response.InfoLogado

@Dao
interface UsuarioDao {

    @Query("SELECT id,nome,email FROM usuario WHERE email = :emailPassada AND senha = :senhaPassada")
    suspend fun logar(emailPassada: String, senhaPassada: String) : InfoLogado?

    @Insert
    suspend fun inserir (usuario: Usuario)

    @Query("DELETE FROM usuario WHERE id = :usuarioId")
    suspend fun deletar(usuarioId: Int) : Int

    @Query("UPDATE usuario SET senha = :novaSenha WHERE id = :usuarioId")
    suspend fun atualizarSenha(novaSenha: String, usuarioId: Int) : Int
}
