package com.example.multiplas_telas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    private lateinit var logCliente: Button
    private lateinit var logPrestador: Button
    fun baseLogar(page: Class<*>){
        var intent = Intent(this@MenuActivity, page)
        startActivity(intent)
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        logCliente = findViewById<Button>(R.id.logCliente)
        logPrestador = findViewById<Button>(R.id.logPrestador)

        logCliente.setOnClickListener {
            baseLogar(LoginClienteActivity::class.java)
        }
        logPrestador.setOnClickListener {
            baseLogar(LoginPrestadorActivity::class.java)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}