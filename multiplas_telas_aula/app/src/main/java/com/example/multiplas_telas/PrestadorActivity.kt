package com.example.multiplas_telas

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.format.TextStyle

class PrestadorActivity : AppCompatActivity() {
    private lateinit var emailprestador: TextView
    private lateinit var btSair: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_prestador)
        emailprestador = findViewById<TextView>(R.id.emailPrestador)
        btSair = findViewById<Button>(R.id.sairUserDentroPrest)

        var intent = getIntent()
        var email = intent.getStringExtra("email")

        emailprestador.text = email

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