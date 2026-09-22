package com.example.prestamolab.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Almacenamiento seguro (Semana 9): el token conceptual se cifra con una
 * clave AES/GCM generada en el Android Keystore y solo se persiste su forma
 * cifrada. Nunca se guarda el token en texto plano.
 */
class TokenStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("prestamolab_seguro", Context.MODE_PRIVATE)

    private val alias = "prestamolab_token_clave"
    private val claveToken = "token_cifrado"
    private val claveIv = "token_iv"

    fun obtenerToken(): String {
        descifrar(prefs.getString(claveToken, null), prefs.getString(claveIv, null))?.let {
            return it
        }
        // No existe o no pudo descifrarse (por ejemplo, en un tejido diferente):
        // se genera un nuevo token conceptual y se persiste cifrado.
        val nuevo = "tk_${java.util.UUID.randomUUID()}"
        guardarCifrado(nuevo)
        return nuevo
    }

    private fun guardarCifrado(token: String) {
        val clave: SecretKey = obtenerOCrearClave()
        val cipher = Cipher.getInstance(TRANSFORMACION)
        cipher.init(Cipher.ENCRYPT_MODE, clave)
        val cifrado = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
        prefs.edit()
            .putString(claveToken, Base64.encodeToString(cifrado, Base64.NO_WRAP))
            .putString(claveIv, Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            .apply()
    }

    private fun descifrar(token: String?, iv: String?): String? {
        if (token == null || iv == null) return null
        return try {
            val clave: SecretKey = obtenerOCrearClave()
            val cipher = Cipher.getInstance(TRANSFORMACION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                clave,
                GCMParameterSpec(128, Base64.decode(iv, Base64.NO_WRAP))
            )
            String(cipher.doFinal(Base64.decode(token, Base64.NO_WRAP)), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    @Suppress("NewApi")
    private fun obtenerOCrearClave(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(alias, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        generator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    private companion object {
        const val TRANSFORMACION = "AES/GCM/NoPadding"
    }
}