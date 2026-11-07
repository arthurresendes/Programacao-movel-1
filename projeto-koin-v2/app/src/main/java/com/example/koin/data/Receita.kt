package com.example.koin.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "receita")
data class Receita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nomeCategoria: String,
    val valor: Long,
    val descricao: String? = null,
    val dataReceitaCadastrada: Date = Date(),
    val idFkUsuario: Int
)