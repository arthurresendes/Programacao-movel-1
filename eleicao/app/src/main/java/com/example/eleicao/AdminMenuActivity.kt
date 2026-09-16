package com.example.eleicao

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminMenuActivity : AppCompatActivity() {
    private lateinit var btEleitores: Button
    private lateinit var btResultado: Button
    private lateinit var btLimpar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_menu)
        btEleitores = findViewById<Button>(R.id.btEleitoresMenu)
        btResultado = findViewById<Button>(R.id.btResultadosMenu)
        btLimpar = findViewById<Button>(R.id.btLimparDados)

        btEleitores.setOnClickListener {
            direcionando(this@AdminMenuActivity, EleitoresActivity::class.java)
        }
        btResultado.setOnClickListener {
            direcionando(this@AdminMenuActivity, ResultadoActivity::class.java)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}