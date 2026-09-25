package com.example.eleicao.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entrevistados")
data class `Entrevistado`(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val telefone: String,
    val dataFinalizacao: Long = System.currentTimeMillis()
)