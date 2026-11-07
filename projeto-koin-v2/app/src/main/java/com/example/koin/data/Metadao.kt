package com.example.koin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface MetaDao {

    @Query("""
        SELECT * FROM meta
        WHERE idFkUsuario = :idUsuario
        ORDER BY id ASC
    """)
    suspend fun listar(idUsuario: Int): List<Meta>

    @Query("SELECT COUNT(*) FROM meta WHERE idFkUsuario = :idUsuario")
    suspend fun contar(idUsuario: Int): Int

    @Insert
    suspend fun inserir(meta: Meta)

    @Query("""UPDATE meta
        SET valorGuardado = MAX(0, valorGuardado + :delta)
        WHERE id = :id AND idFkUsuario = :idUsuario""")
    suspend fun ajustarValorGuardado(id: Int, idUsuario: Int, delta: Long): Int

    @Query("""UPDATE meta
        SET nome = :nome, valorAlvo = :valorAlvo, icone = :icone, prazo = :prazo
        WHERE id = :id AND idFkUsuario = :idUsuario""")
    suspend fun atualizar(id: Int, idUsuario: Int, nome: String, valorAlvo: Long, icone: String, prazo: Date?): Int

    @Query("DELETE FROM meta WHERE id = :id AND idFkUsuario = :idUsuario")
    suspend fun excluir(id: Int, idUsuario: Int): Int

    @Query("DELETE FROM meta WHERE idFkUsuario = :idUsuario")
    suspend fun excluirTodasDoUsuario(idUsuario: Int)
}