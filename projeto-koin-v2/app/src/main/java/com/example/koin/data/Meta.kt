package com.example.koin.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "meta")
data class Meta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nome: String,

    val valorAlvo: Long,

    val valorGuardado: Long,

    val icone: String,

    val cor: String,

    val dataCriacao: Date = Date(),

    val idFkUsuario: Int,

    val prazo: Date? = null
)