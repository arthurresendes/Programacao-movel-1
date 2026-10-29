package com.example.koin

import android.os.Bundle
import android.text.style.TtsSpan
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.koin.data.DadosEntrePaginas

class VerInformcoesContaActivity : AppCompatActivity() {
    private lateinit var tvEmail: TextView
    private lateinit var tvNome: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_informcoes_conta)
        val email = DadosEntrePaginas.email
        val nome = DadosEntrePaginas.nome

        tvNome = findViewById<TextView>(R.id.tvNome)
        tvEmail = findViewById<TextView>(R.id.tvEmail)

        tvNome.text = "Olá ${nome}"
        tvEmail.text = "Seu email: ${email}"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}