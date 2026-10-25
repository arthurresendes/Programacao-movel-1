package com.example.koin.data.response

import androidx.room.ColumnInfo
import java.util.Date

data class UltimaTransacao(
    val id: Int,
    val nomeMes: String,
    val nomeCategoria: String,
    val valor: Double,
    val idFkUsuario: Int,
    @ColumnInfo(name = "data_transacao") val dataTransacao: Date,
    val tipo: String
)