package com.example.koin.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuario WHERE email = :emailPassado LIMIT 1")
    suspend fun buscarPorEmail(emailPassado: String): Usuario?

    @Query("SELECT senha FROM usuario WHERE id = :usuarioId")
    suspend fun buscarSenhaPorId(usuarioId: Int): String?

    @Insert
    suspend fun inserir (usuario: Usuario)

    @Query("DELETE FROM usuario WHERE id = :usuarioId")
    suspend fun deletar(usuarioId: Int) : Int

    @Query("UPDATE usuario SET senha = :novaSenha WHERE id = :usuarioId")
    suspend fun atualizarSenha(novaSenha: String, usuarioId: Int) : Int


}
