package com.example.eleicao

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.CheckBox
import com.example.eleicao.data.PesquisaAtual
class ProblemasActivity : AppCompatActivity() {
    private lateinit var btConfirmar: Button
    private lateinit var cbSaude:CheckBox
    private lateinit var cbViolencia: CheckBox
    private lateinit var cbEconomia: CheckBox
    private lateinit var cbEducacao: CheckBox
    private lateinit var cbCorrupcao: CheckBox
    private lateinit var cbDesemprego: CheckBox
    private lateinit var cbFome: CheckBox
    private lateinit var cbDesigualdade: CheckBox
    private lateinit var cbAdministracao: CheckBox
    private lateinit var cbSalario: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_problemas)
        btConfirmar = findViewById<Button>(R.id.btConfirmar)
        cbSaude = findViewById(R.id.saude)
        cbViolencia = findViewById(R.id.violenciaSegurancaPublica)
        cbEconomia = findViewById(R.id.economiaInflacao)
        cbEducacao = findViewById(R.id.educacao)
        cbCorrupcao = findViewById(R.id.corrupcao)
        cbDesemprego = findViewById(R.id.desemprego)
        cbFome = findViewById(R.id.fomePobreza)
        cbDesigualdade = findViewById(R.id.desiquidadeSocial)
        cbAdministracao = findViewById(R.id.maAdmin)
        cbSalario = findViewById(R.id.salario)

        btConfirmar.setOnClickListener {

                    val algumSelecionado =
                        cbSaude.isChecked ||
                                cbViolencia.isChecked ||
                                cbEconomia.isChecked ||
                                cbEducacao.isChecked ||
                                cbCorrupcao.isChecked ||
                                cbDesemprego.isChecked ||
                                cbFome.isChecked ||
                                cbDesigualdade.isChecked ||
                                cbAdministracao.isChecked ||
                                cbSalario.isChecked

                    if (algumSelecionado) {

                        val problemasSelecionados = mutableListOf<String>()

                        if (cbSaude.isChecked) problemasSelecionados.add("Saúde")
                        if (cbViolencia.isChecked) problemasSelecionados.add("Violência e Segurança Pública")
                        if (cbEconomia.isChecked) problemasSelecionados.add("Economia e Inflação")
                        if (cbEducacao.isChecked) problemasSelecionados.add("Educação")
                        if (cbCorrupcao.isChecked) problemasSelecionados.add("Corrupção")
                        if (cbDesemprego.isChecked) problemasSelecionados.add("Desemprego")
                        if (cbFome.isChecked) problemasSelecionados.add("Fome e Pobreza")
                        if (cbDesigualdade.isChecked) problemasSelecionados.add("Desigualdade Social")
                        if (cbAdministracao.isChecked) problemasSelecionados.add("Má Administração")
                        if (cbSalario.isChecked) problemasSelecionados.add("Salário")

                        PesquisaAtual.problemas = problemasSelecionados.joinToString(", ")

                        val intent = Intent(
                            this@ProblemasActivity,
                            DadosEntrevistadoActivity::class.java
                        )

                        startActivity(intent)

                    } else {

                        Toast.makeText(
                            this,
                            "Selecione pelo menos um problema.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
        }
    }
}















