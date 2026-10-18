package com.example.koin.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "despesas")
data class Despesas(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nomeMes: String,
    val nomeCategoria: String,
    val valorDespesa: Double,
    val descricao: String?,
    val dataDespesaCadastrada: LocalDate,
    val idFkUsuario: Int
)