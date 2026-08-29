package com.example.multiplas_telas

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ClienteActivity : AppCompatActivity() {
    private lateinit var emailCli: TextView
    private lateinit var btSair: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cliente)
        emailCli = findViewById<TextView>(R.id.emailCliente)
        btSair = findViewById<Button>(R.id.sairUserDentroCliente)
        var intent = getIntent()
        var email = intent.getStringExtra("email")

        emailCli.text = email

        btSair.setOnClickListener {
            finishAffinity()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}