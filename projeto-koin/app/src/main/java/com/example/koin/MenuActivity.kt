package com.example.koin

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.koin.data.AppDatabase
import com.example.koin.data.DadosEntrePaginas
import com.example.koin.utils.obterMesAtualPorExtenso
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {
    private lateinit var tvNomeUsuario: TextView
    private lateinit var tvSaldoTotal: TextView
    private lateinit var tvReceitaMes: TextView
    private lateinit var tvDespesaMes: TextView
    private lateinit var cardSaldo: LinearLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        tvNomeUsuario = findViewById<TextView>(R.id.tvNomeUsuario)
        tvNomeUsuario.text = "Olá, ${DadosEntrePaginas.nome}"

        val db = AppDatabase.getDatabase(this)
        val receita = db.receitaDao()
        val despesa = db.despesasDao()

        val mesAtual = obterMesAtualPorExtenso()
        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()

        lifecycleScope.launch {
            val valorReceitaUsuarioLogadoMesAtual = receita.totalReceitaMes(idUsuario, mesAtual)
            val valorDespesaUsuarioLogadoMesAtual = despesa.totalDespesaMes(idUsuario, mesAtual)

            val total = valorReceitaUsuarioLogadoMesAtual - valorDespesaUsuarioLogadoMesAtual

            cardSaldo = findViewById<LinearLayout>(R.id.cardSaldo)
            tvSaldoTotal = findViewById<TextView>(R.id.tvSaldoTotal)
            tvReceitaMes = findViewById<TextView>(R.id.tvReceitaMes)
            tvDespesaMes = findViewById<TextView>(R.id.tvDespesaMes)

            if (total > 0.0) {
                cardSaldo.setBackgroundResource(R.drawable.fundo_saldo)
                tvSaldoTotal.text = 0.0
                tvReceiraMes.text = 0.0
                tvDespesaMes.text = 0.0
            } else if(total < 0.0){
                cardSaldo.setBackgroundResource(R.drawable.fundo_vermelho)
                tvSaldoTotal = total
                tvReceitaMes = valorReceitaUsuarioLogadoMesAtual
                tvDespesaMes = valorDespesaUsuarioLogadoMesAtual
            }else{
                cardSaldo.setBackgroundResource(R.drawable.fundo_saldo_zerado)
                tvSaldoTotal = total
                tvReceitaMes = valorReceitaUsuarioLogadoMesAtual
                tvDespesaMes = valorDespesaUsuarioLogadoMesAtual
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
