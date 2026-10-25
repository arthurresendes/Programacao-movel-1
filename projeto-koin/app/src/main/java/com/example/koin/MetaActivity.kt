package com.example.koin

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.koin.utils.direcionando

class MetaActivity : AppCompatActivity() {
    private lateinit var inicio: LinearLayout
    private lateinit var perfil: ImageButton
    private lateinit var adicionarDespesaReceita: TextView
    private lateinit var relatorios: LinearLayout
    private lateinit var movimetacao: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meta)
        perfil = findViewById<ImageButton>(R.id.ivFotoPerfil)
        inicio = findViewById<LinearLayout>(R.id.navInicio)
        relatorios = findViewById<LinearLayout>(R.id.navRelatorios)
        adicionarDespesaReceita = findViewById<TextView>(R.id.btnAdicionarMovimento)
        movimetacao = findViewById<LinearLayout>(R.id.navMovimentacoes)

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}