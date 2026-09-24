package com.example.prestamolab.data.repository

import com.example.prestamolab.model.CategoriaEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Doble de prueba de PrestamoRepository. Vive solo en app/src/test — nunca en main.
 * Reproduce el mismo comportamiento que RoomPrestamoRepository pero en memoria,
 * para poder probar el ViewModel sin necesitar una base de datos real.
 */
class FakePrestamoRepository : PrestamoRepository {

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
    private var siguienteSolicitudId = 1

    override fun observarEquipos(): StateFlow<List<Equipo>> = _equipos.asStateFlow()
    override fun observarSolicitudes(): StateFlow<List<SolicitudPrestamo>> = _solicitudes.asStateFlow()

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipo = _equipos.value.find { it.id == solicitud.equipoId }
            ?: return Result.failure(IllegalArgumentException("El equipo no existe."))

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(IllegalStateException("El equipo no está disponible para préstamo."))
        }

        val existeActiva = _solicitudes.value.any {
            it.equipoId == solicitud.equipoId &&
                    it.estado in setOf(EstadoSolicitud.SOLICITADA, EstadoSolicitud.APROBADA, EstadoSolicitud.ENTREGADA)
        }
        if (existeActiva) {
            return Result.failure(IllegalStateException("Ya existe una solicitud activa para este equipo."))
        }

        val nueva = solicitud.copy(id = siguienteSolicitudId++)
        _solicitudes.value = _solicitudes.value + nueva
        _equipos.value = _equipos.value.map {
            if (it.id == solicitud.equipoId) it.copy(estado = EstadoEquipo.PRESTADO) else it
        }

        return Result.success(Unit)
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitud = _solicitudes.value.find { it.id == id }
            ?: return Result.failure(IllegalArgumentException("La solicitud no existe."))

        _solicitudes.value = _solicitudes.value.map {
            if (it.id == id) it.copy(estado = EstadoSolicitud.CANCELADA) else it
        }
        _equipos.value = _equipos.value.map {
            if (it.id == solicitud.equipoId) it.copy(estado = EstadoEquipo.DISPONIBLE) else it
        }

        return Result.success(Unit)
    }

    override suspend fun devolverSolicitud(id: Int, fotoDevolucionUri: String?): Result<Unit> {
        val solicitud = _solicitudes.value.find { it.id == id }
            ?: return Result.failure(IllegalArgumentException("La solicitud no existe."))

        _solicitudes.value = _solicitudes.value.map {
            if (it.id == id) it.copy(estado = EstadoSolicitud.DEVUELTA, fotoDevolucionUri = fotoDevolucionUri) else it
        }
        _equipos.value = _equipos.value.map {
            if (it.id == solicitud.equipoId) it.copy(estado = EstadoEquipo.DISPONIBLE) else it
        }

        return Result.success(Unit)
    }
}