
package com.example.koin

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.koin.data.AppDatabase
import com.example.koin.data.DadosEntrePaginas
import com.example.koin.data.Meta
import com.example.koin.utils.direcionando
import com.example.koin.utils.infoDoPrazo
import com.example.koin.utils.temaDaMeta
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class MetaActivity : AppCompatActivity() {

    private lateinit var inicio: LinearLayout
    private lateinit var perfil: ImageButton
    private lateinit var adicionarDespesaReceita: TextView
    private lateinit var relatorios: LinearLayout
    private lateinit var movimetacao: LinearLayout

    private lateinit var btnCriarMeta: TextView
    private lateinit var containerMetas: LinearLayout
    private lateinit var tvSemMetas: TextView


    private lateinit var tvTotalGuardado: TextView
    private lateinit var tvObjetivoTotal: TextView
    private lateinit var progressoTotalMetas: ProgressBar
    private lateinit var tvPercentualTotal: TextView

    private val formatoMoeda =
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))


    private fun formatarMoeda(centavos: Long): String {
        val valorEmReais = BigDecimal.valueOf(centavos, 2)
        return formatoMoeda.format(valorEmReais)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meta)

        perfil = findViewById(R.id.ivFotoPerfil)
        inicio = findViewById(R.id.navInicio)
        relatorios = findViewById(R.id.navRelatorios)
        adicionarDespesaReceita = findViewById(R.id.btnAdicionarMovimento)
        movimetacao = findViewById(R.id.navMovimentacoes)

        btnCriarMeta = findViewById(R.id.btnCriarMeta)
        containerMetas = findViewById(R.id.containerMetas)
        tvSemMetas = findViewById(R.id.tvSemMetas)
        tvTotalGuardado = findViewById(R.id.tvTotalGuardado)
        tvObjetivoTotal = findViewById(R.id.tvObjetivoTotal)
        progressoTotalMetas = findViewById(R.id.progressoTotalMetas)
        tvPercentualTotal = findViewById(R.id.tvPercentualTotal)


        btnCriarMeta.setOnClickListener {
            NovaMetaBottomSheet().show(
                supportFragmentManager,
                NovaMetaBottomSheet.TAG
            )
        }


        supportFragmentManager.setFragmentResultListener(
            NovaMetaBottomSheet.RESULTADO_META_SALVA,
            this
        ) { _, _ ->
            carregarMetas()
        }


        supportFragmentManager.setFragmentResultListener(
            GuardarMetaBottomSheet.RESULTADO_VALOR_ATUALIZADO,
            this
        ) { _, _ ->
            carregarMetas()
        }


        supportFragmentManager.setFragmentResultListener(
            EditarMetaBottomSheet.RESULTADO_META_EDITADA,
            this
        ) { _, _ ->
            carregarMetas()
        }


        perfil.setOnClickListener {
            direcionando(this@MetaActivity, PerfilActivity::class.java)
        }

        inicio.setOnClickListener {
            direcionando(this@MetaActivity, MenuActivity::class.java)
        }

        relatorios.setOnClickListener {
            direcionando(this@MetaActivity, RelatoriosActivity::class.java)
        }

        adicionarDespesaReceita.setOnClickListener {
            direcionando(this@MetaActivity, NovaMovimentacaoActivity::class.java)
        }

        movimetacao.setOnClickListener {
            direcionando(this@MetaActivity, MovimentacaoActivity::class.java)
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
        carregarMetas()
    }



    private fun carregarMetas() {

        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()
        if (idUsuario == 0) return

        lifecycleScope.launch {
            try {
                val metas = AppDatabase.getDatabase(this@MetaActivity)
                    .metaDao()
                    .listar(idUsuario)

                mostrarMetas(metas)

            } catch (e: Exception) {
                Toast.makeText(
                    this@MetaActivity,
                    "Erro ao carregar as metas.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarMetas(metas: List<Meta>) {

        containerMetas.removeAllViews()

        val inflater = LayoutInflater.from(this)

        metas.forEach { meta ->
            containerMetas.addView(criarCardMeta(inflater, meta))
        }

        tvSemMetas.visibility =
            if (metas.isEmpty()) View.VISIBLE else View.GONE

        atualizarResumo(metas)
    }

    private fun criarCardMeta(inflater: LayoutInflater, meta: Meta): View {

        val card = inflater.inflate(
            R.layout.item_meta,
            containerMetas,
            false
        )


        card.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag(
                    GuardarMetaBottomSheet.TAG
                ) == null
            ) {
                GuardarMetaBottomSheet.novo(meta)
                    .show(supportFragmentManager, GuardarMetaBottomSheet.TAG)
            }
        }


        card.findViewById<ImageButton>(R.id.btnOpcoesMeta)
            .setOnClickListener { botao ->
                mostrarOpcoes(botao, meta)
            }

        val tema = temaDaMeta(meta.cor)

        card.findViewById<TextView>(R.id.tvIconeMeta).apply {
            text = meta.icone
            setBackgroundResource(tema.fundoIcone)
        }

        card.findViewById<TextView>(R.id.tvNomeMeta).text = meta.nome

        card.findViewById<TextView>(R.id.tvValorMeta).text =
            "${formatarMoeda(meta.valorGuardado)} de ${formatarMoeda(meta.valorAlvo)}"


        val percentual = if (meta.valorAlvo > 0L) {
            meta.valorGuardado.toDouble() / meta.valorAlvo.toDouble() * 100
        } else {
            0.0
        }

        card.findViewById<ProgressBar>(R.id.progressoMeta).apply {
            progress = percentual.toInt().coerceIn(0, 100)
            progressTintList = ColorStateList.valueOf(tema.corProgresso)
            progressBackgroundTintList = ColorStateList.valueOf(tema.corTrilha)
        }

        card.findViewById<TextView>(R.id.tvPercentualMeta).apply {
            setTextColor(tema.corProgresso)

            text = when {
                meta.valorGuardado <= 0L ->
                    "Comece a guardar um pouco por mês."

                percentual >= 100.0 ->
                    "Meta concluída!"

                else ->
                    "${formatarPercentual(percentual)}% concluído"
            }
        }


        card.findViewById<TextView>(R.id.tvPrazoMeta).apply {
            val info = infoDoPrazo(meta)

            if (info == null) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                text = info.texto
                setTextColor(
                    Color.parseColor(
                        if (info.vencido) "#C0392B" else "#89958D"
                    )
                )
            }
        }

        return card
    }



    private fun mostrarOpcoes(ancora: View, meta: Meta) {

        PopupMenu(this, ancora).apply {

            menu.add(0, OPCAO_EDITAR, 0, "Editar")
            menu.add(0, OPCAO_EXCLUIR, 1, "Excluir")

            setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    OPCAO_EDITAR -> {
                        if (supportFragmentManager.findFragmentByTag(
                                EditarMetaBottomSheet.TAG
                            ) == null
                        ) {
                            EditarMetaBottomSheet.novo(meta)
                                .show(
                                    supportFragmentManager,
                                    EditarMetaBottomSheet.TAG
                                )
                        }
                        true
                    }

                    OPCAO_EXCLUIR -> {
                        confirmarExclusao(meta)
                        true
                    }

                    else -> false
                }
            }

            show()
        }
    }

    private fun confirmarExclusao(meta: Meta) {

        val dialogo = AlertDialog.Builder(this)
            .setTitle("Excluir meta")
            .setMessage(
                "Deseja excluir a meta \"${meta.nome}\"? O valor guardado nela será perdido."
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Excluir") { _, _ ->
                excluirMeta(meta)
            }
            .create()

        dialogo.show()

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(Color.parseColor("#C0392B"))
    }

    private fun excluirMeta(meta: Meta) {

        val idUsuario = (DadosEntrePaginas.id ?: 0L).toInt()

        if (idUsuario == 0) {
            Toast.makeText(
                this,
                "Sessão expirada. Faça login novamente.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        lifecycleScope.launch {
            try {
                AppDatabase.getDatabase(this@MetaActivity)
                    .metaDao()
                    .excluir(meta.id, idUsuario)

                Toast.makeText(
                    this@MetaActivity,
                    "Meta excluída.",
                    Toast.LENGTH_SHORT
                ).show()

                carregarMetas()

            } catch (e: Exception) {
                Toast.makeText(
                    this@MetaActivity,
                    "Não foi possível excluir a meta.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }



    private fun atualizarResumo(metas: List<Meta>) {

        val totalGuardado = metas.sumOf { it.valorGuardado }
        val totalAlvo = metas.sumOf { it.valorAlvo }

        tvTotalGuardado.text = formatarMoeda(totalGuardado)

        if (metas.isEmpty() || totalAlvo <= 0L) {
            tvObjetivoTotal.text = "Crie sua primeira meta abaixo"
            progressoTotalMetas.progress = 0
            tvPercentualTotal.text = "0% do objetivo total"
            return
        }


        val percentual =
            totalGuardado.toDouble() / totalAlvo.toDouble() * 100

        tvObjetivoTotal.text =
            "de ${formatarMoeda(totalAlvo)} planejados"

        progressoTotalMetas.progress =
            percentual.toInt().coerceIn(0, 100)

        tvPercentualTotal.text =
            "${formatarPercentual(percentual)}% do objetivo total"
    }

    private fun formatarPercentual(valor: Double): String =
        String.format(
            Locale("pt", "BR"),
            "%.1f",
            valor
        ).removeSuffix(",0")

    private companion object {
        const val OPCAO_EDITAR = 1
        const val OPCAO_EXCLUIR = 2
    }
}
