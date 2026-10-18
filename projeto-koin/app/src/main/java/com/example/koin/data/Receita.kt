package com.example.koin.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "receita")
data class Receita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nomeMes: String,
    val nomeCategoria: String,
    val valor: Double,
    val idFkUsuario: Int
)
