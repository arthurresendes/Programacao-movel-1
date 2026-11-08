package com.example.koin.utils

import android.app.Activity
import android.content.Intent
import com.example.koin.LoginActivity
import com.example.koin.data.DadosEntrePaginas

fun Activity.direcionandoLimpandoSessao(pagAtual: Activity, pageProx: Class<*>) {
    DadosEntrePaginas.limpar()
    val intent = Intent(pagAtual, pageProx)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    startActivity(intent)
}

fun Activity.exigirSessao(): Boolean {
    if ((DadosEntrePaginas.id ?: 0L) != 0L) return true
    direcionandoLimpandoSessao(this, LoginActivity::class.java)
    finish()
    return false
}