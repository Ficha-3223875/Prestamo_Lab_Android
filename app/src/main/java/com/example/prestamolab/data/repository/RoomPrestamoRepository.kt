package com.example.prestamolab.data.repository

import com.example.prestamolab.data.local.EquipoDao
import com.example.prestamolab.data.local.EquipoEntity
import com.example.prestamolab.data.local.SolicitudDao
import com.example.prestamolab.data.local.SolicitudEntity
import com.example.prestamolab.model.CategoriaEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao
) : PrestamoRepository {

    override fun observarEquipos(): Flow<List<Equipo>> =
        equipoDao.observarTodos().map { lista -> lista.map { it.toDomain() } }

    override fun observarSolicitudes(): Flow<List<SolicitudPrestamo>> =
        solicitudDao.observarTodas().map { lista -> lista.map { it.toDomain() } }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> =
        withContext(Dispatchers.IO) {
            val equipoEntity = equipoDao.obtenerPorId(solicitud.equipoId)
                ?: return@withContext Result.failure(IllegalArgumentException("El equipo no existe."))

            if (equipoEntity.estado != EstadoEquipo.DISPONIBLE.name) {
                return@withContext Result.failure(IllegalStateException("El equipo no está disponible para préstamo."))
            }

            // RN-05: evita que una doble pulsación (o el mismo equipo dos veces) cree solicitudes duplicadas
            val existeActiva = solicitudDao.obtenerActivasPorEquipo(solicitud.equipoId).isNotEmpty()
            if (existeActiva) {
                return@withContext Result.failure(IllegalStateException("Ya existe una solicitud activa para este equipo."))
            }

            solicitudDao.insertar(solicitud.toEntity())
            equipoDao.actualizar(equipoEntity.copy(estado = EstadoEquipo.PRESTADO.name))

            Result.success(Unit)
        }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> = withContext(Dispatchers.IO) {
        val solicitudEntity = solicitudDao.obtenerPorId(id)
            ?: return@withContext Result.failure(IllegalArgumentException("La solicitud no existe."))

        solicitudDao.actualizar(solicitudEntity.copy(estado = EstadoSolicitud.CANCELADA.name))

        equipoDao.obtenerPorId(solicitudEntity.equipoId)?.let { equipoEntity ->
            equipoDao.actualizar(equipoEntity.copy(estado = EstadoEquipo.DISPONIBLE.name))
        }

        Result.success(Unit)
    }

    override suspend fun devolverSolicitud(id: Int, fotoDevolucionUri: String?): Result<Unit> =
        withContext(Dispatchers.IO) {
            val solicitudEntity = solicitudDao.obtenerPorId(id)
                ?: return@withContext Result.failure(IllegalArgumentException("La solicitud no existe."))

            solicitudDao.actualizar(
                solicitudEntity.copy(
                    estado = EstadoSolicitud.DEVUELTA.name,
                    fotoDevolucionUri = fotoDevolucionUri
                )
            )

            equipoDao.obtenerPorId(solicitudEntity.equipoId)?.let { equipoEntity ->
                equipoDao.actualizar(equipoEntity.copy(estado = EstadoEquipo.DISPONIBLE.name))
            }

            Result.success(Unit)
        }

    private fun EquipoEntity.toDomain() = Equipo(
        id = id,
        nombre = nombre,
        categoria = CategoriaEquipo.valueOf(categoria),
        estado = EstadoEquipo.valueOf(estado)
    )

    private fun SolicitudEntity.toDomain() = SolicitudPrestamo(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        fechaSolicitud = fechaSolicitud,
        estado = EstadoSolicitud.valueOf(estado),
        fotoUriString = fotoUriString,
        fotoDevolucionUri = fotoDevolucionUri
    )

    private fun SolicitudPrestamo.toEntity() = SolicitudEntity(
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        fechaSolicitud = fechaSolicitud,
        estado = EstadoSolicitud.SOLICITADA.name,
        fotoUriString = fotoUriString,
        fotoDevolucionUri = fotoDevolucionUri
    )
}