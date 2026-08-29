package com.example.aula01

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var etNota1: EditText
    private lateinit var etNota2: EditText
    private lateinit var etFaltas: EditText
    private lateinit var btCalcular: Button
    private lateinit var btLimpar: Button
    private lateinit var btSair: Button
    private lateinit var tvNotaFinal: TextView
    private lateinit var tvSituacao: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        etNota1 = findViewById<EditText>(R.id.etNota1)
        etNota2 = findViewById<EditText>(R.id.etNota2)
        etFaltas = findViewById<EditText>(R.id.etFaltas)

        btCalcular = findViewById<Button>(R.id.btCalcular)
        btLimpar = findViewById<Button>(R.id.btLimpar)
        btSair = findViewById<Button>(R.id.btSair)

        tvNotaFinal = findViewById<TextView>(R.id.tvNotaFinal)
        tvSituacao = findViewById<TextView>(R.id.tvSituacao)

        btCalcular.setOnClickListener {
            var nota1: Double
            var nota2: Double
            var faltas: Int
            var notaFinal: Double
            var situacao: String

            nota1 = etNota1.text.toString().toDouble()
            nota2 = etNota2.text.toString().toDouble()
            faltas = etFaltas.text.toString().toInt()

            notaFinal = (nota1 + nota2)/2
            if(faltas >= 25 || notaFinal < 6.0){
                situacao = "Reprovado"
            }else{
                situacao = "Aprovado"
            }

            tvNotaFinal.text = "$notaFinal"
            tvSituacao.text = situacao

        }

        btLimpar.setOnClickListener {
            etNota1.setText("")
            etNota2.setText("")
            etFaltas.setText("")
            tvSituacao.text = ""
            tvNotaFinal.text = ""
            etNota1.requestFocus()
        }

        btSair.setOnClickListener {
            finishAndRemoveTask()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}