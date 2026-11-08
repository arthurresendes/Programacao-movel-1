package com.example.koin.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.koin.R
import com.example.koin.data.AppDatabase
import com.example.koin.data.DadosEntrePaginas
import com.example.koin.data.Meta
import com.example.koin.utils.temaDaMeta
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

class GuardarMetaBottomSheet : BottomSheetDialogFragment() {

    private lateinit var etValor: EditText
    private lateinit var tvModoGuardar: TextView
    private lateinit var tvModoRetirar: TextView
    private lateinit var btConfirmar: TextView


    private var metaId = 0
    private var valorGuardado = 0L
    private var valorAlvo = 0L

    private var modoRetirar = false

    private val formatoMoeda =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun getTheme(): Int = R.style.Koin_BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(
        R.layout.bottom_sheet_guardar_meta,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        if (args == null) {
            dismiss()
            return
        }

        metaId = args.getInt(ARG_ID)
        valorGuardado = args.getLong(ARG_GUARDADO)
        valorAlvo = args.getLong(ARG_ALVO)
        modoRetirar = savedInstanceState?.getBoolean(CHAVE_MODO) ?: false

        etValor = view.findViewById<EditText>(R.id.etValorGuardar)
        tvModoGuardar = view.findViewById<TextView>(R.id.tvModoGuardar)
        tvModoRetirar = view.findViewById<TextView>(R.id.tvModoRetirar)
        btConfirmar = view.findViewById<TextView>(R.id.btConfirmarGuardar)

        view.findViewById<TextView>(R.id.tvIconeGuardar).apply {
            text = args.getString(ARG_ICONE)
            setBackgroundResource(
                temaDaMeta(args.getString(ARG_COR) ?: "").fundoIcone
            )
        }

        view.findViewById<TextView>(R.id.tvNomeGuardar).text =
            args.getString(ARG_NOME)

        val faltam = (valorAlvo - valorGuardado).coerceAtLeast(0L)

        view.findViewById<TextView>(R.id.tvResumoGuardar).text =
            if (faltam > 0L) {
                "${formatarMoeda(valorGuardado)} de ${formatarMoeda(valorAlvo)} · faltam ${formatarMoeda(faltam)}"
            } else {
                "${formatarMoeda(valorGuardado)} de ${formatarMoeda(valorAlvo)} · meta atingida"
            }

        tvModoGuardar.setOnClickListener {
            trocarModo(retirar = false)
        }

        tvModoRetirar.setOnClickListener {
            trocarModo(retirar = true)
        }

        atualizarModo()

        btConfirmar.setOnClickListener {
            confirmar()
        }

        etValor.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                confirmar()
                true
            } else {
                false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(CHAVE_MODO, modoRetirar)
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



    private fun trocarModo(retirar: Boolean) {
        modoRetirar = retirar
        etValor.error = null
        atualizarModo()
    }

    private fun atualizarModo() {
        estilizarAba(tvModoGuardar, selecionada = !modoRetirar)
        estilizarAba(tvModoRetirar, selecionada = modoRetirar)

        btConfirmar.text =
            if (modoRetirar) "Retirar da meta" else "Guardar na meta"
    }

    private fun estilizarAba(aba: TextView, selecionada: Boolean) {
        aba.setBackgroundResource(
            if (selecionada) R.drawable.botao_koin
            else R.drawable.barra_busca
        )

        aba.setTextColor(
            if (selecionada) Color.WHITE
            else Color.parseColor("#536159")
        )
    }


    private fun converterParaCentavos(texto: String): Long? {
        val valorLimpo = texto
            .trim()
            .replace("R$", "")
            .replace(" ", "")

        if (valorLimpo.isEmpty()) return null

        val valorNormalizado = when {
            valorLimpo.contains(",") ->
                valorLimpo.replace(".", "").replace(",", ".")

            else -> valorLimpo
        }

        return try {
            BigDecimal(valorNormalizado)
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .longValueExact()
        } catch (e: NumberFormatException) {
            null
        } catch (e: ArithmeticException) {
            null
        }
    }

    private fun formatarMoeda(centavos: Long): String {
        return formatoMoeda.format(
            BigDecimal.valueOf(centavos, 2)
        )
    }

    private fun confirmar() {

        etValor.error = null

        val valor = converterParaCentavos(etValor.text.toString())

        if (valor == null || valor <= 0L) {
            etValor.error = "Informe um valor maior que zero"
            etValor.requestFocus()
            return
        }

        if (valor > VALOR_MAXIMO) {
            etValor.error = "Valor muito alto"
            etValor.requestFocus()
            return
        }

        if (modoRetirar && valor > valorGuardado) {
            etValor.error =
                if (valorGuardado <= 0L) {
                    "Não há nada guardado nesta meta"
                } else {
                    "Você só tem ${formatarMoeda(valorGuardado)} guardado"
                }

            etValor.requestFocus()
            return
        }

        if (!modoRetirar && valor > VALOR_MAXIMO - valorGuardado) {
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

        val delta = if (modoRetirar) -valor else valor

        val concluiuAgora =
            !modoRetirar &&
                    valorGuardado < valorAlvo &&
                    valor >= valorAlvo - valorGuardado

        btConfirmar.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(requireContext())

                if (!modoRetirar) {
                    val disponivel = db.receitaDao().totalReceitas(idUsuario) -
                            db.despesasDao().totalDespesas(idUsuario) -
                            db.metaDao().totalGuardado(idUsuario)

                    if (valor > disponivel) {
                        if (!isAdded) return@launch
                        btConfirmar.isEnabled = true
                        etValor.error =
                            if (disponivel <= 0L) {
                                "Você não tem saldo disponível para guardar"
                            } else {
                                "Saldo disponível: ${formatarMoeda(disponivel)}"
                            }
                        etValor.requestFocus()
                        return@launch
                    }
                }

                val linhas = db.metaDao()
                    .ajustarValorGuardado(metaId, idUsuario, delta)

                if (!isAdded) return@launch

                if (linhas == 0) {
                    btConfirmar.isEnabled = true

                    Toast.makeText(
                        requireContext(),
                        "Meta não encontrada.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@launch
                }

                parentFragmentManager.setFragmentResult(
                    RESULTADO_VALOR_ATUALIZADO,
                    Bundle()
                )

                val mensagem = when {
                    concluiuAgora -> "Parabéns, meta concluída! 🎉"
                    modoRetirar -> "Valor retirado da meta."
                    else -> "Valor guardado!"
                }

                Toast.makeText(
                    requireContext(),
                    mensagem,
                    Toast.LENGTH_SHORT
                ).show()

                dismiss()

            } catch (e: Exception) {
                if (!isAdded) return@launch

                btConfirmar.isEnabled = true

                Toast.makeText(
                    requireContext(),
                    "Não foi possível atualizar a meta. Tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        const val TAG = "GuardarMetaBottomSheet"
        const val RESULTADO_VALOR_ATUALIZADO = "meta_valor_atualizado"

        private const val ARG_ID = "meta_id"
        private const val ARG_NOME = "meta_nome"
        private const val ARG_ICONE = "meta_icone"
        private const val ARG_COR = "meta_cor"
        private const val ARG_GUARDADO = "meta_guardado"
        private const val ARG_ALVO = "meta_alvo"
        private const val CHAVE_MODO = "modo_retirar"

        private const val VALOR_MAXIMO = 9_999_999_999L

        fun novo(meta: Meta): GuardarMetaBottomSheet =
            GuardarMetaBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, meta.id)
                    putString(ARG_NOME, meta.nome)
                    putString(ARG_ICONE, meta.icone)
                    putString(ARG_COR, meta.cor)
                    putLong(ARG_GUARDADO, meta.valorGuardado)
                    putLong(ARG_ALVO, meta.valorAlvo)
                }
            }
    }
}