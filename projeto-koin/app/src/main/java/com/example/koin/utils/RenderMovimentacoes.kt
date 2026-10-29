package com.example.koin.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.koin.R
import com.example.koin.data.UltimaTransacao
import java.text.SimpleDateFormat
import java.util.Locale

private val formatoData = SimpleDateFormat("dd 'de' MMM", Locale("pt", "BR"))

fun renderizarTransacoes(context: Context, container: LinearLayout, lista: List<UltimaTransacao>) {
    container.removeAllViews()

    if (lista.isEmpty()) {
        val vazio = TextView(context).apply {
            text = "Nenhuma movimentação encontrada."
            setTextColor(Color.parseColor("#89958D"))
            textSize = 13f
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 40)
        }
        container.addView(vazio)
        return
    }

    val inflater = LayoutInflater.from(context)
    lista.forEachIndexed { i, t ->
        val item = inflater.inflate(R.layout.item_movimentacao, container, false)
        val icone = item.findViewById<ImageView>(R.id.ivCategoria)
        val recurso = iconeDaCategoria(t.nomeCategoria)
        if (recurso != null) {
            icone.setImageResource(recurso)
        } else {
            icone.setImageDrawable(null)
        }
        item.findViewById<TextView>(R.id.tvTitulo).text = t.nomeCategoria
        item.findViewById<TextView>(R.id.tvSubtitulo).text = formatoData.format(t.dataTransacao)

        val tvValor = item.findViewById<TextView>(R.id.tvValor)
        if (t.tipo == "despesa") {
            tvValor.text = "- R$ %.2f".format(t.valor)
            tvValor.setTextColor(Color.parseColor("#C65353"))
        } else {
            tvValor.text = "+ R$ %.2f".format(t.valor)
            tvValor.setTextColor(Color.parseColor("#16865B"))
        }

        if (i == lista.lastIndex) item.findViewById<View>(R.id.divisor).visibility = View.GONE
        container.addView(item)
    }
}

fun iconeDaCategoria(categoria: String): Int? =
    when (categoria.trim().lowercase()) {
        "alimentação", "alimentacao", "mercado", "supermercado" -> R.drawable.mercado_gasto
        "transporte", "mobilidade" -> R.drawable.transporte_gasto
        "salário", "salario", "trabalho" -> R.drawable.carteira
        else -> null
    }
