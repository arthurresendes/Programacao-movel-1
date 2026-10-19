package com.example.koin.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
@Entity(tableName = "usuario",
    indices = [
        Index(value = ["email"], unique = true)
])
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    @ColumnInfo(name = "email")
    val email: String,
    val senha: String
)