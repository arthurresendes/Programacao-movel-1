package com.example.koin.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.withTransaction
import com.example.koin.R
import com.example.koin.data.AppDatabase
import com.example.koin.data.DadosEntrePaginas
import com.example.koin.data.Orcamento
import com.example.koin.utils.Categorias
import com.example.koin.utils.periodoDoMesAtual
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

class GerenciarLimitesBottomSheet : BottomSheetDialogFragment() {

    private val camposLimite = LinkedHashMap<String, EditText>()
    private val textosGasto = HashMap<String, TextView>()

    private var limitesExistentes: Map<String, Orcamento> = emptyMap()

    private lateinit var btSalvar: TextView

    private val formatoMoeda =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun getTheme(): Int = R.style.Koin_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(
        R.layout.bottom_sheet_gerenciar_limites,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btSalvar = view.findViewById(R.id.btSalvarLimites)

        val container = view.findViewById<LinearLayout>(
            R.id.containerLimites
        )


        Categorias.despesa.forEach { categoria ->

            val linha = layoutInflater.inflate(
                R.layout.item_limite_categoria,
                container,
                false
            )

            linha.findViewById<TextView>(
                R.id.tvNomeCategoriaLimite
            ).text = categoria

            textosGasto[categoria] = linha.findViewById(
                R.id.tvGastoCategoriaLimite
            )

            val campo = linha.findViewById<EditText>(
                R.id.etLimiteCategoria
            )

            campo.isSaveEnabled = false
            campo.contentDescription = "Limite mensal de $categoria"


            configurarMascaraMonetaria(campo)

            camposLimite[categoria] = campo

            container.addView(linha)
        }


        camposLimite.values.lastOrNull()?.apply {
            imeOptions = EditorInfo.IME_ACTION_DONE

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    salvarLimites()
                    true
                } else {
                    false
                }
            }
        }

        btSalvar.setOnClickListener {
            salvarLimites()
        }

        carregarLimites()
    }

    override fun onStart() {
        super.onStart()

        val sheetDialog = dialog as? BottomSheetDialog ?: return

        val sheet = sheetDialog.findViewById<FrameLayout>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return


        sheet.layoutParams = sheet.layoutParams.apply {
            height = (resources.displayMetrics.heightPixels * 0.85).toInt()
        }

        BottomSheetBehavior.from(sheet).apply {
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        sheetDialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }



    private fun configurarMascaraMonetaria(campo: EditText) {

        val simbolos = DecimalFormatSymbols(Locale("pt", "BR"))

        val formato = DecimalFormat("#,##0.00", simbolos).apply {
            roundingMode = RoundingMode.HALF_UP
            isGroupingUsed = true
        }

        var formatando = false

        campo.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) = Unit

            override fun afterTextChanged(s: Editable?) {

                if (formatando) return

                formatando = true

                try {

                    val digitos = s
                        ?.filter { it.isDigit() }
                        ?.take(12)
                        ?.toString()
                        .orEmpty()

                    val textoFormatado = if (digitos.isEmpty()) {
                        ""
                    } else {
                        val valor = BigDecimal(digitos)
                            .movePointLeft(2)

                        formato.format(valor)
                    }

                    if (textoFormatado != s.toString()) {
                        campo.setText(textoFormatado)
                        campo.setSelection(campo.text.length)
                    }

                } finally {
                    formatando = false
                }
            }
        })
    }




    private fun converterParaCentavos(texto: String): Long? {

        val digitos = texto.filter { it.isDigit() }

        if (digitos.isEmpty()) return null

        return try {
            digitos.toLong()
        } catch (e: NumberFormatException) {
            null
        }
    }


    private fun formatarMoeda(centavos: Long): String {
        return formatoMoeda.format(BigDecimal.valueOf(centavos, 2))
    }


    private fun formatarParaCampo(centavos: Long): String {
        return BigDecimal.valueOf(centavos, 2)
            .setScale(2, RoundingMode.HALF_UP)
            .toPlainString()
            .replace(".", ",")
            .let { valor ->
                val partes = valor.split(",")

                val parteInteira = partes[0]
                    .reversed()
                    .chunked(3)
                    .joinToString(".")
                    .reversed()

                "$parteInteira,${partes[1]}"
            }
    }



    private fun carregarLimites() {

        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()

        if (idUsuario == 0) {
            Toast.makeText(
                requireContext(),
                "Não foi possível identificar o usuário.",
                Toast.LENGTH_SHORT
            ).show()

            dismiss()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())

                val (inicioMes, inicioProximoMes) =
                    periodoDoMesAtual()

                limitesExistentes = db.orcamentoDao()
                    .listarOrcamentos(idUsuario)
                    .associateBy { it.nomeCategoria }

                val gastos = db.despesasDao()
                    .totalPorCategoriaPeriodo(
                        idUsuario,
                        inicioMes,
                        inicioProximoMes
                    )
                    .associate {
                        it.nomeCategoria to it.total_categoria
                    }

                camposLimite.forEach { (categoria, campo) ->

                    limitesExistentes[categoria]?.let {
                        campo.setText(
                            formatarParaCampo(it.valorLimite)
                        )
                    }

                    "Gasto este mês: ${
                        formatarMoeda((gastos[categoria] ?: 0.0).toLong())
                    }"
                }

            } catch (e: Exception) {
                if (!isAdded) return@launch

                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar os limites.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }



    private fun salvarLimites() {

        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()

        if (idUsuario == 0) return

        val paraSalvar = mutableListOf<Orcamento>()
        val paraRemover = mutableListOf<String>()


        for ((categoria, campo) in camposLimite) {

            campo.error = null

            val texto = campo.text.toString().trim()
            val existente = limitesExistentes[categoria]

            if (texto.isEmpty()) {
                if (existente != null) {
                    paraRemover.add(categoria)
                }
                continue
            }

            val valorCentavos = converterParaCentavos(texto)

            if (valorCentavos == null) {
                campo.error = "Valor inválido"
                campo.requestFocus()
                return
            }


            if (valorCentavos <= 0L) {
                if (existente != null) {
                    paraRemover.add(categoria)
                }
                continue
            }

            if (valorCentavos > VALOR_MAXIMO) {
                campo.error = "Valor muito alto"
                campo.requestFocus()
                return
            }

            paraSalvar.add(
                Orcamento(
                    id = existente?.id ?: 0,
                    nomeCategoria = categoria,
                    valorLimite = valorCentavos,
                    idFkUsuario = idUsuario
                )
            )
        }


        btSalvar.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                val dao = db.orcamentoDao()

                db.withTransaction {

                    paraRemover.forEach { categoria ->
                        dao.excluirPorCategoria(idUsuario, categoria)
                    }

                    paraSalvar.forEach { orcamento ->
                        dao.salvarOrcamento(orcamento)
                    }
                }

                if (!isAdded) return@launch

                parentFragmentManager.setFragmentResult(
                    RESULTADO_LIMITES_SALVOS,
                    Bundle()
                )

                Toast.makeText(
                    requireContext(),
                    "Limites salvos!",
                    Toast.LENGTH_SHORT
                ).show()

                dismiss()

            } catch (e: Exception) {
                if (!isAdded) return@launch

                btSalvar.isEnabled = true

                Toast.makeText(
                    requireContext(),
                    "Não foi possível salvar. Tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {

        const val TAG = "GerenciarLimitesBottomSheet"

        const val RESULTADO_LIMITES_SALVOS = "limites_salvos"


        private const val VALOR_MAXIMO = 9_999_999_999L
    }
}