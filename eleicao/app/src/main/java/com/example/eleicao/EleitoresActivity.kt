package com.example.eleicao

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.eleicao.data.AppDatabase
import com.example.eleicao.data.EntrevistadoComLocalizacao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EleitoresActivity : AppCompatActivity() {
    private lateinit var containerPesquisas: LinearLayout
    private lateinit var voltar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eleitores)
        containerPesquisas = findViewById(R.id.container_pesquisas)
        voltar = findViewById<ImageButton>(R.id.voltar)
        voltarPage()
        carregarPesquisas()
    }

    override fun onResume() {
        super.onResume()
        carregarPesquisas()
    }

    private fun voltarPage(){
        voltar.setOnClickListener {
            voltando()
        }
    }

    private fun carregarPesquisas() {
        val banco = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val pesquisas = withContext(Dispatchers.IO) {
                banco.entrevistadoDao().buscarEntrevistadosComLocalizacao()
            }
            exibirPesquisas(pesquisas)
        }
    }

    private fun exibirPesquisas(pesquisas: List<EntrevistadoComLocalizacao>) {
        containerPesquisas.removeAllViews()
        if (pesquisas.isEmpty()) {
            val texto = TextView(this)
            texto.text = "Nenhuma pesquisa encontrada."
            texto.textSize = 18f
            texto.setPadding(0, 20, 0, 20)
            containerPesquisas.addView(texto)
            return
        }


        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (pesquisa in pesquisas) {
            val bloco = LinearLayout(this)
            bloco.orientation = LinearLayout.VERTICAL
            bloco.setPadding(24, 20, 24, 20)
            bloco.setBackgroundResource(R.drawable.borda_item)

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 0, 0, 12)
            bloco.layoutParams = layoutParams

            val nome = TextView(this)
            nome.text = "Nome: ${pesquisa.nome}"
            nome.textSize = 18f
            nome.setTextColor(Color.BLACK)

            val telefone = TextView(this)
            telefone.text = "Telefone: ${pesquisa.telefone}"
            telefone.textSize = 16f
            telefone.setTextColor(Color.DKGRAY)

            val endereco = TextView(this)
            endereco.text = "Endereço: ${pesquisa.endereco}"
            endereco.textSize = 16f
            endereco.setTextColor(Color.DKGRAY)

            val data = TextView(this)
            data.text = "Finalizado em: ${formato.format(Date(pesquisa.dataFinalizacao))}"
            data.textSize = 14f
            data.setTextColor(Color.GRAY)
            data.setPadding(0, 8, 0, 0)

            bloco.addView(nome)
            bloco.addView(telefone)
            bloco.addView(endereco)
            bloco.addView(data)

            containerPesquisas.addView(bloco)
        }
    }
}