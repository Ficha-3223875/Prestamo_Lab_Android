package com.example.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestamolab.data.repository.destinoValido
import com.example.prestamolab.data.repository.duracionValida
import com.example.prestamolab.data.repository.equipoDisponible
import com.example.prestamolab.data.repository.propositoValido
import com.example.prestamolab.data.repository.PrestamoRepository
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val guardando: Boolean = false
)

class PrestamoViewModel(
    private val repository: PrestamoRepository
) : ViewModel() {

    private val _mensaje = MutableStateFlow<String?>(null)
    private val _guardando = MutableStateFlow(false)

    val uiState: StateFlow<PrestamoUiState> = combine(
        repository.observarEquipos(),
        repository.observarSolicitudes(),
        _mensaje,
        _guardando
    ) { equipos, solicitudes, mensaje, guardando ->
        PrestamoUiState(equipos, solicitudes, mensaje, guardando)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrestamoUiState()
    )

    fun equipo(id: Int): Equipo? = uiState.value.equipos.find { it.id == id }

    fun solicitud(id: Int): SolicitudPrestamo? = uiState.value.solicitudes.find { it.id == id }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun crearSolicitud(
        equipoId: Int,
        destino: String,
        proposito: String,
        duracionHoras: Int,
        fotoUri: String? = null,
        onResultado: (Boolean) -> Unit = {}
    ) {
        if (_guardando.value) {
            onResultado(false)
            return
        }

        val equipo = equipo(equipoId)
        if (equipo == null) {
            _mensaje.value = "El equipo solicitado no existe."
            onResultado(false)
            return
        }
        if (!equipoDisponible(equipo)) {
            _mensaje.value = "El equipo no está disponible."
            onResultado(false)
            return
        }
        if (!destinoValido(destino)) {
            _mensaje.value = "El ambiente o destino es obligatorio."
            onResultado(false)
            return
        }
        if (!propositoValido(proposito)) {
            _mensaje.value = "El propósito debe tener entre 10 y 180 caracteres."
            onResultado(false)
            return
        }
        if (!duracionValida(duracionHoras)) {
            _mensaje.value = "La duración debe estar entre 1 y 8 horas."
            onResultado(false)
            return
        }

        _guardando.value = true
        viewModelScope.launch {
            val resultado = repository.crearSolicitud(
                SolicitudPrestamo(
                    id = 0,
                    equipoId = equipoId,
                    ambienteDestino = destino.trim(),
                    proposito = proposito.trim(),
                    duracionHoras = duracionHoras,
                    estado = EstadoSolicitud.SOLICITADA,
                    fotoUriString = fotoUri
                )
            )

            _guardando.value = false
            _mensaje.value = resultado.fold(
                onSuccess = { "Solicitud creada correctamente." },
                onFailure = { it.message ?: "No fue posible crear la solicitud." }
            )
            onResultado(resultado.isSuccess)
        }
    }

    fun cancelarSolicitud(id: Int, onResultado: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val resultado = repository.cancelarSolicitud(id)
            _mensaje.value = resultado.fold(
                onSuccess = { "Solicitud cancelada. El equipo volvió a estar disponible." },
                onFailure = { it.message ?: "No fue posible cancelar la solicitud." }
            )
            onResultado(resultado.isSuccess)
        }
    }

    fun devolverSolicitud(id: Int, fotoDevolucionUri: String? = null, onResultado: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val resultado = repository.devolverSolicitud(id, fotoDevolucionUri)
            _mensaje.value = resultado.fold(
                onSuccess = { "Equipo devuelto correctamente. El equipo vuelve a estar disponible." },
                onFailure = { it.message ?: "No fue posible procesar la devolución." }
            )
            onResultado(resultado.isSuccess)
        }
    }
}