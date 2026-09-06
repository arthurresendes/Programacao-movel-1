package com.example.loteria03
import android.app.Activity
import android.content.Intent

fun Activity.direcionar(pageAtual: Activity, pageProx: Class<*>){
    val intent = Intent(pageAtual, pageProx)
    startActivity(intent)
}
