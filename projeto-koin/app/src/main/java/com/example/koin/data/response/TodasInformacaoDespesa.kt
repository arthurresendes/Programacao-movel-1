package com.example.koin.data.response

import java.util.Date

data class TodasInformacaoDespesa(
    val id: Int,
    val nomeMes: String,
    val nomeCategoria: String,
    val valorDespesa: Double,
    val descricao: String?,
    val dataDespesaCadastrada: Date,
    val idFkUsuario: Int
)