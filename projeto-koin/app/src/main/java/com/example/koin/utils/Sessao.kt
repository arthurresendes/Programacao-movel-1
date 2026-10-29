package com.example.koin.utils

import android.app.Activity
import android.content.Intent
import com.example.koin.data.DadosEntrePaginas

fun Activity.direcionandoLimpandoSessao(pagAtual: Activity, pageProx: Class<*>) {
    DadosEntrePaginas.limpar()
    val intent = Intent(pagAtual, pageProx)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    startActivity(intent)
}