package com.example.koin.data.response

import androidx.room.ColumnInfo
import java.util.Date

data class UltimaTransacao(
    val id: Int,
    val nomeCategoria: String,
    val valor: Long,
    val descricao: String?,
    val idFkUsuario: Int,
    @ColumnInfo(name = "data_transacao") val dataTransacao: Date,
    val tipo: String
)
