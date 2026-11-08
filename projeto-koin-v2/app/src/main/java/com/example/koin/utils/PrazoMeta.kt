package com.example.koin.utils

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.koin.data.Meta
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class InfoPrazo(val texto: String, val vencido: Boolean)

fun infoDoPrazo(meta: Meta): InfoPrazo? {

    val prazo = meta.prazo ?: return null
    val dataTexto = formatoDoPrazo().format(prazo)
    val faltante = meta.valorAlvo - meta.valorGuardado

    if (faltante <= 0L) return InfoPrazo("Prazo: $dataTexto", false)

    val hoje = Calendar.getInstance()
    val limite = Calendar.getInstance().apply { time = prazo }

    if (chaveDoDia(hoje) > chaveDoDia(limite)) {
        return InfoPrazo("Prazo vencido em $dataTexto", true)
    }

    var meses = (limite.get(Calendar.YEAR) - hoje.get(Calendar.YEAR)) * 12 +
            (limite.get(Calendar.MONTH) - hoje.get(Calendar.MONTH))
    if (limite.get(Calendar.DAY_OF_MONTH) < hoje.get(Calendar.DAY_OF_MONTH)) meses -= 1
    meses = meses.coerceAtLeast(1)

    val porMesCentavos = (faltante + meses - 1) / meses
    val valorTexto = FormatadorMonetario.formatar(porMesCentavos)

    val texto = if (meses == 1) "Guarde $valorTexto até $dataTexto"
    else "Guarde cerca de $valorTexto por mês até $dataTexto"

    return InfoPrazo(texto, false)
}

private fun chaveDoDia(c: Calendar): Int = c.get(Calendar.YEAR) * 1000 + c.get(Calendar.DAY_OF_YEAR)

private fun formatoDoPrazo() = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

class CampoPrazo(
    private val fragment: Fragment,
    campo: View,
    private val tvPrazo: TextView,
    private val btLimpar: TextView
) {

    var prazo: Long? = null
        private set

    init {
        campo.setOnClickListener { abrirSeletor() }
        btLimpar.setOnClickListener { definir(null) }


        @Suppress("UNCHECKED_CAST")
        (fragment.childFragmentManager.findFragmentByTag(TAG_SELETOR) as? MaterialDatePicker<Long>)
            ?.let { ligar(it) }

        atualizar()
    }

    fun definir(millis: Long?) {
        prazo = millis
        atualizar()
    }

    fun guardarEstado(saida: Bundle) {
        saida.putLong(CHAVE_ESTADO, prazo ?: SEM_PRAZO)
    }

    fun restaurarEstado(entrada: Bundle?) {
        val valor = entrada?.getLong(CHAVE_ESTADO, SEM_PRAZO) ?: return
        definir(if (valor == SEM_PRAZO) null else valor)
    }

    private fun abrirSeletor() {

        if (fragment.childFragmentManager.findFragmentByTag(TAG_SELETOR) != null) return

        val hojeUtc = paraUtcMeiaNoite(System.currentTimeMillis())
        val atualUtc = prazo?.let { paraUtcMeiaNoite(it) }


        val restricao = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.from(hojeUtc))
            .build()

        val seletor = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Prazo da meta")
            .setSelection(if (atualUtc != null && atualUtc >= hojeUtc) atualUtc else hojeUtc)
            .setCalendarConstraints(restricao)
            .build()

        ligar(seletor)
        seletor.show(fragment.childFragmentManager, TAG_SELETOR)
    }

    private fun ligar(seletor: MaterialDatePicker<Long>) {
        seletor.addOnPositiveButtonClickListener { utcMillis ->
            definir(deUtcParaMeioDiaLocal(utcMillis))
        }
    }

    private fun atualizar() {
        val atual = prazo
        if (atual == null) {
            tvPrazo.text = "Sem prazo"
            tvPrazo.setTextColor(Color.parseColor("#89958D"))
            btLimpar.visibility = View.GONE
        } else {
            tvPrazo.text = formatoDoPrazo().format(Date(atual))
            tvPrazo.setTextColor(Color.parseColor("#26332C"))
            btLimpar.visibility = View.VISIBLE
        }
    }


    private fun paraUtcMeiaNoite(millis: Long): Long {
        val local = Calendar.getInstance().apply { timeInMillis = millis }
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                local.get(Calendar.YEAR),
                local.get(Calendar.MONTH),
                local.get(Calendar.DAY_OF_MONTH)
            )
        }.timeInMillis
    }

    private fun deUtcParaMeioDiaLocal(utcMillis: Long): Long {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
        return Calendar.getInstance().apply {
            clear()
            set(
                utc.get(Calendar.YEAR),
                utc.get(Calendar.MONTH),
                utc.get(Calendar.DAY_OF_MONTH),
                12, 0, 0
            )
        }.timeInMillis
    }

    companion object {
        const val SEM_PRAZO = Long.MIN_VALUE
        private const val TAG_SELETOR = "seletor_prazo_meta"
        private const val CHAVE_ESTADO = "campo_prazo_meta"
    }
}