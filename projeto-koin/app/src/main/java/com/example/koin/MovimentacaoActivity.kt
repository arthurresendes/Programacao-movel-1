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

class MovimentacaoActivity : AppCompatActivity() {
    private lateinit var inicio: LinearLayout
    private lateinit var perfil: ImageButton
    private lateinit var adicionarDespesaReceita: TextView
    private lateinit var relatorios: LinearLayout
    private lateinit var metas: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movimentacao)

        perfil = findViewById<ImageButton>(R.id.ivFotoPerfil)
        inicio = findViewById<LinearLayout>(R.id.navInicio)
        relatorios = findViewById<LinearLayout>(R.id.navRelatorios)
        adicionarDespesaReceita = findViewById<TextView>(R.id.btnAdicionarMovimento)
        metas = findViewById<LinearLayout>(R.id.navMetas)

        perfil.setOnClickListener {
            direcionando(this@MovimentacaoActivity, PerfilActivity::class.java)
        }
        inicio.setOnClickListener {
            direcionando(this@MovimentacaoActivity, MenuActivity::class.java)
        }
        relatorios.setOnClickListener {
            direcionando(this@MovimentacaoActivity, RelatoriosActivity::class.java)
        }
        adicionarDespesaReceita.setOnClickListener {
            direcionando(this@MovimentacaoActivity, NovaMovimentacaoActivity::class.java)
        }
        metas.setOnClickListener {
            direcionando(this@MovimentacaoActivity, MetaActivity::class.java)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}