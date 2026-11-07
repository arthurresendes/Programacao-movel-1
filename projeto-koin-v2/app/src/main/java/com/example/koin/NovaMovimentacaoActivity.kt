
package com.example.koin

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
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
import com.example.koin.data.Despesas
import com.example.koin.data.Receita
import com.example.koin.utils.Categorias
import com.example.koin.utils.ConverterValor
import com.example.koin.utils.iconeDaCategoria
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.launch

private data class DadosMovimentacao(
    val tipo: String,
    val valor: Long,
    val descricao: String?,
    val categoria: String,
    val data: Date,
)

class NovaMovimentacaoActivity : AppCompatActivity() {



    private lateinit var btFechar: TextView
    private lateinit var btDespesa: TextView
    private lateinit var btReceita: TextView

    private lateinit var tvRotuloValor: TextView

    private lateinit var cardValor: LinearLayout

    private lateinit var etValor: EditText
    private lateinit var etDescricao: EditText

    private lateinit var btCategoria: LinearLayout
    private lateinit var tvCategoriaSelecionada: TextView
    private lateinit var ivIconeCategoria: ImageView

    private lateinit var btData: LinearLayout
    private lateinit var tvDataSelecionada: TextView

    private lateinit var btSalvar: TextView



    private var tipoAtual = TIPO_DESPESA
    private var categoriaSelecionada: String? = null


    private var dataSelecionada: Long = System.currentTimeMillis()

