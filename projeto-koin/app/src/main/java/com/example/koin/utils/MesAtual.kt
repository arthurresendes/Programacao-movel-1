package com.example.koin.utils

import java.util.Calendar
import java.util.Locale

fun obterMesAtualPorExtenso(): String {
    val calendario = Calendar.getInstance()
    val localBrasil = Locale("pt", "BR")
    return calendario.getDisplayName(Calendar.MONTH, Calendar.LONG, localBrasil)
        ?.replaceFirstChar { it.uppercase() } ?: ""
}
