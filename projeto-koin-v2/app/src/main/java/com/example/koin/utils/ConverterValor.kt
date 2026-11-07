package com.example.koin.utils

object ConverterValor {

fun paraCentavos(valor: String): Long {
    val digitos = valor.filter { it.isDigit() }

    return digitos.toLongOrNull() ?: 0L
}

fun paraDecimal(centavos: Long): java.math.BigDecimal {
    return java.math.BigDecimal.valueOf(centavos, 2)
}
}
