package com.example.eleicao

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eleicao.data.PesquisaAtual

class EstimuladaActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var btConfirmar: Button

    private var candidatoSelecionado: String? = null
    private var candidatoSelecionadoView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_estimulada)

        radioGroup = findViewById(R.id.radioGroupOpcoes)
        btConfirmar = findViewById(R.id.btConfirmarEstimulada)

        val candidatoMew = findViewById<View>(R.id.candidatoMew)
        val candidatoPikachu = findViewById<View>(R.id.candidatoPikachu)
        val candidatoCharmander = findViewById<View>(R.id.candidatoCharmander)
        val candidatoBulbassauro = findViewById<View>(R.id.candidatoBulbassauro)
        val candidatoSquirtle = findViewById<View>(R.id.cardCandidatoUm)

        candidatoMew.setOnClickListener {
            selecionarCandidato("Mew", candidatoMew)
        }

        candidatoPikachu.setOnClickListener {
            selecionarCandidato("Pikachu", candidatoPikachu)
        }

        candidatoCharmander.setOnClickListener {
            selecionarCandidato("Charmander", candidatoCharmander)
        }

        candidatoBulbassauro.setOnClickListener {
            selecionarCandidato("Bulbassauro", candidatoBulbassauro)
        }

        candidatoSquirtle.setOnClickListener {
            selecionarCandidato("Squirtle", candidatoSquirtle)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId != -1) {

                val radioButton = findViewById<RadioButton>(checkedId)

                candidatoSelecionado = radioButton.text.toString()
                candidatoSelecionadoView?.setBackgroundResource(
                    R.drawable.canditado_escolhido
                )

                candidatoSelecionadoView = null
            }
        }

       
        btConfirmar.setOnClickListener {

            if (candidatoSelecionado != null) {

                val valor = candidatoSelecionado!!

                PesquisaAtual.voto = valor

                Toast.makeText(
                    this,
                    "Candidato selecionado: $valor",
                    Toast.LENGTH_SHORT
                ).show()
                direcionando(
                    this@EstimuladaActivity,
                    ProblemasActivity::class.java
                )
            } else {

                Toast.makeText(
                    this,
                    "Selecione um candidato",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

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

    private fun selecionarCandidato(
        candidato: String,
        view: View
    ) {


        candidatoSelecionadoView?.setBackgroundResource(
            R.drawable.canditado_escolhido
        )


        radioGroup.clearCheck()


        candidatoSelecionado = candidato
        candidatoSelecionadoView = view

        view.setBackgroundResource(
            R.drawable.candidato_selecionado
        )
    }
}