package com.example.koin

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
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
import com.example.koin.utils.renderizarAgrupadoPorData
import com.example.koin.utils.renderizarTransacoes
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MovimentacaoActivity : AppCompatActivity() {
    private lateinit var inicio: LinearLayout
    private lateinit var perfil: ImageButton
    private lateinit var adicionarDespesaReceita: TextView
    private lateinit var relatorios: LinearLayout
    private lateinit var metas: LinearLayout

    private lateinit var etBuscar: EditText
    private lateinit var containerTop3: LinearLayout
    private lateinit var containerTodas: LinearLayout
    private lateinit var filtroTodas: TextView
    private lateinit var filtroReceitas: TextView
    private lateinit var filtroDespesas: TextView

    private var tipoAtual = "todas"
    private var jobTop3: Job? = null
    private var jobLista: Job? = null

    private val transacaoDao by lazy { AppDatabase.getDatabase(this).transacaoDao() }
    private val idUsuario: Int get() = (DadosEntrePaginas.id ?: 0L).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movimentacao)

        perfil = findViewById(R.id.ivFotoPerfil)
        inicio = findViewById(R.id.navInicio)
        relatorios = findViewById(R.id.navRelatorios)
        adicionarDespesaReceita = findViewById(R.id.btnAdicionarMovimento)
        metas = findViewById(R.id.navMetas)

        etBuscar = findViewById(R.id.etBuscarMovimentacao)
        containerTop3 = findViewById(R.id.containerMovimentacoes)
        containerTodas = findViewById(R.id.containerTodas)
        filtroTodas = findViewById(R.id.filtroTodas)
        filtroReceitas = findViewById(R.id.filtroReceitas)
        filtroDespesas = findViewById(R.id.filtroDespesas)

        perfil.setOnClickListener {
            direcionando(this@MovimentacaoActivity, PerfilActivity::class.java)
        }
        inicio.setOnClickListener {
            direcionando(this@MovimentacaoActivity, MenuActivity::class.java)
        }
        relatorios.setOnClickListener {
            direcionando(this@MovimentacaoActivity, RelatoriosActivity::class.java)
        }
        adicionarDespesaReceita.setOnClickListener {
            direcionando(this@MovimentacaoActivity, NovaMovimentacaoActivity::class.java)
        }
        metas.setOnClickListener {
            direcionando(this@MovimentacaoActivity, MetaActivity::class.java)
        }

        filtroTodas.setOnClickListener { selecionarFiltro("todas") }
        filtroReceitas.setOnClickListener { selecionarFiltro("receita") }
        filtroDespesas.setOnClickListener { selecionarFiltro("despesa") }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { carregarLista() }
        })

        marcarFiltro()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        carregarTop3()
        carregarLista()
    }

    private fun selecionarFiltro(tipo: String) {
        tipoAtual = tipo
        marcarFiltro()
        carregarTop3()
        carregarLista()
    }

    private fun marcarFiltro() {
        mapOf(
            "todas" to filtroTodas,
            "receita" to filtroReceitas,
            "despesa" to filtroDespesas
        ).forEach { (tipo, botao) ->
            if (tipo == tipoAtual) {
                botao.setBackgroundResource(R.drawable.filtro_ativo)
                botao.setTextColor(Color.WHITE)
                botao.setTypeface(null, Typeface.BOLD)
            } else {
                botao.setBackgroundResource(R.drawable.filtro_inativo)
                botao.setTextColor(Color.parseColor("#89958D"))
                botao.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    private fun carregarTop3() {
        jobTop3?.cancel()
        jobTop3 = lifecycleScope.launch {
            val lista = transacaoDao.listar(idUsuario, tipoAtual, "", 3)
            renderizarTransacoes(this@MovimentacaoActivity, containerTop3, lista)
        }
    }

    private fun carregarLista() {
        val busca = etBuscar.text.toString().trim()
        jobLista?.cancel()
        jobLista = lifecycleScope.launch {
            val lista = transacaoDao.listar(idUsuario, tipoAtual, busca, -1)
            renderizarAgrupadoPorData(this@MovimentacaoActivity, containerTodas, lista)
        }
    }
}
