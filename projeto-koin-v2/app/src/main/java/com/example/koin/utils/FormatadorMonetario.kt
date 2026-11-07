package com.example.koin.utils

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

object FormatadorMonetario {

private val formatoBrasileiro: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

fun formatar(centavos: Long): String {
    val valor = BigDecimal.valueOf(centavos, 2)

    return formatoBrasileiro.format(valor)
}
}
