package com.example.koin

import android.app.AlertDialog
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.koin.data.AppDatabase
import com.example.koin.data.DadosEntrePaginas
import com.example.koin.utils.direcionando
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilActivity : AppCompatActivity() {
    private lateinit var editarSenha: TextView
    private lateinit var verInformacoes: LinearLayout
    private lateinit var sairConta: TextView
    private lateinit var deletarConta: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)

        editarSenha = findViewById<TextView>(R.id.btEditarSenha)
        verInformacoes = findViewById<LinearLayout>(R.id.llDadosPessoais)
        sairConta = findViewById<TextView>(R.id.btSairConta)
        deletarConta = findViewById<TextView>(R.id.btDeletarConta)

        val id = (DadosEntrePaginas.id ?: 0L).toInt()

        editarSenha.setOnClickListener {
            direcionando(this@PerfilActivity, EditarSenhaActivity::class.java)
        }

        verInformacoes.setOnClickListener {
            direcionando(this@PerfilActivity, VerInformcoesContaActivity::class.java)
        }

        sairConta.setOnClickListener {
            direcionando(this@PerfilActivity, LoginActivity::class.java)
        }

        deletarConta.setOnClickListener {
            mostrarConfirmacao(id)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun mostrarConfirmacao(id: Int) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Deseja deletar sua conta?")
        builder.setMessage("Você tem certeza que deseja executar esta ação?")

        builder.setPositiveButton("Sim") { dialog, which ->
            lifecycleScope.launch {
                deletandoConta(id)
                Toast.makeText(this@PerfilActivity, "Deleção executada com sucesso!", Toast.LENGTH_SHORT).show()
                direcionando(this@PerfilActivity, CadastroActivity::class.java)
            }
        }
        builder.setNegativeButton("Não") { dialog, which ->
            Toast.makeText(this, "Ação cancelada.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private suspend fun deletandoConta(id: Int) {
        withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@PerfilActivity)
            val usuario = db.usuarioDao()
            usuario.deletar(id)
        }
    }
}
