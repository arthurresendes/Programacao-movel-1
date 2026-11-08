package com.example.koin

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.koin.utils.direcionando
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.example.koin.data.AppDatabase
import com.example.koin.data.DadosEntrePaginas
import com.example.koin.data.response.TotalCategoria
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import android.widget.Toast
import com.example.koin.dialog.GerenciarLimitesBottomSheet
import com.example.koin.utils.FormatadorMonetario
import com.example.koin.utils.corDaCategoria
import com.example.koin.utils.exigirSessao
import com.example.koin.utils.periodoDoMesAtual

class RelatoriosActivity : AppCompatActivity() {


    private var consultaRelatorioJob: Job? = null

    private lateinit var tvRelatorioReceitas: TextView

    private lateinit var tvRelatorioDespesas: TextView

    private lateinit var tvRelatorioSaldo: TextView


    private lateinit var inicio: LinearLayout
    private lateinit var perfil: ImageButton
    private lateinit var adicionarDespesaReceita: TextView
    private lateinit var movimentacao: LinearLayout
    private lateinit var metas: LinearLayout
    private lateinit var btGerenciarLimites: TextView


    private lateinit var chartDespesas: PieChart


    private lateinit var btFiltroMes: TextView
    private lateinit var btFiltroTrimestre: TextView
    private lateinit var btFiltroAno: TextView
    private lateinit var tvSetaSaldo: TextView

