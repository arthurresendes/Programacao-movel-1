package com.example.loteria03

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    private lateinit var megasena: Button
    private lateinit var quina: Button
    private lateinit var lotomania: Button
    private lateinit var lotofacil: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        megasena = findViewById<Button>(R.id.btmegasena)
        quina = findViewById<Button>(R.id.btquina)
        lotofacil = findViewById<Button>(R.id.btlotofacil)
        lotomania = findViewById<Button>(R.id.btlotomania)

        megasena.setOnClickListener {
            val intent = Intent(this@MenuActivity, MegaSenaActivity::class.java)
            startActivity(intent)
            finish()
        }

        quina.setOnClickListener {
            val intent = Intent(this@MenuActivity, QuinaActivity::class.java)
            startActivity(intent)
            finish()
        }

        lotofacil.setOnClickListener {
            val intent = Intent(this@MenuActivity, LotoFacilActivity::class.java)
            startActivity(intent)
            finish()
        }

        lotomania.setOnClickListener {
            val intent = Intent(this@MenuActivity, LotoManiaActivity::class.java)
            startActivity(intent)
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}