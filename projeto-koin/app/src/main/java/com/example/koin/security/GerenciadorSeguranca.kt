package com.example.koin.security

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object GerenciadorSeguranca {

    private const val CHAVE_SECRETA = "MinhaChave123456"
    private const val ALGORITMO = "AES"

    fun criptografar(textoNormal: String): String {
        val cipher = Cipher.getInstance(ALGORITMO)
        val keySpec = SecretKeySpec(CHAVE_SECRETA.toByteArray(), ALGORITMO)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)

        val bytesCriptografados = cipher.doFinal(textoNormal.toByteArray())
        return Base64.encodeToString(bytesCriptografados, Base64.DEFAULT)
    }

    fun descriptografar(textoCriptografado: String): String {
        val cipher = Cipher.getInstance(ALGORITMO)
        val keySpec = SecretKeySpec(CHAVE_SECRETA.toByteArray(), ALGORITMO)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)

        val bytesDecodificados = Base64.decode(textoCriptografado, Base64.DEFAULT)
        val bytesOriginais = cipher.doFinal(bytesDecodificados)
        return String(bytesOriginais)
    }
}