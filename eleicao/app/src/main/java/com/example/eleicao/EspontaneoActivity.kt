package com.example.eleicao

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EspontaneoActivity : AppCompatActivity() {
    private lateinit var etCandidato: EditText
    private lateinit var btConfirmar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_espontaneo)
        etCandidato = findViewById<EditText>(R.id.etEspontaneo)
        btConfirmar = findViewById<Button>(R.id.btConfirmar)

        btConfirmar.setOnClickListener {
            val candidato = etCandidato.toString().trim()
            if(candidato.isNotEmpty()){
                val intent = Intent(this@EspontaneoActivity, EstimuladaActivity::class.java).apply {
                    putExtra("Espontaneo", candidato)
                }
                startActivity(intent)
            }else{
                Toast.makeText(this, "Digite seu candidato corretamente!", Toast.LENGTH_SHORT).show()
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}