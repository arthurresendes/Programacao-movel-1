package com.example.koin.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object GerenciadorSeguranca {

    private const val ALGORITMO = "PBKDF2WithHmacSHA1"
    private const val ITERACOES = 120000
    private const val TAMANHO_SAL = 16
    private const val TAMANHO_HASH_EM_BITS = 256

    fun gerarHash(senha: String): String {
        val sal = ByteArray(TAMANHO_SAL)
        SecureRandom().nextBytes(sal)
        val hash = derivar(senha, sal, ITERACOES)
        return "$ITERACOES:${codificar(sal)}:${codificar(hash)}"
    }

    fun verificar(senha: String, armazenado: String): Boolean {
        val partes = armazenado.split(":")
        if (partes.size != 3) return false
        val iteracoes = partes[0].toIntOrNull() ?: return false
        val sal = Base64.decode(partes[1], Base64.NO_WRAP)
        val esperado = Base64.decode(partes[2], Base64.NO_WRAP)
        val calculado = derivar(senha, sal, iteracoes)
        return MessageDigest.isEqual(esperado, calculado)
    }

    private fun derivar(senha: String, sal: ByteArray, iteracoes: Int): ByteArray {
        val especificacao = PBEKeySpec(senha.toCharArray(), sal, iteracoes, TAMANHO_HASH_EM_BITS)
        return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacao).encoded
    }

    private fun codificar(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
}