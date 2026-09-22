package com.example.prestamolab.data.repository

import com.example.prestamolab.model.CategoriaEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.EstadoEvidencia
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementación Fake (en memoria) usada para pruebas unitarias y demos
 * rápidas. No persiste datos entre reinicios de la aplicación.
 */
open class InMemoryPrestamoRepository : PrestamoRepository {

    private val mutex = Mutex()

    private val _equipos = MutableStateFlow(
        listOf(
            Equipo(1, "Portátil Lenovo ThinkPad", CategoriaEquipo.COMPUTO, EstadoEquipo.DISPONIBLE),
            Equipo(2, "Video Beam Epson", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.DISPONIBLE),
            Equipo(3, "Kit Arduino UNO", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
            Equipo(4, "Router TP-Link", CategoriaEquipo.REDES, EstadoEquipo.RESERVADO),
            Equipo(5, "Portátil HP ProBook", CategoriaEquipo.COMPUTO, EstadoEquipo.PRESTADO),
            Equipo(6, "Cámara Logitech", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.DISPONIBLE)
        )
    )

    private val _solicitudes = MutableStateFlow<List<SolicitudPrestamo>>(emptyList())
    private val _filtroCategoria = MutableStateFlow<CategoriaEquipo?>(null)
    private var siguienteSolicitudId = 1

    override fun observarEquipos(): Flow<List<Equipo>> = _equipos.asStateFlow()

    override fun observarEquipo(id: Int): Flow<Equipo?> =
        observing { it.firstOrNull { e -> e.id == id } }

    override fun observarSolicitudes(): Flow<List<SolicitudPrestamo>> = _solicitudes.asStateFlow()

    override suspend fun obtenerEquipo(id: Int): Equipo? = _equipos.value.firstOrNull { it.id == id }

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        _solicitudes.value.firstOrNull { it.id == id }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> = mutex.withLock {
        val equipoIndex = _equipos.value.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex == -1) {
            return@withLock Result.failure(IllegalArgumentException("El equipo no existe."))
        }

        if (_equipos.value[equipoIndex].estado != EstadoEquipo.DISPONIBLE) {
            return@withLock Result.failure(IllegalStateException("El equipo ya no está disponible."))
        }

        val existeActiva = _solicitudes.value.any {
            it.equipoId == solicitud.equipoId &&
                it.estado in setOf(
                    EstadoSolicitud.SOLICITADA,
                    EstadoSolicitud.APROBADA,
                    EstadoSolicitud.ENTREGADA
                )
        }

        if (existeActiva) {
            return@withLock Result.failure(IllegalStateException("Ya existe una solicitud activa para este equipo."))
        }

        val nueva = solicitud.copy(
            id = siguienteSolicitudId++,
            estado = EstadoSolicitud.SOLICITADA,
            fechaSolicitud = solicitud.fechaSolicitud.takeIf { it > 0 } ?: System.currentTimeMillis()
        )

        _solicitudes.value = _solicitudes.value + nueva
        _equipos.value = _equipos.value.toMutableList().also { lista ->
            lista[equipoIndex] = lista[equipoIndex].copy(estado = EstadoEquipo.RESERVADO)
        }

        Result.success(Unit)
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> = mutex.withLock {
        val index = _solicitudes.value.indexOfFirst { it.id == id }
        if (index == -1) {
            return@withLock Result.failure(IllegalArgumentException("La solicitud no existe."))
        }

        val solicitud = _solicitudes.value[index]
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return@withLock Result.failure(
                IllegalStateException("Solo se pueden cancelar solicitudes SOLICITADAS.")
            )
        }

        val lista = _solicitudes.value.toMutableList()
        lista[index] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)
        _solicitudes.value = lista

        val equipoIndex = _equipos.value.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex != -1) {
            _equipos.value = _equipos.value.toMutableList().also { l ->
                l[equipoIndex] = l[equipoIndex].copy(estado = EstadoEquipo.DISPONIBLE)
            }
        }

        Result.success(Unit)
    }

    override suspend fun registrarDevolucion(id: Int, evidenciaUri: String?): Result<Unit> = mutex.withLock {
        val index = _solicitudes.value.indexOfFirst { it.id == id }
        if (index == -1) {
            return@withLock Result.failure(IllegalArgumentException("La solicitud no existe."))
        }

        val solicitud = _solicitudes.value[index]
        if (solicitud.estado != EstadoSolicitud.ENTREGADA) {
            return@withLock Result.failure(
                IllegalStateException("La devolución debe asociarse a un préstamo activo (ENTREGADA).")
            )
        }

        val lista = _solicitudes.value.toMutableList()
        lista[index] = solicitud.copy(
            estado = EstadoSolicitud.DEVUELTA,
            evidenciaUri = evidenciaUri ?: solicitud.evidenciaUri,
            evidenciaEstado = if (evidenciaUri != null) EstadoEvidencia.SUBIENDO else solicitud.evidenciaEstado
        )
        _solicitudes.value = lista

        val equipoIndex = _equipos.value.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex != -1) {
            _equipos.value = _equipos.value.toMutableList().also { l ->
                l[equipoIndex] = l[equipoIndex].copy(estado = EstadoEquipo.DISPONIBLE)
            }
        }

        Result.success(Unit)
    }

    override suspend fun actualizarEstadoEvidencia(id: Int, estado: EstadoEvidencia): Result<Unit> {
        val index = _solicitudes.value.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(IllegalArgumentException("La solicitud no existe."))
        val lista = _solicitudes.value.toMutableList()
        lista[index] = lista[index].copy(evidenciaEstado = estado)
        _solicitudes.value = lista
        return Result.success(Unit)
    }

    open override suspend fun sincronizarCatalogo(): Result<Unit> {
        // En memoria no hay servicio remoto: no-op que simula éxito.
        return Result.success(Unit)
    }

    open override suspend fun sincronizarSolicitudes(): Result<Unit> {
        // En memoria no hay servicio remoto: no-op que simula éxito.
        return Result.success(Unit)
    }

    override fun observarFiltroCategoria(): Flow<CategoriaEquipo?> = _filtroCategoria.asStateFlow()

    override suspend fun guardarFiltroCategoria(categoria: CategoriaEquipo?) {
        _filtroCategoria.value = categoria
    }

    private fun observing(mapper: (List<Equipo>) -> Equipo?): Flow<Equipo?> =
        _equipos.asStateFlow().map { mapper(it) }
}