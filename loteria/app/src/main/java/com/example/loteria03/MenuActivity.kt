package com.example.loteria03

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    private lateinit var megasena: Button
    private lateinit var quina: Button
    private lateinit var lotomania: Button
    private lateinit var lotofacil: Button
    private lateinit var btVoltar: ImageButton

    fun direcionar(page: Class<*>){
        val intent = Intent(this@MenuActivity, page)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        megasena = findViewById<Button>(R.id.btmegasena)
        quina = findViewById<Button>(R.id.btquina)
        lotofacil = findViewById<Button>(R.id.btlotofacil)
        lotomania = findViewById<Button>(R.id.btlotomania)
        btVoltar = findViewById<ImageButton>(R.id.voltar)

        megasena.setOnClickListener {
            direcionar(MegaSenaActivity::class.java)
        }

        quina.setOnClickListener {
            direcionar(QuinaActivity::class.java)
        }

        lotofacil.setOnClickListener {
            direcionar(LotoFacilActivity::class.java)
        }

        lotomania.setOnClickListener {
            direcionar(LotoManiaActivity::class.java)
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