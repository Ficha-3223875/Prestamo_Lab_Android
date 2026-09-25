package com.example.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prestamolab.util.BluetoothDispositivo
import com.example.prestamolab.util.BluetoothProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BluetoothUiState(
    val soportado: Boolean = false,
    val permisoConcedido: Boolean = false,
    val activado: Boolean = false,
    val dispositivos: List<BluetoothDispositivo> = emptyList(),
    val mensaje: String? = null
)

class BluetoothViewModel(
    private val provider: BluetoothProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(BluetoothUiState())
    val uiState: StateFlow<BluetoothUiState> = _uiState.asStateFlow()

    init {
        actualizar()
    }

    fun actualizar() {
        val soportado = provider.estaSoportado()
        val permisoConcedido = provider.permisoConectado()
        val activado = if (permisoConcedido) provider.estaActivado() else false
        val dispositivos = if (permisoConcedido) {
            provider.dispositivosVinculados()
        } else {
            emptyList()
        }

        _uiState.value = BluetoothUiState(
            soportado = soportado,
            permisoConcedido = permisoConcedido,
            activado = activado,
            dispositivos = dispositivos,
            mensaje = when {
                !soportado -> "Este dispositivo no tiene Bluetooth."
                !permisoConcedido -> "Se necesita permiso para consultar los dispositivos vinculados."
                !activado -> "Activa Bluetooth para ver los dispositivos vinculados."
                dispositivos.isEmpty() -> "No hay dispositivos vinculados. Vincula uno desde Ajustes."
                else -> null
            }
        )
    }

    fun resultadoPermiso(concedido: Boolean) {
        if (concedido) {
            actualizar()
        } else {
            _uiState.value = _uiState.value.copy(
                permisoConcedido = false,
                mensaje = "No se concedió permiso para Bluetooth."
            )
        }
    }
}

class BluetoothViewModelFactory(
    private val provider: BluetoothProvider
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothViewModel::class.java)) {
            return BluetoothViewModel(provider) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
