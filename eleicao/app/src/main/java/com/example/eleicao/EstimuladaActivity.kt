package com.example.eleicao

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EstimuladaActivity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var btConfirmar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_estimulada)
        radioGroup = findViewById<RadioGroup>(R.id.radioGroupOpcoes)
        btConfirmar = findViewById<Button>(R.id.btConfirmarEstimulada)

        btConfirmar.setOnClickListener {
            val selectedItem = radioGroup.checkedRadioButtonId

            if(selectedItem != -1){
                val radioButton =  findViewById<RadioButton>(selectedItem)
                val valor = radioButton.text.toString()
                val candidato = intent.getStringExtra("Espontaneo")
                Toast.makeText(applicationContext, "Candidato esponataneo: ${candidato}, Candidato estimulado: ${valor}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@EstimuladaActivity, ProblemasActivity::class.java).apply {
                    putExtra("Espontaneo", candidato)
                    putExtra("Estimulado", valor)
                }
                startActivity(intent)
            }else{
                Toast.makeText(applicationContext, "Selecione um candidato", Toast.LENGTH_SHORT).show()
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}