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

class LotoManiaActivity : AppCompatActivity() {
    private lateinit var btLotoMania: Button
    private lateinit var qtdJogos: EditText
    private lateinit var btVoltar: ImageButton
    private lateinit var container: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loto_mania)
        btLotoMania = findViewById<Button>(R.id.btlotomaniajogo)
        qtdJogos = findViewById<EditText>(R.id.qtdjogos)
        container = findViewById<LinearLayout>(R.id.linearcontainer)
        btVoltar = findViewById<ImageButton>(R.id.voltar)

        btLotoMania.setOnClickListener {
            val numSeletor = 50
            val qtd = qtdJogos.text.toString().toInt()

            container.removeAllViews()

            repeat(qtd) { i ->

                val textView = TextView(this)
                val numeros = (0..99).shuffled().take(numSeletor).sorted()
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