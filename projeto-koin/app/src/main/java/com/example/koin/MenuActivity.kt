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

        tvNomeUsuario = findViewById(R.id.tvNomeUsuario)
        tvSaldoTotal = findViewById(R.id.tvSaldoAtual)
        tvReceitaMes = findViewById(R.id.tvReceitasMes)
        tvDespesaMes = findViewById(R.id.tvDespesasMes)
        cardSaldo = findViewById(R.id.cardSaldo)

        tvNomeUsuario.text = "Olá, ${DadosEntrePaginas.nome}!"

        val db = AppDatabase.getDatabase(this)
        val receitaDao = db.receitaDao()
        val despesaDao = db.despesasDao()

        val mesAtual = obterMesAtualPorExtenso()
        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()

        lifecycleScope.launch {
            val receitaMes = receitaDao.totalReceitaMes(idUsuario, mesAtual)
            val despesaMes = despesaDao.totalDespesaMes(idUsuario, mesAtual)
            val total = receitaMes - despesaMes

            val fundo = when {
                total > 0.0 -> R.drawable.fundo_saldo
                total < 0.0 -> R.drawable.fundo_vermelho
                else -> R.drawable.fundo_saldo_zerado
            }
            cardSaldo.setBackgroundResource(fundo)

            tvSaldoTotal.text = "R$ %.2f".format(total)
            tvReceitaMes.text = "R$ %.2f".format(receitaMes)
            tvDespesaMes.text = "R$ %.2f".format(despesaMes)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
