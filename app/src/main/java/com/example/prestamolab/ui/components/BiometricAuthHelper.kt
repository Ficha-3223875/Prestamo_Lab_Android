package com.example.prestamolab.ui.components

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

private fun Context.buscarFragmentActivity(): FragmentActivity? {
    var contexto = this
    while (contexto is ContextWrapper) {
        if (contexto is FragmentActivity) return contexto
        contexto = contexto.baseContext
    }
    return null
}

/**
 * Pide autenticación con huella (si el dispositivo la tiene) o PIN/patrón/contraseña
 * como alternativa. Cumple el requisito de "capacidad de hardware adicional" usando
 * la API oficial de biometría de Android, con reconocimiento gradual si el sensor
 * no está disponible.
 */
fun autenticarConHuellaOPin(
    context: Context,
    titulo: String,
    subtitulo: String,
    onExito: () -> Unit,
    onError: (String) -> Unit
) {
    val activity = context.buscarFragmentActivity()
    if (activity == null) {
        onError("No se pudo iniciar la autenticación.")
        return
    }

    val autenticadores = BiometricManager.Authenticators.BIOMETRIC_WEAK or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL

    val disponibilidad = BiometricManager.from(activity).canAuthenticate(autenticadores)
    if (disponibilidad != BiometricManager.BIOMETRIC_SUCCESS) {
        onError("Este dispositivo no tiene huella ni PIN/patrón configurado.")
        return
    }

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(titulo)
        .setSubtitle(subtitulo)
        .setAllowedAuthenticators(autenticadores)
        .build()

    val executor = ContextCompat.getMainExecutor(activity)
    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            onExito()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            onError(errString.toString())
        }

        override fun onAuthenticationFailed() {
            // Un intento fallido (huella no reconocida); BiometricPrompt deja reintentar solo.
        }
    }

    BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
}