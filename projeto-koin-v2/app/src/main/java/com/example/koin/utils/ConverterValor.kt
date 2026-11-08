package com.example.koin.utils

object ConverterValor {

fun paraCentavos(valor: String): Long {
    val digitos = valor.filter { it.isDigit() }

    return digitos.toLongOrNull() ?: 0L
}

}
