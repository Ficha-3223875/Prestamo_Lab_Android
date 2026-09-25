package com.example.prestamolab.util

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

data class BluetoothDispositivo(
    val nombre: String,
    val direccion: String
)

class BluetoothProvider(context: Context) {

    private val appContext = context.applicationContext

    private val adapter: BluetoothAdapter?
        get() = (appContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    fun estaSoportado(): Boolean = adapter != null

    fun permisoConectado(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED

    @Suppress("DEPRECATION")
    fun estaActivado(): Boolean = permisoConectado() && adapter?.isEnabled == true

    @Suppress("DEPRECATION")
    fun dispositivosVinculados(): List<BluetoothDispositivo> {
        if (!permisoConectado()) return emptyList()

        return adapter?.bondedDevices.orEmpty()
            .map { dispositivo ->
                BluetoothDispositivo(
                    nombre = dispositivo.name ?: "Dispositivo sin nombre",
                    direccion = dispositivo.address
                )
            }
            .sortedBy { it.nombre.lowercase() }
    }
}
