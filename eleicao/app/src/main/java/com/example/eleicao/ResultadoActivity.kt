package com.example.eleicao

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.eleicao.data.AppDatabase
import com.example.eleicao.data.VotoContagem
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultadoActivity : AppCompatActivity() {

    private lateinit var chartEspontaneo: BarChart
    private lateinit var chartEstimulado: PieChart
    private lateinit var chartTemas: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resultado)

        chartEspontaneo = findViewById(R.id.chart_espontaneo)
        chartEstimulado = findViewById(R.id.chart_estimulado)
        chartTemas = findViewById(R.id.chart_temas)

        carregarResultado()
    }

    private fun carregarResultado() {
        val banco = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            val total = withContext(Dispatchers.IO) {
                banco.respostaDao().quantidadeRespostas()
            }
            findViewById<TextView>(R.id.textView).text =
                "Quant. de pessoas entrevistadas: $total"

            // 1) Espontâneo - top 5 - barras
            val top5 = withContext(Dispatchers.IO) {
                banco.respostaDao().top5Espontaneos()
            }
            montarBarras(chartEspontaneo, top5)

            // 2) Estimulado - pizza
            val votos = withContext(Dispatchers.IO) {
                banco.respostaDao().contagemPorVoto()
            }
            montarPizza(chartEstimulado, votos)

            // 3) Temas - vem como texto separado por vírgula, então
            // conto manualmente quantas vezes cada tema aparece
            val problemasCrus = withContext(Dispatchers.IO) {
                banco.respostaDao().listarTodosProblemas()
            }
            val contagemTemas = contarTemas(problemasCrus)
            montarBarras(chartTemas, contagemTemas)
        }
    }

    // Junta todas as strings "Saúde, Educação", separa por vírgula
    // e conta quantas vezes cada tema aparece
    private fun contarTemas(problemasCrus: List<String>): List<VotoContagem> {
        val contador = mutableMapOf<String, Int>()

        for (linha in problemasCrus) {
            val temas = linha.split(", ")
            for (tema in temas) {
                if (tema.isNotBlank()) {
                    contador[tema] = (contador[tema] ?: 0) + 1
                }
            }
        }

        return contador.map { (tema, quantidade) -> VotoContagem(tema, quantidade) }
            .sortedByDescending { it.quantidade }
    }

    private fun montarBarras(chart: BarChart, dados: List<VotoContagem>) {
        val entradas = dados.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.quantidade.toFloat())
        }
        val rotulos = dados.map { it.voto }

        val dataSet = BarDataSet(entradas, "Quantidade")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f

        chart.data = BarData(dataSet)
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(rotulos)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.granularity = 1f
        chart.xAxis.labelRotationAngle = -30f
        chart.description.isEnabled = false
        chart.animateY(800)
        chart.invalidate()
    }

    private fun montarPizza(chart: PieChart, dados: List<VotoContagem>) {
        val entradas = dados.map { PieEntry(it.quantidade.toFloat(), it.voto) }

        val dataSet = PieDataSet(entradas, "Votos")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 14f

        chart.data = PieData(dataSet)
        chart.description.isEnabled = false
        chart.centerText = "Voto estimulado"
        chart.animateY(800)
        chart.invalidate()
    }
}