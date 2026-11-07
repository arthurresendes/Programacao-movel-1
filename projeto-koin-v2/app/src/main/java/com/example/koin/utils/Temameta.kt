package com.example.koin.utils

import android.graphics.Color
import androidx.annotation.DrawableRes
import com.example.koin.R

data class TemaMeta(
    @DrawableRes val fundoIcone: Int,
    val corProgresso: Int,
    val corTrilha: Int
)

val CORES_META = listOf("verde", "laranja", "azul")

fun temaDaMeta(cor: String): TemaMeta =
    when (cor) {
        "laranja" -> TemaMeta(
            R.drawable.bg_icone_meta_laranja,
            Color.parseColor("#D98245"),
            Color.parseColor("#F7E8DC")
        )
        "azul" -> TemaMeta(
            R.drawable.bg_icone_meta_azul,
            Color.parseColor("#5575B9"),
            Color.parseColor("#E8EDF9")
        )
        else -> TemaMeta(
            R.drawable.bg_icone_meta_verde,
            Color.parseColor("#16865B"),
            Color.parseColor("#E3F3E9")
        )
    }