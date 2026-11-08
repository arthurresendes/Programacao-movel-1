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
import com.example.koin.utils.TAMANHO_MINIMO_SENHA
import com.example.koin.utils.direcionandoLimpandoSessao
import com.example.koin.utils.exigirSessao
import com.example.koin.utils.senhaValida
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarSenhaActivity : AppCompatActivity() {
    private lateinit var alterarSenha: TextView
    private lateinit var etSenha: EditText
    private lateinit var etConfirmarSenha: EditText
    private lateinit var etSenhaAtual: EditText

    private lateinit var btFechar: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_senha)
        if (!exigirSessao()) return

        alterarSenha = findViewById<TextView>(R.id.btAlterarSenha)
        etSenhaAtual = findViewById<EditText>(R.id.etSenhaAtual)
        etSenha = findViewById<EditText>(R.id.etSenha)
        etConfirmarSenha = findViewById<EditText>(R.id.etConfirmarSenha)
        btFechar = findViewById<TextView>(R.id.btFechar)
        btFechar.setOnClickListener { finish() }

        val id = (DadosEntrePaginas.id ?: 0L).toInt()
        val db = AppDatabase.getDatabase(this)
        val usuario = db.usuarioDao()

        alterarSenha.setOnClickListener {
            val senhaAtual = etSenhaAtual.text.toString()
            val senha = etSenha.text.toString()
            val confirmarSenha = etConfirmarSenha.text.toString()

            if (senhaAtual.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!senhaValida(senha)) {
                Toast.makeText(this, "A nova senha precisa ter pelo menos $TAMANHO_MINIMO_SENHA caracteres!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "Senhas diferentes!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            alterarSenha.isEnabled = false

            lifecycleScope.launch {
                val hashAtual = usuario.buscarSenhaPorId(id)
                val senhaAtualConfere = hashAtual != null && withContext(Dispatchers.Default) {
                    GerenciadorSeguranca.verificar(senhaAtual, hashAtual)
                }

                if (!senhaAtualConfere) {
                    Toast.makeText(this@EditarSenhaActivity, "Senha atual incorreta!", Toast.LENGTH_SHORT).show()
                    alterarSenha.isEnabled = true
                    return@launch
                }

                val novoHash = withContext(Dispatchers.Default) {
                    GerenciadorSeguranca.gerarHash(senha)
                }
                usuario.atualizarSenha(novoHash, id)
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