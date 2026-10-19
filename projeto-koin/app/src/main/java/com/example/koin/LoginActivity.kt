package com.example.koin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.koin.data.AppDatabase
import com.example.koin.security.GerenciadorSeguranca
import com.example.koin.utils.direcionando
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var btAcessar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.usuarioDao()

        btAcessar.setOnClickListener(){
            val emailDigitado = etEmail.text.toString().trim()
            val senhaDigitado = etSenha.text.toString()

            if (emailDigitado.isEmpty() || senhaDigitado.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btAcessar.isEnabled = false

            val senhaCriptografada = GerenciadorSeguranca.criptografar(senhaDigitado)

            lifecycleScope.launch {
                val validacao = userDao.logar(emailDigitado, senhaCriptografada)

                if (validacao == null){
                    Toast.makeText(applicationContext, "E-mail ou senha inválidos! Tente novamente", Toast.LENGTH_SHORT).show()
                    btAcessar.isEnabled = true
                }else{
                    direcionando(this@LoginActivity, MenuActivity::class.java)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}