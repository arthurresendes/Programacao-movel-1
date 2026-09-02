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
            val intent = Intent(this@MenuActivity, MegaSenaActivity::class.java)
            startActivity(intent)
        }

        quina.setOnClickListener {
            val intent = Intent(this@MenuActivity, QuinaActivity::class.java)
            startActivity(intent)
        }

        lotofacil.setOnClickListener {
            val intent = Intent(this@MenuActivity, LotoFacilActivity::class.java)
            startActivity(intent)
        }

        lotomania.setOnClickListener {
            val intent = Intent(this@MenuActivity, LotoManiaActivity::class.java)
            startActivity(intent)
        }

        btVoltar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}