package com.example.koin.utils

import java.util.Calendar
import java.util.Date

fun periodoDoMesAtual(): Pair<Date, Date> {
    val inicio = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val fim = (inicio.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
    return Pair(inicio.time, fim.time)
}