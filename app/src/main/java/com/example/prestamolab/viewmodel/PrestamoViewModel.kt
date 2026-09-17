package com.example.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import com.example.prestamolab.data.repository.destinoValido
import com.example.prestamolab.data.repository.duracionValida
import com.example.prestamolab.data.repository.equipoDisponible
import com.example.prestamolab.data.repository.propositoValido
import com.example.prestamolab.data.repository.PrestamoRepository
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val guardando: Boolean = false
)

class PrestamoViewModel(
    private val repository: PrestamoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PrestamoUiState(
            equipos = repository.obtenerEquipos(),
            solicitudes = repository.obtenerSolicitudes()
        )
    )

    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    fun equipo(id: Int): Equipo? = repository.obtenerEquipo(id)

    fun solicitud(id: Int): SolicitudPrestamo? = repository.obtenerSolicitud(id)

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(mensaje = null)
    }

    fun crearSolicitud(
        equipoId: Int,
        destino: String,
        proposito: String,
        duracionHoras: Int
    ): Boolean {
        if (_uiState.value.guardando) return false

        val equipo = repository.obtenerEquipo(equipoId)
            ?: return mostrarError("El equipo solicitado no existe.")

        if (!equipoDisponible(equipo)) {
            return mostrarError("El equipo no está disponible.")
        }

        if (!destinoValido(destino)) {
            return mostrarError("El ambiente o destino es obligatorio.")
        }

        if (!propositoValido(proposito)) {
            return mostrarError("El propósito debe tener entre 10 y 180 caracteres.")
        }

        if (!duracionValida(duracionHoras)) {
            return mostrarError("La duración debe estar entre 1 y 8 horas.")
        }

        _uiState.value = _uiState.value.copy(guardando = true)

        val resultado = repository.crearSolicitud(
            SolicitudPrestamo(
                id = 0,
                equipoId = equipoId,
                ambienteDestino = destino.trim(),
                proposito = proposito.trim(),
                duracionHoras = duracionHoras,
                estado = EstadoSolicitud.SOLICITADA
            )
        )

        _uiState.value = _uiState.value.copy(
            equipos = repository.obtenerEquipos(),
            solicitudes = repository.obtenerSolicitudes(),
            mensaje = resultado.fold(
                onSuccess = { "Solicitud creada correctamente." },
                onFailure = { it.message ?: "No fue posible crear la solicitud." }
            ),
            guardando = false
        )

        return resultado.isSuccess
    }

    fun cancelarSolicitud(id: Int): Boolean {
        val resultado = repository.cancelarSolicitud(id)

        _uiState.value = _uiState.value.copy(
            equipos = repository.obtenerEquipos(),
            solicitudes = repository.obtenerSolicitudes(),
            mensaje = resultado.fold(
                onSuccess = { "Solicitud cancelada. El equipo volvió a estar disponible." },
                onFailure = { it.message ?: "No fue posible cancelar la solicitud." }
            )
        )

        return resultado.isSuccess
    }

    private fun mostrarError(mensaje: String): Boolean {
        _uiState.value = _uiState.value.copy(mensaje = mensaje)
        return false
    }
}