    private var tipoAtual = TIPO_MES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_relatorios)
        if (!exigirSessao()) return


        tipoAtual = savedInstanceState?.getString(CHAVE_TIPO) ?: TIPO_MES
        tvRelatorioReceitas = findViewById<TextView>(R.id.tvRelatorioReceitas)
        tvRelatorioDespesas = findViewById<TextView>(R.id.tvRelatorioDespesas)
        tvRelatorioSaldo = findViewById<TextView>(R.id.tvRelatorioSaldo)
        tvSetaSaldo = findViewById<TextView>(R.id.tvSetaSaldo)

        perfil = findViewById<ImageButton>(R.id.ivFotoPerfil)
        inicio = findViewById<LinearLayout>(R.id.navInicio)
        movimentacao = findViewById<LinearLayout>(R.id.navMovimentacoes)
        adicionarDespesaReceita = findViewById<TextView>(R.id.btnAdicionarMovimento)
        metas = findViewById<LinearLayout>(R.id.navMetas)

        chartDespesas = findViewById<PieChart>(R.id.chartDespesas)
        btGerenciarLimites = findViewById<TextView>(R.id.btGerenciarLimites)

        btFiltroMes = findViewById<TextView>(R.id.btFiltroMes)
        btFiltroTrimestre = findViewById<TextView>(R.id.btFiltroTrimestre)
        btFiltroAno = findViewById<TextView>(R.id.btFiltroAno)


        configurarGraficoDespesas()


        btFiltroMes.setOnClickListener {
            selecionarPeriodo(TIPO_MES)
        }

        btFiltroTrimestre.setOnClickListener {
            selecionarPeriodo(TIPO_TRIMESTRE)
        }

        btFiltroAno.setOnClickListener {
            selecionarPeriodo(TIPO_ANO)
        }


        atualizarTipoNaTela()


        btGerenciarLimites.setOnClickListener {
            GerenciarLimitesBottomSheet().show(
                supportFragmentManager,
                GerenciarLimitesBottomSheet.TAG
            )
        }


        supportFragmentManager.setFragmentResultListener(
            GerenciarLimitesBottomSheet.RESULTADO_LIMITES_SALVOS,
            this
        ) { _, _ -> atualizarOrcamento() }


        perfil.setOnClickListener {
            direcionando(this@RelatoriosActivity, PerfilActivity::class.java)
        }

        inicio.setOnClickListener {
            direcionando(this@RelatoriosActivity, MenuActivity::class.java)
        }

        movimentacao.setOnClickListener {
            direcionando(this@RelatoriosActivity, MovimentacaoActivity::class.java)
        }

        adicionarDespesaReceita.setOnClickListener {
            direcionando(this@RelatoriosActivity, NovaMovimentacaoActivity::class.java)
        }

        metas.setOnClickListener {
            direcionando(this@RelatoriosActivity, MetaActivity::class.java)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarGraficoPorPeriodo()
        atualizarOrcamento()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CHAVE_TIPO, tipoAtual)
    }




    private fun configurarGraficoDespesas() {

        chartDespesas.description.isEnabled = false
        chartDespesas.legend.isEnabled = false
        chartDespesas.setDrawEntryLabels(false)

        chartDespesas.isDrawHoleEnabled = true
        chartDespesas.holeRadius = 65f
        chartDespesas.transparentCircleRadius = 70f

        chartDespesas.setHoleColor(Color.WHITE)
        chartDespesas.setTouchEnabled(false)


        chartDespesas.clear()
        chartDespesas.invalidate()
    }



    private fun selecionarPeriodo(tipo: String) {

        if (tipo == tipoAtual) return

        tipoAtual = tipo

        atualizarTipoNaTela()
        atualizarGraficoPorPeriodo()
    }

    private fun atualizarTipoNaTela() {

        marcarAba(btFiltroMes, tipoAtual == TIPO_MES)
        marcarAba(btFiltroTrimestre, tipoAtual == TIPO_TRIMESTRE)
        marcarAba(btFiltroAno, tipoAtual == TIPO_ANO)
    }


    private fun marcarAba(aba: TextView, ativa: Boolean) {

        aba.animate().cancel()

        aba.animate()
            .alpha(0.7f)
            .setDuration(100)
            .withEndAction {

                if (ativa) {
                    aba.setBackgroundResource(R.drawable.filtro_ativo)
                    aba.setTextColor(Color.WHITE)
                    aba.setTypeface(null, Typeface.BOLD)
                } else {
                    aba.setBackgroundResource(R.drawable.filtro_inativo)
                    aba.setTextColor(Color.parseColor("#536159"))
                    aba.setTypeface(null, Typeface.NORMAL)
                }

                aba.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }



    private fun atualizarGraficoPorPeriodo() {


        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()

        if (idUsuario == 0) {

            Toast.makeText(
                this,
                "Não foi possível identificar o usuário.",
                Toast.LENGTH_SHORT
            ).show()

            atualizarGrafico(emptyList(), 0.0)
            return
        }


        consultaRelatorioJob?.cancel()

        consultaRelatorioJob = lifecycleScope.launch {

            try {

                val (dataInicio, dataFim) = obterPeriodo(tipoAtual)

                val db = AppDatabase.getDatabase(this@RelatoriosActivity)
                val dao = db.despesasDao()


                val total = dao.totalDespesasPeriodo(
                    idUsuario,
                    dataInicio,
                    dataFim
                )

                tvRelatorioDespesas.text = FormatadorMonetario.formatar(total.toLong())

                val totalReceitas = db.receitaDao().totalReceitasPeriodo(
                    idUsuario,
                    dataInicio,
                    dataFim
                )

                tvRelatorioReceitas.text = FormatadorMonetario.formatar(totalReceitas.toLong())

                val saldo = totalReceitas - total

                tvRelatorioSaldo.text = FormatadorMonetario.formatar(saldo.toLong())

                atualizarIndicadorSaldo(saldo >= 0.0)

                val categorias = dao.totalPorCategoriaPeriodo(
                    idUsuario,
                    dataInicio,
                    dataFim
                )


                atualizarGrafico(categorias, total)

            } catch (e: Exception) {

                Toast.makeText(
                    this@RelatoriosActivity,
                    "Erro ao carregar os relatórios.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun atualizarIndicadorSaldo(positivo: Boolean) {
        if (positivo) {
            tvSetaSaldo.text = "↗"
            tvSetaSaldo.setTextColor(Color.parseColor("#16865B"))
            tvRelatorioSaldo.setTextColor(Color.parseColor("#16865B"))
        } else {
            tvSetaSaldo.text = "↓"
            tvSetaSaldo.setTextColor(Color.parseColor("#C65353"))
            tvRelatorioSaldo.setTextColor(Color.parseColor("#C65353"))
        }
    }

    private fun obterPeriodo(tipo: String): Pair<Date, Date> {

        val calendario = Calendar.getInstance()


        val inicio = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }


        val fim = inicio.clone() as Calendar

        when (tipo) {

            TIPO_MES -> {
                fim.add(Calendar.MONTH, 1)
            }

            TIPO_TRIMESTRE -> {


                inicio.add(Calendar.MONTH, -2)

                fim.add(Calendar.MONTH, 1)
            }

            TIPO_ANO -> {


                inicio.set(Calendar.MONTH, Calendar.JANUARY)


                fim.set(Calendar.YEAR, calendario.get(Calendar.YEAR))
                fim.set(Calendar.MONTH, Calendar.JANUARY)
                fim.add(Calendar.YEAR, 1)
            }
        }

        return Pair(inicio.time, fim.time)
    }




    private data class FatiaCategoria(
        val nome: String,
        val valor: Double,
        val cor: Int
    )

    private fun atualizarGrafico(
        categorias: List<TotalCategoria>,
        total: Double
    ) {

        val fatias = prepararFatias(categorias)

        if (fatias.isEmpty()) {

            chartDespesas.clear()

        } else {

            val conjunto = PieDataSet(
                fatias.map { PieEntry(it.valor.toFloat(), it.nome) },
                ""
            ).apply {

                colors = fatias.map { it.cor }
                setDrawValues(false)
                sliceSpace = 3f
            }

            chartDespesas.data = PieData(conjunto)
            chartDespesas.animateY(650)
        }


        findViewById<TextView>(R.id.tvTotalCategorias).text = FormatadorMonetario.formatar(total.toLong())

        chartDespesas.invalidate()

        atualizarLegenda(fatias)
    }

    private fun prepararFatias(categorias: List<TotalCategoria>): List<FatiaCategoria> {

        val ordenadas = categorias
            .filter { it.total_categoria > 0.0 }
            .sortedByDescending { it.total_categoria }
            .map { FatiaCategoria(it.nomeCategoria, it.total_categoria, corDaCategoria(it.nomeCategoria)) }

        if (ordenadas.size <= MAX_LINHAS_LEGENDA) return ordenadas

        val principais = ordenadas.take(MAX_LINHAS_LEGENDA - 1)
        val demais = ordenadas.drop(MAX_LINHAS_LEGENDA - 1)

        return principais + FatiaCategoria("Demais", demais.sumOf { it.valor }, COR_DEMAIS)
    }



    private fun atualizarLegenda(fatias: List<FatiaCategoria>) {

        val container = findViewById<LinearLayout>(R.id.containerLegendaCategorias)
        container.removeAllViews()

        if (fatias.isEmpty()) {
            container.addView(TextView(this).apply {
                text = "Sem despesas no período"
                setTextColor(Color.parseColor("#89958D"))
                textSize = 11f
            })
            return
        }


        val soma = fatias.sumOf { it.valor }

        fatias.forEach { fatia ->
            container.addView(criarLinhaLegenda(fatia, formatarPercentual(fatia.valor, soma)))
        }
    }

    private fun criarLinhaLegenda(fatia: FatiaCategoria, percentual: String): View {

        val densidade = resources.displayMetrics.density

        val linha = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (22 * densidade).toInt()
            )
        }

        val bolinha = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                (10 * densidade).toInt(),
                (10 * densidade).toInt()
            ).apply { marginEnd = (8 * densidade).toInt() }

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(fatia.cor)
            }
        }

        val tvNome = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = fatia.nome
            setTextColor(Color.parseColor("#536159"))
            textSize = 10f
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }

        val tvPercentual = TextView(this).apply {
            text = percentual
            setTextColor(Color.parseColor("#26332C"))
            textSize = 10f
        }

        linha.addView(bolinha)
        linha.addView(tvNome)
        linha.addView(tvPercentual)

        return linha
    }

    private fun formatarPercentual(valor: Double, total: Double): String {

        if (total <= 0.0) return "0%"

        val percentual = (valor / total) * 100


        return if (percentual > 0.0 && percentual < 1.0) "<1%" else "${Math.round(percentual)}%"
    }




    private fun atualizarOrcamento() {

        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()
        if (idUsuario == 0) return

        lifecycleScope.launch {

            try {

                val db = AppDatabase.getDatabase(this@RelatoriosActivity)
                val (inicioMes, inicioProximoMes) = periodoDoMesAtual()

                val limites = db.orcamentoDao().listarOrcamentos(idUsuario)

                val gastosPorCategoria = db.despesasDao()
                    .totalPorCategoriaPeriodo(idUsuario, inicioMes, inicioProximoMes)
                    .associate { it.nomeCategoria to it.total_categoria }

                val totalLimite = limites.sumOf { it.valorLimite }


                val totalUsado = limites.sumOf { gastosPorCategoria[it.nomeCategoria] ?: 0.0 }

                mostrarOrcamento(totalUsado, totalLimite)

            } catch (e: Exception) {

            }
        }
    }

    private fun mostrarOrcamento(usado: Double, limite: Long) {

        val tvPercentual = findViewById<TextView>(R.id.tvPercentualOrcamento)
        val progresso = findViewById<ProgressBar>(R.id.progressoOrcamento)
        val tvResumo = findViewById<TextView>(R.id.tvResumoOrcamento)



        if (limite <= 0L) {
            tvPercentual.text = "0%"
            progresso.progress = 0
            tvResumo.text = "Nenhum limite definido. Toque em Gerenciar limites."
            return
        }

        val percentual = ((usado / limite) * 100).toInt()


        val cor = when {
            percentual >= 100 -> Color.parseColor("#C65353")
            percentual >= 80 -> Color.parseColor("#D9A441")
            else -> Color.parseColor("#16865B")
        }

        tvPercentual.text = "$percentual%"
        tvPercentual.setTextColor(cor)
        progresso.progress = percentual.coerceAtMost(100)
        progresso.progressTintList = ColorStateList.valueOf(cor)
        tvResumo.text = "${FormatadorMonetario.formatar(usado.toLong())} de ${FormatadorMonetario.formatar(limite)} neste mês"
    }




    companion object {

        private const val TIPO_MES = "mes"
        private const val TIPO_TRIMESTRE = "trimestre"
        private const val TIPO_ANO = "ano"

        private const val CHAVE_TIPO = "tipo_relatorio"


        private const val MAX_LINHAS_LEGENDA = 6
        private val COR_DEMAIS = Color.rgb(201, 207, 203)
    }
}