
package com.example.eleicao.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "respostas")
data class Resposta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val problemas: String,

    val voto: String,

    val candidatoEspontaneo: String
)
