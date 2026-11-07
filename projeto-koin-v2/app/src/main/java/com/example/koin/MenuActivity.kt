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
import com.example.koin.utils.FormatadorMonetario
import com.example.koin.utils.periodoDoMesAtual
import com.example.koin.utils.renderizarTransacoes
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
    private lateinit var containerUltimas: LinearLayout

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
        containerUltimas = findViewById<LinearLayout>(R.id.containerUltimas)

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
    
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onResume() {
        super.onResume()
        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()
        val banco = AppDatabase.getDatabase(this)
        val (inicioMes, inicioProximoMes) = periodoDoMesAtual()

        lifecycleScope.launch {
            val ultimas = banco.transacaoDao().listar(idUsuario, "todas", "", 3)
            val receitaMes = banco.receitaDao()
                .totalReceitasPeriodo(idUsuario, inicioMes, inicioProximoMes).toLong()
            val despesaMes = banco.despesasDao()
                .totalDespesasPeriodo(idUsuario, inicioMes, inicioProximoMes).toLong()
            val saldo = receitaMes - despesaMes

            renderizarTransacoes(this@MenuActivity, containerUltimas, ultimas)

            val fundo = when {
                saldo > 0L -> R.drawable.fundo_saldo
                saldo < 0L -> R.drawable.fundo_vermelho
                else -> R.drawable.fundo_saldo_zerado
            }
            cardSaldo.setBackgroundResource(fundo)

            tvSaldoTotal.text = FormatadorMonetario.formatar(saldo)
            tvReceitaMes.text = FormatadorMonetario.formatar(receitaMes)
            tvDespesaMes.text = FormatadorMonetario.formatar(despesaMes)
            tvEntrada.text = FormatadorMonetario.formatar(receitaMes)
            tvSaida.text = FormatadorMonetario.formatar(despesaMes)
        }
    }
}
