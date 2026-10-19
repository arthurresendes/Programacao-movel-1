package com.example.koin.data.response

import java.util.Date

data class TodasInformacaoReceita(
    val id: Int,
    val nomeMes: String,
    val nomeCategoria: String,
    val valor: Double,
    val dataReceitaCadastrada: Date,
    val idFkUsuario: Int
)