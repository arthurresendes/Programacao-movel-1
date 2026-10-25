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

class RelatoriosActivity : AppCompatActivity() {
    private lateinit var inicio: LinearLayout
    private lateinit var perfil: ImageButton
    private lateinit var adicionarDespesaReceita: TextView
    private lateinit var movimentacao: LinearLayout
    private lateinit var metas: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_relatorios)

        perfil = findViewById<ImageButton>(R.id.ivFotoPerfil)
        inicio = findViewById<LinearLayout>(R.id.navInicio)
        movimentacao = findViewById<LinearLayout>(R.id.navMovimentacoes)
        adicionarDespesaReceita = findViewById<TextView>(R.id.btnAdicionarMovimento)
        metas = findViewById<LinearLayout>(R.id.navMetas)

        perfil.setOnClickListener {
            direcionando(this@RelatoriosActivity, PerfilActivity::class.java)
        }
        inicio.setOnClickListener {
            direcionando(this@RelatoriosActivity, MenuActivity::class.java)
        }
        movimentacao.setOnClickListener {
            direcionando(this@RelatoriosActivity, MovimentacaoActivity::class.java)
        }
        adicionarDespesaReceita.setOnClickListener {
            direcionando(this@RelatoriosActivity, NovaMovimentacaoActivity::class.java)
        }
        metas.setOnClickListener {
            direcionando(this@RelatoriosActivity, MetaActivity::class.java)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}