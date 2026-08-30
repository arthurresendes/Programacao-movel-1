package com.example.loteria03

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class login_activity : AppCompatActivity() {
    private lateinit var user: EditText
    private lateinit var senha: EditText
    private lateinit var entrar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        user = findViewById<EditText>(R.id.userText)
        senha = findViewById<EditText>(R.id.userSenha)
        entrar = findViewById<Button>(R.id.btEntrar)

        entrar.setOnClickListener {
            val valuser = user.text.toString()
            val valsenha = senha.text.toString()

            if(valuser == "Arthur" && valsenha == "1234"){
                val intent = Intent(this@login_activity, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(applicationContext, "Nome ou senha errados", Toast.LENGTH_SHORT).show()
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}