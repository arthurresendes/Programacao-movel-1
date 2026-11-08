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
import com.example.koin.utils.FormatadorMonetario
import com.example.koin.utils.direcionando
import com.example.koin.utils.exigirSessao
import com.example.koin.utils.periodoDoMesAtual
import com.example.koin.utils.renderizarTransacoes
import kotlinx.coroutines.launch

class MenuActivity : AppCompatActivity() {
    private lateinit var tvNomeUsuario: TextView
    private lateinit var tvSaldoTotal: TextView
    private lateinit var tvReceitaMes: TextView
    private lateinit var tvDespesaMes: TextView
    private lateinit var cardSaldo: LinearLayout
    private lateinit var btVisualizar: TextView
    private lateinit var tvEntrada: TextView
    private lateinit var tvSaida: TextView

    private lateinit var movimentos: LinearLayout
    private lateinit var perfil: ImageButton
    private lateinit var adicionarDespesaReceita: TextView
    private lateinit var relatorios: LinearLayout
    private lateinit var metas: LinearLayout
    private lateinit var verTodasMovimentacoes: TextView
    private lateinit var containerUltimas: LinearLayout

    private var saldoOculto = false
    private var valorSaldo = 0L
    private var valorReceitaMes = 0L
    private var valorDespesaMes = 0L

    private var valorSaldoTotal = 0L
    private var valorDespesaTotal = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        if (!exigirSessao()) return

        saldoOculto = savedInstanceState?.getBoolean(CHAVE_SALDO_OCULTO) ?: false

        tvNomeUsuario = findViewById<TextView>(R.id.tvNomeUsuario)
        tvSaldoTotal = findViewById<TextView>(R.id.tvSaldoAtual)
        tvReceitaMes = findViewById<TextView>(R.id.tvReceitasMes)
        tvDespesaMes = findViewById<TextView>(R.id.tvDespesasMes)
        tvEntrada = findViewById<TextView>(R.id.tvEntradas)
        tvSaida = findViewById<TextView>(R.id.tvSaidas)
        btVisualizar = findViewById<TextView>(R.id.btnVisibilidadeSaldo)
        cardSaldo = findViewById<LinearLayout>(R.id.cardSaldo)

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
        btVisualizar.setOnClickListener {
            saldoOculto = !saldoOculto
            mostrarValores()
        }

        tvNomeUsuario.text = "Olá, ${DadosEntrePaginas.nome}!"

        mostrarValores()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(CHAVE_SALDO_OCULTO, saldoOculto)
    }

    override fun onResume() {
        super.onResume()
        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()
        val db = AppDatabase.getDatabase(this)
        val (inicioMes, inicioProximoMes) = periodoDoMesAtual()

        lifecycleScope.launch {
            val ultimas = db.transacaoDao().listar(idUsuario, "todas", "", 3)

            valorReceitaMes = db.receitaDao()
                .totalReceitasPeriodo(idUsuario, inicioMes, inicioProximoMes).toLong()
            valorDespesaMes = db.despesasDao()
                .totalDespesasPeriodo(idUsuario, inicioMes, inicioProximoMes).toLong()
            valorSaldoTotal = db.receitaDao().totalReceitas(idUsuario)
            valorDespesaTotal = db.despesasDao().totalDespesas(idUsuario)
            valorSaldo = valorSaldoTotal -
                    valorDespesaTotal -
                    db.metaDao().totalGuardado(idUsuario)

            renderizarTransacoes(this@MenuActivity, containerUltimas, ultimas)

            val fundo = when {
                valorSaldo > 0L -> R.drawable.fundo_saldo
                valorSaldo < 0L -> R.drawable.fundo_vermelho
                else -> R.drawable.fundo_saldo_zerado
            }
            cardSaldo.setBackgroundResource(fundo)

            mostrarValores()
        }
    }

    private fun mostrarValores() {
        if (saldoOculto) {
            tvSaldoTotal.text = TEXTO_OCULTO
            tvReceitaMes.text = TEXTO_OCULTO
            tvDespesaMes.text = TEXTO_OCULTO
            tvEntrada.text = TEXTO_OCULTO
            tvSaida.text = TEXTO_OCULTO
            btVisualizar.text = "○"
        } else {
            tvSaldoTotal.text = FormatadorMonetario.formatar(valorSaldo)
            tvReceitaMes.text = FormatadorMonetario.formatar(valorReceitaMes)
            tvDespesaMes.text = FormatadorMonetario.formatar(valorDespesaMes)
            tvEntrada.text = FormatadorMonetario.formatar(valorSaldoTotal)
            tvSaida.text = FormatadorMonetario.formatar(valorDespesaTotal)
            btVisualizar.text = "◉"
        }
    }

    companion object {
        private const val TEXTO_OCULTO = "••••"
        private const val CHAVE_SALDO_OCULTO = "saldo_oculto"
    }
}