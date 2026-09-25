package com.example.eleicao.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "localizacoes")
data class Endereco(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrevistadoId: Int,

    val latitude: Double,

    val longitude: Double,

    val endereco: String
)