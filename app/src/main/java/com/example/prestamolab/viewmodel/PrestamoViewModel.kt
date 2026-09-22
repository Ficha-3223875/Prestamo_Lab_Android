package com.example.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prestamolab.data.destinoValido
import com.example.prestamolab.data.duracionValida
import com.example.prestamolab.data.propositoValido
import com.example.prestamolab.data.repository.PrestamoRepository
import com.example.prestamolab.model.CategoriaEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo
import com.example.prestamolab.util.NotificadorRecordatorio
import com.example.prestamolab.util.UbicacionProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class EstadoPagina { CARGANDO, CONTENIDO, VACIO, ERROR }

data class PrestamoUiState(
    val estadoCatalogo: EstadoPagina = EstadoPagina.CARGANDO,
    val estadoSolicitudes: EstadoPagina = EstadoPagina.CARGANDO,
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val operacionEnCurso: Boolean = false,
    val sincronizando: Boolean = false,
    val filtroCategoria: CategoriaEquipo? = null,
    val ubicacionActual: String? = null
)

class PrestamoViewModel(
    private val repository: PrestamoRepository,
    private val ubicacionProvider: UbicacionProvider = UbicacionProvider.NoDisponible,
    private val notificador: NotificadorRecordatorio = NotificadorRecordatorio.NoOp
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    private var ubicacionPendiente: Pair<Double, Double>? = null

    init {
        viewModelScope.launch {
            repository.observarEquipos()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            estadoCatalogo = EstadoPagina.ERROR,
                            mensaje = "No se pudo cargar el catálogo: ${e.message}"
                        )
                    }
                }
                .collect { lista ->
                    _uiState.update {
                        it.copy(
                            equipos = lista,
                            estadoCatalogo =
                                if (lista.isEmpty()) EstadoPagina.VACIO else EstadoPagina.CONTENIDO
                        )
                    }
                }
        }

        viewModelScope.launch {
            repository.observarSolicitudes()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            estadoSolicitudes = EstadoPagina.ERROR,
                            mensaje = "No se pudo cargar las solicitudes: ${e.message}"
                        )
                    }
                }
                .collect { lista ->
                    _uiState.update {
                        it.copy(
                            solicitudes = lista,
                            estadoSolicitudes =
                                if (lista.isEmpty()) EstadoPagina.VACIO else EstadoPagina.CONTENIDO
                        )
                    }
                }
        }

        viewModelScope.launch {
            repository.observarFiltroCategoria().collect { filtro ->
                _uiState.update { it.copy(filtroCategoria = filtro) }
            }
        }
    }

    fun equipo(id: Int): Equipo? = _uiState.value.equipos.firstOrNull { it.id == id }

    fun solicitud(id: Int): SolicitudPrestamo? = _uiState.value.solicitudes.firstOrNull { it.id == id }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun crearSolicitud(
        equipoId: Int,
        destino: String,
        proposito: String,
        duracionHoras: Int,
        onResult: (Boolean) -> Unit = {}
    ) {
        if (_uiState.value.operacionEnCurso) return

        val equipo = _uiState.value.equipos.firstOrNull { it.id == equipoId }
        if (equipo == null) {
            _uiState.update { it.copy(mensaje = "El equipo solicitado no existe.") }
            onResult(false)
            return
        }
        if (equipo.estado.name != "DISPONIBLE") {
            _uiState.update { it.copy(mensaje = "El equipo no está disponible.") }
            onResult(false)
            return
        }

        val error = when {
            !destinoValido(destino) -> "El ambiente o destino es obligatorio."
            !propositoValido(proposito) -> "El propósito debe tener entre 10 y 180 caracteres."
            !duracionValida(duracionHoras) -> "La duración debe estar entre 1 y 8 horas."
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(mensaje = error) }
            onResult(false)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(operacionEnCurso = true) }
            val ahora = System.currentTimeMillis()
            val solicitud = SolicitudPrestamo(
                id = 0,
                equipoId = equipoId,
                ambienteDestino = destino.trim(),
                proposito = proposito.trim(),
                duracionHoras = duracionHoras,
                estado = EstadoSolicitud.SOLICITADA,
                fechaSolicitud = ahora,
                fechaLimiteDevolucion = ahora + duracionHoras * 3_600_000L,
                latitud = ubicacionPendiente?.first,
                longitud = ubicacionPendiente?.second
            )

            val resultado = ejecutar { repository.crearSolicitud(solicitud) }

            ubicacionPendiente = null
            _uiState.update {
                it.copy(
                    operacionEnCurso = false,
                    mensaje = resultado.fold(
                        onSuccess = {
                            notificador.programar(0, equipo.nombre, solicitud.fechaLimiteDevolucion)
                            "Solicitud creada correctamente."
                        },
                        onFailure = { error ->
                            error.message ?: "No fue posible crear la solicitud."
                        }
                    )
                )
            }
            onResult(resultado.isSuccess)
        }
    }

    fun cancelarSolicitud(id: Int, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val resultado = ejecutar { repository.cancelarSolicitud(id) }
            _uiState.update {
                it.copy(
                    mensaje = resultado.fold(
                        onSuccess = { "Solicitud cancelada. El equipo volvió a estar disponible." },
                        onFailure = { error ->
                            error.message ?: "No fue posible cancelar la solicitud."
                        }
                    )
                )
            }
            onResult(resultado.isSuccess)
        }
    }

    fun registrarDevolucion(id: Int, evidenciaUri: String?, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val resultado = ejecutar { repository.registrarDevolucion(id, evidenciaUri) }
            _uiState.update {
                it.copy(
                    mensaje = resultado.fold(
                        onSuccess = {
                            "Devolución registrada. El equipo volvió a estar disponible."
                        },
                        onFailure = { error ->
                            error.message ?: "No fue posible registrar la devolución."
                        }
                    )
                )
            }
            onResult(resultado.isSuccess)
            if (resultado.isSuccess && evidenciaUri != null) {
                subirEvidenciasPendientes()
            }
        }
    }

    /** Convierte cualquier excepción lanzada por el repositorio en un Result recuperable. */
    private suspend fun ejecutar(operacion: suspend () -> Result<Unit>): Result<Unit> =
        try {
            operacion()
        } catch (e: Exception) {
            Result.failure(e)
        }

    fun sincronizar() {
        if (_uiState.value.sincronizando) return
        viewModelScope.launch {
            _uiState.update { it.copy(sincronizando = true) }
            val catalogo = ejecutar { repository.sincronizarCatalogo() }
            val solicitudes = ejecutar { repository.sincronizarSolicitudes() }
            _uiState.update {
                it.copy(
                    sincronizando = false,
                    mensaje = when {
                        catalogo.isSuccess && solicitudes.isSuccess ->
                            "Sincronización completada con el servicio remoto."
                        catalogo.isFailure ->
                            "No se pudo sincronizar: ${catalogo.exceptionOrNull()?.message ?: "error de red"}"
                        else ->
                            "Catálogo sincronizado. Hubo evidencias con error de red."
                    }
                )
            }
        }
    }

    /** Sube las evidencias pendientes (estado SUBIENDO) para cambiar a SINCRONIZADA/FALLIDA. */
    fun subirEvidenciasPendientes() {
        viewModelScope.launch {
            _uiState.update { it.copy(sincronizando = true) }
            val resultado = ejecutar { repository.sincronizarSolicitudes() }
            _uiState.update {
                it.copy(
                    sincronizando = false,
                    mensaje = if (resultado.isSuccess) {
                        "Evidencias sincronizadas al servicio."
                    } else {
                        "No se pudieron sincronizar las evidencias."
                    }
                )
            }
        }
    }

    fun capturarUbicacion() {
        viewModelScope.launch {
            val resultado = ubicacionProvider.obtenerUbicacion()
            resultado.fold(
                onSuccess = { (lat, lng) ->
                    ubicacionPendiente = lat to lng
                    _uiState.update { it.copy(ubicacionActual = "%.6f, %.6f".format(lat, lng)) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            ubicacionActual = null,
                            mensaje = e.message ?: "No se pudo obtener la ubicación."
                        )
                    }
                }
            )
        }
    }

    fun guardarFiltroCategoria(categoria: CategoriaEquipo?) {
        viewModelScope.launch {
            repository.guardarFiltroCategoria(categoria)
        }
    }
}