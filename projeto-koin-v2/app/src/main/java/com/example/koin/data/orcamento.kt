package com.example.koin.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orcamento",
    indices = [
        Index(
            value = ["idFkUsuario", "nomeCategoria"],
            unique = true
        )
    ]
)
data class Orcamento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nomeCategoria: String,

    val valorLimite: Long,

    val idFkUsuario: Int
)