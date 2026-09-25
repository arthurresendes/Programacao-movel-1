package com.example.eleicao

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.eleicao.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminMenuActivity : AppCompatActivity() {
    private lateinit var btEleitores: Button
    private lateinit var btResultado: Button
    private lateinit var btLimpar: Button
    private lateinit var tvTotal: TextView
    private lateinit var banco: AppDatabase
    private lateinit var voltar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_menu)

        banco = AppDatabase.getDatabase(this)

        btEleitores = findViewById(R.id.btEleitoresMenu)
        btResultado = findViewById(R.id.btResultadosMenu)
        btLimpar = findViewById(R.id.btLimparDados)
        tvTotal = findViewById(R.id.totalCount)
        voltar = findViewById<ImageButton>(R.id.voltar)

        atualizarTotal()

        btEleitores.setOnClickListener {
            direcionando(this@AdminMenuActivity, EleitoresActivity::class.java)
        }
        btResultado.setOnClickListener {
            direcionando(this@AdminMenuActivity, ResultadoActivity::class.java)
        }

        voltar.setOnClickListener {
            voltando()
        }

        btLimpar.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    banco.entrevistadoDao().deletarTodos()
                    banco.respostaDao().deletarTodos()
                }
                Toast.makeText(this@AdminMenuActivity, "Dados apagados!", Toast.LENGTH_SHORT).show()
                atualizarTotal()
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarTotal()
    }

    private fun atualizarTotal() {
        lifecycleScope.launch {
            val total = withContext(Dispatchers.IO) {
                banco.entrevistadoDao().quantidadeEntrevistados()
            }
            tvTotal.text = "Total de entrevistados: $total"
        }
    }
}