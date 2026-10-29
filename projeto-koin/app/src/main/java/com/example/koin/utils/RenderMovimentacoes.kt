package com.example.koin.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.koin.R
import com.example.koin.data.response.UltimaTransacao
import java.text.SimpleDateFormat
import java.util.Locale

private val formatoData = SimpleDateFormat("dd 'de' MMM", Locale("pt", "BR"))
private val formatoDia = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))

private fun criarLinha(
    inflater: LayoutInflater,
    container: LinearLayout,
    t: UltimaTransacao,
    mostrarDivisor: Boolean
): View {
    val item = inflater.inflate(R.layout.item_movimentacao, container, false)

    val icone = item.findViewById<ImageView>(R.id.ivCategoria)
    val recurso = iconeDaCategoria(t.nomeCategoria)
    if (recurso != null) icone.setImageResource(recurso) else icone.setImageDrawable(null)

    val descricao = t.descricao?.takeIf { it.isNotBlank() }
    val data = formatoData.format(t.dataTransacao)
    item.findViewById<TextView>(R.id.tvTitulo).text = descricao ?: t.nomeCategoria
    item.findViewById<TextView>(R.id.tvSubtitulo).text =
        if (descricao != null) "${t.nomeCategoria} · $data" else data

    val tvValor = item.findViewById<TextView>(R.id.tvValor)
    if (t.tipo == "despesa") {
        tvValor.text = "- R$ %.2f".format(t.valor)
        tvValor.setTextColor(Color.parseColor("#C65353"))
    } else {
        tvValor.text = "+ R$ %.2f".format(t.valor)
        tvValor.setTextColor(Color.parseColor("#16865B"))
    }

    if (!mostrarDivisor) item.findViewById<View>(R.id.divisor).visibility = View.GONE
    return item
}

private fun mensagemVazia(context: Context) = TextView(context).apply {
    text = "Nenhuma movimentação encontrada."
    setTextColor(Color.parseColor("#89958D"))
    textSize = 13f
    gravity = Gravity.CENTER
    setPadding(0, 40, 0, 40)
}

fun renderizarTransacoes(context: Context, container: LinearLayout, lista: List<UltimaTransacao>) {
    container.removeAllViews()
    if (lista.isEmpty()) {
        container.addView(mensagemVazia(context))
        return
    }
    val inflater = LayoutInflater.from(context)
    lista.forEachIndexed { i, t ->
        container.addView(criarLinha(inflater, container, t, i != lista.lastIndex))
    }
}

fun renderizarAgrupadoPorData(context: Context, container: LinearLayout, lista: List<UltimaTransacao>) {
    container.removeAllViews()
    if (lista.isEmpty()) {
        container.addView(mensagemVazia(context))
        return
    }
    val inflater = LayoutInflater.from(context)
    val grupos = lista.groupBy { formatoDia.format(it.dataTransacao) }

    grupos.forEach { (dia, itens) ->
        val titulo = TextView(context).apply {
            text = dia
            setTextColor(Color.parseColor("#89958D"))
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 18, 0, 4)
        }
        container.addView(titulo)
        itens.forEachIndexed { i, t ->
            container.addView(criarLinha(inflater, container, t, i != itens.lastIndex))
        }
    }
}

fun iconeDaCategoria(categoria: String): Int? =
    when (categoria.trim().lowercase()) {
        "alimentação", "alimentacao", "mercado", "supermercado" -> R.drawable.mercado_gasto
        "transporte", "mobilidade" -> R.drawable.transporte_gasto
        "salário", "salario", "trabalho" -> R.drawable.carteira
        else -> null
    }
