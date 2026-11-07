package com.example.koin.utils

object Categorias {

    val despesa = listOf(
        "Alimentação",
        "Transporte",
        "Moradia",
        "Contas",
        "Saúde",
        "Educação",
        "Lazer",
        "Compras",
        "Outros"
    )

    val receita = listOf(
        "Salário",
        "Freelance",
        "Investimentos",
        "Presente",
        "Outros"
    )

    fun doTipo(tipo: String): List<String> =
        if (tipo == "despesa") despesa else receita
}