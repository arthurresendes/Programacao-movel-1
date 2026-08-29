package com.example.media_mobile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var atividade: EditText
    private lateinit var prova1: EditText
    private lateinit var prova2: EditText
    private lateinit var faltas: EditText

    private lateinit var situacao: TextView
    private lateinit var calcular: Button
    private lateinit var limpar: Button
    private lateinit var sair: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        atividade = findViewById<EditText>(R.id.atividade)
        prova1 = findViewById<EditText>(R.id.prova1)
        prova2 = findViewById<EditText>(R.id.prova2)
        faltas = findViewById<EditText>(R.id.faltas)

        situacao = findViewById<TextView>(R.id.situacao)

        calcular = findViewById<Button>(R.id.calcular)
        limpar = findViewById<Button>(R.id.limpar)
        sair = findViewById<Button>(R.id.sair)

        calcular.setOnClickListener {
            val valAtividade = atividade.text.toString().toDouble()
            val valProva1 = prova1.text.toString().toDouble()
            val valProva2 = prova2.text.toString().toDouble()
            val valFaltas = faltas.text.toString().toInt()
            val media = (valAtividade *0.20) + (valProva1 * 0.40) + (valProva2 *0.40)/1

            if(valFaltas < 20 &&  media >= 6){
                situacao.text = "Aprovado"
            }else {
                situacao.text = "Reprovado"
            }
        }

        limpar.setOnClickListener {
            atividade.text.clear()
            prova1.text.clear()
            prova2.text.clear()
            faltas.setText("")
            situacao.text = ""
        }

        sair.setOnClickListener {
            finish()
        }

    }
}