package com.example.koin.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "despesas")
data class Despesas(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nomeMes: String,
    val nomeCategoria: String,
    val valorDespesa: Double,
    val descricao: String?,
    val dataDespesaCadastrada: Date,
    val idFkUsuario: Int
)