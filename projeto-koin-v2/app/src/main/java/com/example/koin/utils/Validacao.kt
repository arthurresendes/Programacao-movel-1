package com.example.koin.utils
import android.util.Patterns

const val TAMANHO_MINIMO_SENHA = 8

fun emailValido(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun senhaValida(senha: String): Boolean = senha.length >= TAMANHO_MINIMO_SENHA