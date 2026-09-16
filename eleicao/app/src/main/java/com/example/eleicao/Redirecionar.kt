package com.example.eleicao

import android.app.Activity

fun Activity.direcionando(pagAtual: Activity, pageProx: Class<*>){
    val intent = Intent(pagAtual, pageProx)
    startActivity(intent)
}