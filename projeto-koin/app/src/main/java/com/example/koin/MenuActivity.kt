package com.example.koin

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.koin.data.AppDatabase
import com.example.koin.data.DadosEntrePaginas
import com.example.koin.utils.direcionando
import com.example.koin.utils.obterMesAtualPorExtenso
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {
    private lateinit var tvNomeUsuario: TextView
    private lateinit var tvSaldoTotal: TextView
    private lateinit var tvReceitaMes: TextView
    private lateinit var tvDespesaMes: TextView
    private lateinit var cardSaldo: LinearLayout
    private lateinit var tvEntrada: TextView
    private lateinit var tvSaida: TextView

    private lateinit var movimentos: LinearLayout
    private lateinit var perfil: ImageButton
    private lateinit var adicionarDespesaReceita: TextView
    private lateinit var relatorios: LinearLayout
    private lateinit var metas: LinearLayout
    private lateinit var verTodasMovimentacoes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        tvNomeUsuario = findViewById(R.id.tvNomeUsuario)
        tvSaldoTotal = findViewById(R.id.tvSaldoAtual)
        tvReceitaMes = findViewById(R.id.tvReceitasMes)
        tvDespesaMes = findViewById(R.id.tvDespesasMes)
        tvEntrada = findViewById<TextView>(R.id.tvEntradas)
        tvSaida = findViewById<TextView>(R.id.tvSaidas)
        cardSaldo = findViewById(R.id.cardSaldo)

        perfil = findViewById<ImageButton>(R.id.ivFotoPerfil)
        movimentos = findViewById<LinearLayout>(R.id.navMovimentacoes)
        relatorios = findViewById<LinearLayout>(R.id.navRelatorios)
        adicionarDespesaReceita = findViewById<TextView>(R.id.btnAdicionarMovimento)
        metas = findViewById<LinearLayout>(R.id.navMetas)
        verTodasMovimentacoes = findViewById<TextView>(R.id.tvVerTodas)

        perfil.setOnClickListener {
            direcionando(this@MenuActivity, PerfilActivity::class.java)
        }
        movimentos.setOnClickListener {
            direcionando(this@MenuActivity, MovimentacaoActivity::class.java)
        }
        verTodasMovimentacoes.setOnClickListener {
            direcionando(this@MenuActivity, MovimentacaoActivity::class.java)
        }
        relatorios.setOnClickListener {
            direcionando(this@MenuActivity, RelatoriosActivity::class.java)
        }
        adicionarDespesaReceita.setOnClickListener {
            direcionando(this@MenuActivity, NovaMovimentacaoActivity::class.java)
        }
        metas.setOnClickListener {
            direcionando(this@MenuActivity, MetaActivity::class.java)
        }

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
            tvEntrada.text = "R$ %.2f".format(receitaMes)
            tvSaida.text = "R$ %.2f".format(despesaMes)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
