package com.example.multiplas_telas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginClienteActivity : AppCompatActivity() {
    private lateinit var btnLogar: Button
    private lateinit var btnLimpar: Button
    private lateinit var btnSair: Button
    private lateinit var email: EditText
    private lateinit var senha: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_cliente)
        btnLogar = findViewById<Button>(R.id.logarUserCliente)
        btnLimpar = findViewById<Button>(R.id.limpUserCliente)
        btnSair = findViewById<Button>(R.id.sairUserCliente)
        email = findViewById<EditText>(R.id.emailCliente)
        senha = findViewById<EditText>(R.id.senhaCliente)

        btnLogar.setOnClickListener {
            val valEmail = email.text.toString()
            val valSenha = senha.text.toString()

            if(valEmail == "arthur@gmail.com" && valSenha == "12345"){
                Toast.makeText(this, "Logando...", Toast.LENGTH_SHORT).show()
                var intent = Intent(this@LoginClienteActivity, ClienteActivity::class.java)
                intent.putExtra("email", valEmail)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Email ou senha inválidos, tente novamente", Toast.LENGTH_LONG).show()
            }
        }

        btnLimpar.setOnClickListener {
            email.setText("")
            senha.setText("")
        }

        btnSair.setOnClickListener {
            finishAffinity()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}