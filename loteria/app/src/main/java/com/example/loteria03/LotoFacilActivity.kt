package com.example.loteria03

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LotoFacilActivity : AppCompatActivity() {
    private lateinit var selectbox: Spinner
    private lateinit var btLotoFacil: Button
    private lateinit var qtdJogos: EditText
    private lateinit var container: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loto_facil)
        selectbox = findViewById<Spinner>(R.id.selecionarLotoFacil)
        btLotoFacil = findViewById<Button>(R.id.btlotofacil)
        qtdJogos = findViewById<EditText>(R.id.qtdjogos)
        container = findViewById<LinearLayout>(R.id.linearcontainer)

        val opcoes = (15..20).toList()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opcoes
        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        selectbox.adapter = adapter

        btLotoFacil.setOnClickListener {
            val numSeletor = selectbox.selectedItem.toString().toInt()
            val qtd = qtdJogos.text.toString().toInt()

            container.removeAllViews()

            repeat(qtd) { i ->

                val textView = TextView(this)
                val numeros = (1..25).shuffled().take(numSeletor).sorted()
                textView.text = "Jogo ${i + 1}: ${numeros.joinToString(" - ")}"

                container.addView(textView)
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}