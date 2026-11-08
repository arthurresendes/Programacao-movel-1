package com.example.koin.utils

import android.graphics.Color
fun corDaCategoria(categoria: String): Int =
    when (categoria.trim().lowercase()) {
        "alimentação", "alimentacao" -> Color.rgb(22, 134, 91)
        "transporte" -> Color.rgb(198, 83, 83)
        "moradia" -> Color.rgb(217, 139, 155)
        "saúde", "saude" -> Color.rgb(167, 205, 180)
        "contas" -> Color.rgb(90, 115, 145)
        "educação", "educacao" -> Color.rgb(190, 155, 90)
        "lazer" -> Color.rgb(130, 110, 160)
        "compras" -> Color.rgb(224, 144, 107)
        else -> Color.rgb(137, 149, 141)
    }