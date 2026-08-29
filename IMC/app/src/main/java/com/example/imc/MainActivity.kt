package com.example.imc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var meuBotao: Button
    private lateinit var limpar: Button
    private lateinit var sair: Button
    private lateinit var peso: EditText
    private lateinit var altura: EditText
    private lateinit var res: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        meuBotao = findViewById<Button>(R.id.calcular)
        limpar = findViewById<Button>(R.id.limpar)
        sair = findViewById<Button>(R.id.sair)
        peso = findViewById<EditText>(R.id.peso)
        altura = findViewById<EditText>(R.id.altura)
        res = findViewById<TextView>(R.id.resposta)

        meuBotao.setOnClickListener {
            val pesoRes = peso.text.toString()
            val alturaRes = altura.text.toString()

            val alturaConv = alturaRes.toDouble()
            val pesoConv = pesoRes.toDouble()

            val calculo = (pesoConv / (alturaConv * alturaConv))
            if(calculo <= 18.5){
                res.text = "Abaixo do peso"
            }else if(calculo <= 24.9){
                res.text = "Peso normal"
            }else if( calculo <= 29.9){
                res.text = "Sobrepeso"
            }else if(calculo <= 34.9){
                res.text = "Obesidade Grau 1"
            }else if(calculo <= 39.9){
                res.text = "Obesidade grau 2"
            }else{
                res.text = "Obesidade grau 3"
            }
        }

        limpar.setOnClickListener {
            peso.setText("")
            altura.setText("")
        }

        sair.setOnClickListener {
            finish()
        }

    }
}