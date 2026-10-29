package com.example.koin

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.koin.data.AppDatabase
import com.example.koin.data.DadosEntrePaginas
import com.example.koin.security.GerenciadorSeguranca
import com.example.koin.utils.direcionando
import com.example.koin.utils.direcionandoLimpandoSessao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarSenhaActivity : AppCompatActivity() {
    private lateinit var alterarSenha: TextView
    private lateinit var etSenha: EditText
    private lateinit var etConfirmarSenha: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_senha)

        alterarSenha = findViewById(R.id.btAlterarSenha)
        etSenha = findViewById(R.id.etSenha)
        etConfirmarSenha = findViewById(R.id.etConfirmarSenha)

        val id = (DadosEntrePaginas.id ?: 0L).toInt()
        val db = AppDatabase.getDatabase(this)
        val usuario = db.usuarioDao()

        alterarSenha.setOnClickListener {
            val senha  =  etSenha.text.toString().trim()
            val confirmarSenha = etConfirmarSenha.text.toString().trim()
            if (senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "Senhas diferentes!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            alterarSenha.isEnabled = false
            val senhaCriptografada = GerenciadorSeguranca.criptografar(senha)

            lifecycleScope.launch {
                usuario.atualizarSenha(senhaCriptografada, id)
                Toast.makeText(this@EditarSenhaActivity, "Senha atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                delay(1500)
                direcionandoLimpandoSessao(this@EditarSenhaActivity, LoginActivity::class.java)
            }

            }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}