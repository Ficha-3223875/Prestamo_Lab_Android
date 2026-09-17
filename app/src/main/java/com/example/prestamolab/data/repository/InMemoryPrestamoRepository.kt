package com.example.prestamolab.data.repository

import com.example.prestamolab.model.CategoriaEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo

class InMemoryPrestamoRepository : PrestamoRepository {

    private val equipos = mutableListOf(
        Equipo(1, "Portátil Lenovo ThinkPad", CategoriaEquipo.COMPUTO, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Video Beam Epson", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.DISPONIBLE),
        Equipo(3, "Kit Arduino UNO", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(4, "Router TP-Link", CategoriaEquipo.REDES, EstadoEquipo.RESERVADO),
        Equipo(5, "Portátil HP ProBook", CategoriaEquipo.COMPUTO, EstadoEquipo.PRESTADO),
        Equipo(6, "Cámara Logitech", CategoriaEquipo.AUDIOVISUAL, EstadoEquipo.DISPONIBLE)
    )

    private val solicitudes = mutableListOf<SolicitudPrestamo>()
    private var siguienteSolicitudId = 1

    override fun obtenerEquipos(): List<Equipo> = equipos.toList()

    override fun obtenerEquipo(id: Int): Equipo? =
        equipos.firstOrNull { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> =
        solicitudes.toList()

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        solicitudes.firstOrNull { it.id == id }

    @Synchronized
    override fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex == -1) {
            return Result.failure(IllegalArgumentException("El equipo no existe."))
        }

        if (equipos[equipoIndex].estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(IllegalStateException("El equipo ya no está disponible."))
        }

        val existeActiva = solicitudes.any {
            it.equipoId == solicitud.equipoId &&
                    it.estado in setOf(
                EstadoSolicitud.SOLICITADA,
                EstadoSolicitud.APROBADA,
                EstadoSolicitud.ENTREGADA
            )
        }

        if (existeActiva) {
            return Result.failure(IllegalStateException("Ya existe una solicitud activa para este equipo."))
        }

        val nueva = solicitud.copy(
            id = siguienteSolicitudId++,
            estado = EstadoSolicitud.SOLICITADA
        )

        solicitudes.add(nueva)
        equipos[equipoIndex] = equipos[equipoIndex].copy(
            estado = EstadoEquipo.RESERVADO
        )

        return Result.success(Unit)
    }

    @Synchronized
    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index == -1) {
            return Result.failure(IllegalArgumentException("La solicitud no existe."))
        }

        val solicitud = solicitudes[index]

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(
                IllegalStateException("Solo se pueden cancelar solicitudes SOLICITADAS.")
            )
        }

        solicitudes[index] = solicitud.copy(
            estado = EstadoSolicitud.CANCELADA
        )

        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex != -1) {
            equipos[equipoIndex] = equipos[equipoIndex].copy(
                estado = EstadoEquipo.DISPONIBLE
            )
        }

        return Result.success(Unit)
    }
}

object RepositoryProvider {
    val repository: PrestamoRepository by lazy {
        InMemoryPrestamoRepository()
    }
}