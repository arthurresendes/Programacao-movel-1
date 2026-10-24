package com.example.koin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.koin.data.AppDatabase
import com.example.koin.data.Usuario
import com.example.koin.security.GerenciadorSeguranca
import com.example.koin.utils.direcionando
import kotlinx.coroutines.launch

class CadastroActivity : AppCompatActivity() {
    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var etConfirmarSenha: EditText
    private lateinit var tvEntrar: TextView

    private lateinit var btCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.usuarioDao()

        etNome= findViewById<EditText>(R.id.etNome)
        etEmail = findViewById<EditText>(R.id.etEmail)
        etSenha = findViewById<EditText>(R.id.etSenha)
        etConfirmarSenha = findViewById<EditText>(R.id.etConfirmarSenha)
        btCadastrar = findViewById<Button>(R.id.btnEntrar)
        tvEntrar = findViewById<TextView>(R.id.tvLogar)

        tvEntrar.setOnClickListener {
            direcionando(this@CadastroActivity, LoginActivity::class.java)
        }

        btCadastrar.setOnClickListener(){
            val nomeDigitado  =  etNome.text.toString().trim()
            val emailDigitado = etEmail.text.toString().trim()
            val senhaDigitado = etSenha.text.toString()
            val senhaConfirmacao = etConfirmarSenha.toString()

            if (nomeDigitado.isEmpty() || emailDigitado.isEmpty() || senhaDigitado.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(senhaDigitado != senhaConfirmacao){
                Toast.makeText(this, "As senhas tem que ser iguais!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btCadastrar.isEnabled = false

            val criptografarSenha = GerenciadorSeguranca.criptografar(senhaDigitado)

            lifecycleScope.launch {
                val usuario = Usuario(
                    nome = nomeDigitado,
                    email = emailDigitado,
                    senha = criptografarSenha
                )

                try{
                    userDao.inserir(usuario)
                    Toast.makeText(applicationContext, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    direcionando(this@CadastroActivity, LoginActivity::class.java)
                }catch(e : Exception){
                    Toast.makeText(applicationContext, "Erro ao conectar ao banco de dados.", Toast.LENGTH_SHORT).show()
                    btCadastrar.isEnabled = true
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