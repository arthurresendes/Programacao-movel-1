package com.example.loteria03

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MegaSenaActivity : AppCompatActivity() {
    private lateinit var selectbox: Spinner
    private lateinit var btmega: Button
    private lateinit var qtdJogos: EditText
    private lateinit var container: LinearLayout
    private lateinit var btVoltar: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mega_sena)
        selectbox = findViewById<Spinner>(R.id.selecionarMega)
        btmega = findViewById<Button>(R.id.btmegajogo)
        qtdJogos = findViewById<EditText>(R.id.qtdjogos)
        btVoltar = findViewById<ImageButton>(R.id.voltar)
        container = findViewById<LinearLayout>(R.id.linearcontainer)

        val opcoes = (6..20).toList()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opcoes
        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        selectbox.adapter = adapter

        btmega.setOnClickListener {
            val numSeletor = selectbox.selectedItem.toString().toInt()
            val qtd = qtdJogos.text.toString().toInt()

            container.removeAllViews()

            repeat(qtd) { i ->

                val textView = TextView(this)
                val numeros = (1..60).shuffled().take(numSeletor).sorted()
                textView.text = "Jogo ${i + 1}: ${numeros.joinToString(" - ")}"

                container.addView(textView)
            }
        }

        btVoltar.setOnClickListener {
            voltarSimples()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}