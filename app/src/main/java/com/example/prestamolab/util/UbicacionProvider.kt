package com.example.prestamolab.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Proveedor de ubicación (capacidad física GPS, Semana 9). */
fun interface UbicacionProvider {
    suspend fun obtenerUbicacion(): Result<Pair<Double, Double>>

    companion object {
        val NoDisponible = UbicacionProvider {
            Result.failure(IllegalStateException("La ubicación no está disponible en este dispositivo."))
        }
    }
}

class GpsUbicacionProvider(private val context: Context) : UbicacionProvider {

    @SuppressLint("MissingPermission")
    override suspend fun obtenerUbicacion(): Result<Pair<Double, Double>> =
        withContext(Dispatchers.IO) {
            val tienePermiso =
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (!tienePermiso) {
                return@withContext Result.failure(
                    SecurityException("El permiso de ubicación no ha sido concedido.")
                )
            }

            try {
                val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val proveedores = listOf(
                    LocationManager.GPS_PROVIDER,
                    LocationManager.NETWORK_PROVIDER
                )
                val ubicacion = proveedores.firstNotNullOfOrNull { proveedor ->
                    if (!manager.isProviderEnabled(proveedor)) null
                    else runCatching { manager.getLastKnownLocation(proveedor) }.getOrNull()
                }

                ubicacion?.let {
                    Result.success(it.latitude to it.longitude)
                } ?: Result.failure(
                    IllegalStateException("No se pudo obtener la ubicación actual. Verifica el GPS.")
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}