package com.example.eleicao

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eleicao.direcionando

class LoginActivity : AppCompatActivity() {
    private lateinit var etUser: EditText
    private lateinit var etSenha: EditText
    private lateinit var btAcessar: Button
    private lateinit var btFinalizar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        etUser = findViewById<EditText>(R.id.usuario)
        etSenha = findViewById<EditText>(R.id.senha)
        btAcessar = findViewById<Button>(R.id.acessar)
        btFinalizar = findViewById<Button>(R.id.finalizar)

        btAcessar.setOnClickListener {
            val resUser = etUser.text.toString()
            val resSenha = etSenha.text.toString()

            if (resUser == "admin" && resSenha == "admin") {
                direcionando(this@LoginActivity, AdminMenuActivity::class.java)
            } else if (resUser == "entrevistado" && resSenha == "entrevistado") {
                direcionando(this@LoginActivity, EspontaneoActivity::class.java)
            } else {
                Toast.makeText(applicationContext, "Digite um usuário valido", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btFinalizar.setOnClickListener {
            finalizar()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}