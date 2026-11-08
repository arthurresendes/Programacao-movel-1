package com.example.koin.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
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
import com.example.koin.R
import com.example.koin.data.AppDatabase
import com.example.koin.data.DadosEntrePaginas
import com.example.koin.data.Meta
import com.example.koin.utils.CORES_META
import com.example.koin.utils.CampoPrazo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Date
import java.util.Locale

class NovaMetaBottomSheet : BottomSheetDialogFragment() {

    private lateinit var etNome: EditText
    private lateinit var etValor: EditText
    private lateinit var btCriar: TextView
    private lateinit var campoPrazo: CampoPrazo

    private val viewsIcones = mutableListOf<TextView>()
    private var iconeSelecionado = ICONES.first()

    private var formatandoValor = false

    override fun getTheme(): Int = R.style.Koin_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(
        R.layout.bottom_sheet_nova_meta,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iconeSelecionado =
            savedInstanceState?.getString(CHAVE_ICONE) ?: ICONES.first()

        etNome = view.findViewById(R.id.etNomeMeta)
        etValor = view.findViewById(R.id.etValorMeta)
        btCriar = view.findViewById(R.id.btCriarMetaSheet)

        campoPrazo = CampoPrazo(
            this,
            view.findViewById(R.id.llPrazoMeta),
            view.findViewById(R.id.tvPrazoMetaSheet),
            view.findViewById(R.id.btLimparPrazo)
        )

        campoPrazo.restaurarEstado(savedInstanceState)

        montarIcones(view.findViewById(R.id.containerIcones))


        configurarMascaraMonetaria()

        btCriar.setOnClickListener {
            criarMeta()
        }

        etValor.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                criarMeta()
                true
            } else {
                false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(CHAVE_ICONE, iconeSelecionado)

        if (::campoPrazo.isInitialized) {
            campoPrazo.guardarEstado(outState)
        }
    }

    override fun onStart() {
        super.onStart()

        val sheetDialog = dialog as? BottomSheetDialog ?: return

        val sheet = sheetDialog.findViewById<FrameLayout>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return

        BottomSheetBehavior.from(sheet).apply {
            skipCollapsed = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        sheetDialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }



    private fun configurarMascaraMonetaria() {

        val simbolos = DecimalFormatSymbols(Locale("pt", "BR"))

        val formato = DecimalFormat("#,##0.00", simbolos).apply {
            roundingMode = RoundingMode.HALF_UP
            isGroupingUsed = true
        }

        etValor.addTextChangedListener(object : TextWatcher {

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

                if (formatandoValor) return

                formatandoValor = true

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
                        etValor.setText(textoFormatado)
                        etValor.setSelection(etValor.text.length)
                    }

                } finally {
                    formatandoValor = false
                }
            }
        })
    }



    private fun montarIcones(container: LinearLayout) {

        val densidade = resources.displayMetrics.density

        ICONES.forEach { icone ->

            val item = TextView(requireContext()).apply {

                layoutParams = LinearLayout.LayoutParams(
                    (48 * densidade).toInt(),
                    (48 * densidade).toInt()
                ).apply {
                    marginEnd = (8 * densidade).toInt()
                }

                gravity = Gravity.CENTER
                text = icone
                textSize = 24f
                contentDescription = "Ícone $icone"

                setOnClickListener {
                    iconeSelecionado = icone
                    atualizarSelecaoDeIcones()
                }
            }

            viewsIcones.add(item)
            container.addView(item)
        }

        atualizarSelecaoDeIcones()
    }

    private fun atualizarSelecaoDeIcones() {

        viewsIcones.forEachIndexed { indice, item ->

            item.setBackgroundResource(
                if (ICONES[indice] == iconeSelecionado) {
                    R.drawable.bg_icone_meta_selecionado
                } else {
                    R.drawable.barra_busca
                }
            )
        }
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



    private fun criarMeta() {

        etNome.error = null
        etValor.error = null

        val nome = etNome.text.toString().trim()

        if (nome.isEmpty()) {
            etNome.error = "Dê um nome para a meta"
            etNome.requestFocus()
            return
        }

        val valorCentavos = converterParaCentavos(
            etValor.text.toString()
        )

        if (valorCentavos == null || valorCentavos <= 0L) {
            etValor.error = "Informe um valor maior que zero"
            etValor.requestFocus()
            return
        }

        if (valorCentavos > VALOR_MAXIMO) {
            etValor.error = "Valor muito alto"
            etValor.requestFocus()
            return
        }

        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()

        if (idUsuario == 0) {
            Toast.makeText(
                requireContext(),
                "Sessão expirada. Faça login novamente.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        btCriar.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val dao = AppDatabase
                    .getDatabase(requireContext())
                    .metaDao()

                val cor = CORES_META[
                    dao.contar(idUsuario) % CORES_META.size
                ]

                dao.inserir(
                    Meta(
                        nome = nome,
                        valorAlvo = valorCentavos,
                        valorGuardado = 0L,
                        icone = iconeSelecionado,
                        cor = cor,
                        dataCriacao = Date(),
                        prazo = campoPrazo.prazo?.let {
                            Date(it)
                        },
                        idFkUsuario = idUsuario
                    )
                )

                if (!isAdded) return@launch

                parentFragmentManager.setFragmentResult(
                    RESULTADO_META_SALVA,
                    Bundle()
                )

                Toast.makeText(
                    requireContext(),
                    "Meta criada!",
                    Toast.LENGTH_SHORT
                ).show()

                dismiss()

            } catch (e: Exception) {

                if (!isAdded) return@launch

                btCriar.isEnabled = true

                Toast.makeText(
                    requireContext(),
                    "Não foi possível criar a meta. Tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {

        const val TAG = "NovaMetaBottomSheet"
        const val RESULTADO_META_SALVA = "meta_salva"

        private const val CHAVE_ICONE = "icone_meta"


        private const val VALOR_MAXIMO = 9_999_999_999L

        val ICONES = listOf(
            "🎯", "📱", "✈️", "🛡️", "🏠", "🚗",
            "🎓", "🎁", "💻", "💍", "🏖️", "💰"
        )
    }
}