    private val formatoDataCompleta =
        SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))



    private fun configurarMascaraMonetaria() {

        val formatoMoeda = NumberFormat.getCurrencyInstance(
            Locale("pt", "BR")
        )

        etValor.addTextChangedListener(object : TextWatcher {

            private var editando = false

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}

            override fun afterTextChanged(s: Editable?) {

                if (editando) return

                editando = true

                val digitos = s.toString().filter {
                    it.isDigit()
                }

                if (digitos.isEmpty()) {

                    etValor.setText("")

                } else {

                    val centavos = digitos.toLongOrNull() ?: 0L

                    val valor = centavos.toBigDecimal()
                        .divide(100.toBigDecimal())

                    etValor.setText(formatoMoeda.format(valor))
                    etValor.setSelection(etValor.text.length)
                }

                editando = false
            }
        })
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_nova_movimentacao)


        tipoAtual = savedInstanceState?.getString(CHAVE_TIPO) ?: TIPO_DESPESA

        categoriaSelecionada =
            savedInstanceState?.getString(CHAVE_CATEGORIA)

        dataSelecionada =
            savedInstanceState?.getLong(CHAVE_DATA, dataSelecionada)
                ?: dataSelecionada


        btFechar = findViewById(R.id.btFechar)
        btDespesa = findViewById(R.id.btDespesa)
        btReceita = findViewById(R.id.btReceita)

        tvRotuloValor = findViewById(R.id.tvRotuloValor)
        cardValor = findViewById(R.id.cardValor)

        etValor = findViewById(R.id.etValor)
        etDescricao = findViewById(R.id.etDescricao)

        btCategoria = findViewById(R.id.btCategoria)
        tvCategoriaSelecionada = findViewById(R.id.tvCategoriaSelecionada)
        ivIconeCategoria = findViewById(R.id.ivIconeCategoria)

        btData = findViewById(R.id.btData)
        tvDataSelecionada = findViewById(R.id.tvDataSelecionada)

        btSalvar = findViewById(R.id.btSalvarMovimentacao)



        btFechar.setOnClickListener {
            finish()
        }

        btDespesa.setOnClickListener {
            selecionarTipo(TIPO_DESPESA)
        }

        btReceita.setOnClickListener {
            selecionarTipo(TIPO_RECEITA)
        }

        btCategoria.setOnClickListener {
            abrirSeletorDeCategoria()
        }

        btData.setOnClickListener {
            abrirSeletorDeData()
        }


        btSalvar.setOnClickListener {

            btSalvar.animate().cancel()

            btSalvar.animate()
                .scaleX(0.96f)
                .scaleY(0.96f)
                .setDuration(80)
                .withEndAction {

                    btSalvar.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            aoClicarEmSalvar()
                        }
                        .start()
                }
                .start()
        }


        atualizarTipoNaTela()
        atualizarCategoriaNaTela()
        atualizarDataNaTela()

        configurarMascaraMonetaria()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(CHAVE_TIPO, tipoAtual)
        outState.putString(CHAVE_CATEGORIA, categoriaSelecionada)
        outState.putLong(CHAVE_DATA, dataSelecionada)
    }




    private fun selecionarTipo(tipo: String) {

        if (tipo == tipoAtual) return

        tipoAtual = tipo
        categoriaSelecionada = null

        atualizarTipoNaTela()
        atualizarCategoriaNaTela()
    }


    private fun atualizarTipoNaTela() {

        val ehDespesa = tipoAtual == TIPO_DESPESA

        marcarAba(btDespesa, ativa = ehDespesa)
        marcarAba(btReceita, ativa = !ehDespesa)

        tvRotuloValor.text =
            if (ehDespesa) "Valor da despesa" else "Valor da receita"

        etDescricao.hint =
            if (ehDespesa) "Ex.: Compra no supermercado" else "Ex.: Caiu o Salário"


        cardValor.animate().cancel()

        cardValor.animate()
            .alpha(0.5f)
            .setDuration(120)
            .withEndAction {

                cardValor.setBackgroundResource(
                    if (ehDespesa) {
                        R.drawable.fundo_vermelho
                    } else {
                        R.drawable.fundo_saldo
                    }
                )

                cardValor.animate()
                    .alpha(1f)
                    .setDuration(180)
                    .start()
            }
            .start()
    }


    private fun marcarAba(aba: TextView, ativa: Boolean) {

        aba.animate().cancel()

        aba.animate()
            .scaleX(0.96f)
            .scaleY(0.96f)
            .alpha(0.65f)
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
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(180)
                    .start()
            }
            .start()
    }




    private fun abrirSeletorDeCategoria() {

        val opcoes = Categorias.doTipo(tipoAtual)
        val indiceAtual = opcoes.indexOf(categoriaSelecionada)

        MaterialAlertDialogBuilder(this)
            .setTitle("Categoria")
            .setSingleChoiceItems(opcoes.toTypedArray(), indiceAtual) { dialog, which ->

                categoriaSelecionada = opcoes[which]

                atualizarCategoriaNaTela()

                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }




    private fun atualizarCategoriaNaTela() {

        val categoria = categoriaSelecionada

        if (categoria == null) {

            tvCategoriaSelecionada.text = "Selecionar categoria"
            tvCategoriaSelecionada.setTextColor(Color.parseColor("#536159"))
            tvCategoriaSelecionada.setTypeface(null, Typeface.NORMAL)

            ivIconeCategoria.setImageResource(R.drawable.filtro_inativo)

        } else {

            tvCategoriaSelecionada.text = categoria
            tvCategoriaSelecionada.setTextColor(Color.parseColor("#26332C"))
            tvCategoriaSelecionada.setTypeface(null, Typeface.BOLD)

            ivIconeCategoria.setImageResource(
                iconeDaCategoria(categoria, tipoAtual)
            )
        }


        tvCategoriaSelecionada.animate().cancel()
        ivIconeCategoria.animate().cancel()

        tvCategoriaSelecionada.alpha = 0f
        ivIconeCategoria.alpha = 0f

        tvCategoriaSelecionada.animate()
            .alpha(1f)
            .setDuration(250)
            .start()

        ivIconeCategoria.animate()
            .alpha(1f)
            .setDuration(250)
            .start()
    }




    private fun abrirSeletorDeData() {


        val restricao = CalendarConstraints.Builder()
            .setValidator(
                DateValidatorPointBackward.before(
                    paraUtcMeiaNoite(System.currentTimeMillis())
                )
            )
            .build()

        val seletor = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Data do registro")
            .setSelection(paraUtcMeiaNoite(dataSelecionada))
            .setCalendarConstraints(restricao)
            .build()

        seletor.addOnPositiveButtonClickListener { utcMillis ->

            dataSelecionada = deUtcParaDataLocal(utcMillis)

            atualizarDataNaTela()
        }

        seletor.show(supportFragmentManager, "seletor_data")
    }


    private fun atualizarDataNaTela() {

        val hoje = Calendar.getInstance()

        val ontem = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }

        val escolhida = Calendar.getInstance().apply {
            timeInMillis = dataSelecionada
        }

        tvDataSelecionada.text = when {

            mesmoDia(escolhida, hoje) -> "Hoje"

            mesmoDia(escolhida, ontem) -> "Ontem"

            else -> formatoDataCompleta.format(Date(dataSelecionada))
        }


        tvDataSelecionada.animate().cancel()

        tvDataSelecionada.alpha = 0f

        tvDataSelecionada.animate()
            .alpha(1f)
            .setDuration(250)
            .start()
    }


    private fun mesmoDia(a: Calendar, b: Calendar) =

        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)



    private fun paraUtcMeiaNoite(millis: Long): Long {

        val local = Calendar.getInstance().apply {
            timeInMillis = millis
        }

        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()

            set(
                local.get(Calendar.YEAR),
                local.get(Calendar.MONTH),
                local.get(Calendar.DAY_OF_MONTH)
            )

        }.timeInMillis
    }


    private fun deUtcParaDataLocal(utcMillis: Long): Long {

        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = utcMillis
        }

        val agora = Calendar.getInstance()

        return Calendar.getInstance().apply {
            set(
                utc.get(Calendar.YEAR),
                utc.get(Calendar.MONTH),
                utc.get(Calendar.DAY_OF_MONTH),
                agora.get(Calendar.HOUR_OF_DAY),
                agora.get(Calendar.MINUTE),
                agora.get(Calendar.SECOND)
            )

            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }




    private fun validarFormulario(): DadosMovimentacao? {

        etValor.error = null
        etDescricao.error = null


        val valor = ConverterValor.paraCentavos(
            etValor.text.toString()
        )

        if (valor <= 0L) {

            etValor.error = "Informe um valor maior que zero"
            etValor.requestFocus()

            return null
        }


        val descricao = etDescricao.text.toString()
            .trim()
            .ifEmpty { null }

        if (descricao != null && descricao.length > TAMANHO_MAX_DESCRICAO) {

            etDescricao.error =
                "Máximo de $TAMANHO_MAX_DESCRICAO caracteres"

            etDescricao.requestFocus()

            return null
        }


        val categoria = categoriaSelecionada

        if (categoria == null) {

            Toast.makeText(
                this,
                "Selecione uma categoria",
                Toast.LENGTH_SHORT
            ).show()

            return null
        }

        val data = Date(dataSelecionada)


        return DadosMovimentacao(
            tipoAtual,
            valor,
            descricao,
            categoria,
            data,
        )
    }




    private fun aoClicarEmSalvar() {

        val dados = validarFormulario() ?: return


        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()

        if (idUsuario == 0) {

            Toast.makeText(
                this,
                "Sessão expirada. Faça login novamente.",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        btSalvar.isEnabled = false

        lifecycleScope.launch {

            try {

                val db = AppDatabase.getDatabase(
                    this@NovaMovimentacaoActivity
                )


                if (dados.tipo == TIPO_DESPESA) {

                    db.despesasDao().cadastrarDespesa(

                        Despesas(
                            nomeCategoria = dados.categoria,
                            valorDespesa = dados.valor,
                            descricao = dados.descricao,
                            dataDespesaCadastrada = dados.data,
                            idFkUsuario = idUsuario
                        )
                    )

                } else {


                    db.receitaDao().cadastrarReceita(

                        Receita(
                            nomeCategoria = dados.categoria,
                            valor = dados.valor,
                            descricao = dados.descricao,
                            dataReceitaCadastrada = dados.data,
                            idFkUsuario = idUsuario
                        )
                    )
                }

                val nome = if (dados.tipo == TIPO_DESPESA) {
                    "Despesa"
                } else {
                    "Receita"
                }

                Toast.makeText(
                    this@NovaMovimentacaoActivity,
                    "$nome salva!",
                    Toast.LENGTH_SHORT
                ).show()


                finish()

            } catch (e: Exception) {


                btSalvar.isEnabled = true

                Toast.makeText(
                    this@NovaMovimentacaoActivity,
                    "Não foi possível salvar. Tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }



    companion object {

        private const val TIPO_DESPESA = "despesa"
        private const val TIPO_RECEITA = "receita"

        private const val CHAVE_TIPO = "tipo_movimentacao"
        private const val CHAVE_CATEGORIA = "categoria_movimentacao"
        private const val CHAVE_DATA = "data_movimentacao"

        private const val TAMANHO_MAX_DESCRICAO = 60
    }
}
