package com.example.koin.data
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val email: String,
    val senha: String
)