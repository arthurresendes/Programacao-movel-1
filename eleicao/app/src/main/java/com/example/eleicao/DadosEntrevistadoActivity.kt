package com.example.eleicao

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.eleicao.data.AppDatabase
import com.example.eleicao.data.Endereco
import com.example.eleicao.data.Entrevistado
import com.example.eleicao.data.PesquisaAtual
import com.example.eleicao.data.Resposta
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class DadosEntrevistadoActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var btFinalizar: Button
    private lateinit var etNome: EditText
    private lateinit var etTelefone: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_dados_entrevistado)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        etNome = findViewById(R.id.et_nome)
        etTelefone = findViewById(R.id.et_numero)
        btFinalizar = findViewById(R.id.btFinalizar)

        btFinalizar.setOnClickListener {
            finalizar()
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    private fun isTelefoneValido(telefone: String): Boolean {
        val apenasNumeros = telefone.replace(Regex("\\D"), "")
        if (apenasNumeros.length !in 10..11) {
            return false
        }
        if (apenasNumeros.all { it == apenasNumeros[0] }) {
            return false
        }

        if (apenasNumeros.length == 11 && apenasNumeros[2] != '9') {
            return false
        }

        return true
    }


    private fun finalizar() {
        val nome = etNome.text.toString().trim()
        val telefone = etTelefone.text.toString().trim()
        if (nome.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(
                this,
                "Preencha o nome e o telefone.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!isTelefoneValido((telefone))) {
            Toast.makeText(
                this,
                "Preencha o telefone corretamente.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        PesquisaAtual.nome = nome
        PesquisaAtual.telefone = telefone

        solicitarPermissaoLocalizacao()
    }

    private fun solicitarPermissaoLocalizacao() {

        val permissaoFine = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissaoCoarse = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissaoFine && !permissaoCoarse) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )

            return
        }

        obterLocalizacao()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 100) {

            val permissaoFine =
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            val permissaoCoarse =
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (permissaoFine || permissaoCoarse) {

                obterLocalizacao()

            } else {

                Toast.makeText(
                    this,
                    "A localização é necessária para finalizar a pesquisa.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun obterLocalizacao() {

        val permissaoFine =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val permissaoCoarse =
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!permissaoFine && !permissaoCoarse) {
            return
        }

        Toast.makeText(
            this,
            "Obtendo localização...",
            Toast.LENGTH_SHORT
        ).show()

        val prioridade =
            if (permissaoFine) {
                Priority.PRIORITY_HIGH_ACCURACY
            } else {
                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            }

        fusedLocationClient
            .getCurrentLocation(prioridade, null)
            .addOnSuccessListener { location ->

                if (location == null) {

                    Toast.makeText(
                        this,
                        "Não foi possível obter a localização. Verifique se a localização do aparelho está ativada.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                val latitude = location.latitude
                val longitude = location.longitude

                lifecycleScope.launch {

                    val endereco = withContext(Dispatchers.IO) {
                        obterEndereco(
                            latitude,
                            longitude
                        )
                    }

                    salvarPesquisa(
                        latitude,
                        longitude,
                        endereco
                    )
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Erro ao obter a localização.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun obterEndereco(
        latitude: Double,
        longitude: Double
    ): String {

        return try {

            val geocoder = Geocoder(
                this,
                Locale.getDefault()
            )

            @Suppress("DEPRECATION")
            val enderecos = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )

            if (!enderecos.isNullOrEmpty()) {

                enderecos[0].getAddressLine(0)
                    ?: "Endereço não identificado"

            } else {

                "Endereço não identificado"
            }

        } catch (e: Exception) {

            "Endereço não identificado"
        }
    }

    private fun salvarPesquisa(
        latitude: Double,
        longitude: Double,
        endereco: String
    ) {

        val banco = AppDatabase.getDatabase(this)

        val entrevistado = Entrevistado(
            nome = PesquisaAtual.nome,
            telefone = PesquisaAtual.telefone
        )

        val resposta = Resposta(
            problemas = PesquisaAtual.problemas,
            voto = PesquisaAtual.voto,
            candidatoEspontaneo = PesquisaAtual.candidatoEspontaneo
        )

        lifecycleScope.launch {

            try {

                withContext(Dispatchers.IO) {

                    val entrevistadoId =
                        banco.entrevistadoDao().inserir(entrevistado)

                    banco.respostaDao().inserir(resposta)

                    val localizacao = Endereco(
                        entrevistadoId = entrevistadoId.toInt(),
                        latitude = latitude,
                        longitude = longitude,
                        endereco = endereco
                    )

                    banco.localizacaoDao().inserir(localizacao)
                }

                Toast.makeText(
                    this@DadosEntrevistadoActivity,
                    "Pesquisa salva com sucesso!\n$endereco",
                    Toast.LENGTH_LONG
                ).show()

                PesquisaAtual.limpar()
                direcionando(this@DadosEntrevistadoActivity, EspontaneoActivity::class.java)

            } catch (e: Exception) {

                Toast.makeText(
                    this@DadosEntrevistadoActivity,
                    "Erro ao salvar a pesquisa.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}