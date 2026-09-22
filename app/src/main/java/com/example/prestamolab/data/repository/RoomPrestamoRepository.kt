package com.example.prestamolab.data.repository

import androidx.room.withTransaction
import com.example.prestamolab.data.local.EquipoDao
import com.example.prestamolab.data.local.PrestamoDatabase
import com.example.prestamolab.data.local.SolicitudDao
import com.example.prestamolab.data.local.toEntity
import com.example.prestamolab.data.local.toModel
import com.example.prestamolab.data.local.UserPreferencesStore
import com.example.prestamolab.data.remote.DevolucionRequestDto
import com.example.prestamolab.data.remote.PrestamoApiService
import com.example.prestamolab.data.remote.toEntity
import com.example.prestamolab.data.remote.toDto
import com.example.prestamolab.model.CategoriaEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.EstadoEvidencia
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Repositorio con persistencia local (Room) como fuente canónica y
 * sincronización con el servicio remoto (Retrofit). Local-first:
 * la UI siempre observa Room; la red actualiza la base en segundo plano.
 */
class RoomPrestamoRepository(
    private val database: PrestamoDatabase,
    private val equipoDao: EquipoDao,
    private val solicitudDao: SolicitudDao,
    private val userPreferencesStore: UserPreferencesStore,
    private val api: PrestamoApiService
) : PrestamoRepository {

    override fun observarEquipos(): Flow<List<Equipo>> =
        equipoDao.observarTodos().map { lista -> lista.map { it.toModel() } }

    override fun observarEquipo(id: Int): Flow<Equipo?> =
        equipoDao.observarPorId(id).map { it?.toModel() }

    override fun observarSolicitudes(): Flow<List<SolicitudPrestamo>> =
        solicitudDao.observarTodas().map { lista -> lista.map { it.toModel() } }

    override suspend fun obtenerEquipo(id: Int): Equipo? =
        equipoDao.obtenerPorId(id)?.toModel()

    override suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        solicitudDao.obtenerPorId(id)?.toModel()

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> = runCatching {
        database.withTransaction {
            val equipo = equipoDao.obtenerPorId(solicitud.equipoId)
                ?: throw IllegalArgumentException("El equipo no existe.")

            if (equipo.estado != EstadoEquipo.DISPONIBLE.name) {
                throw IllegalStateException("El equipo ya no está disponible.")
            }

            val activas = solicitudDao.porEquipo(solicitud.equipoId)
            val existeActiva = activas.any {
                it.estado in setOf(
                    EstadoSolicitud.SOLICITADA.name,
                    EstadoSolicitud.APROBADA.name,
                    EstadoSolicitud.ENTREGADA.name
                )
            }
            if (existeActiva) {
                throw IllegalStateException("Ya existe una solicitud activa para este equipo.")
            }

            val ahora = System.currentTimeMillis()
            val entidad = solicitud
                .copy(
                    estado = EstadoSolicitud.SOLICITADA,
                    fechaSolicitud = ahora,
                    fechaLimiteDevolucion = ahora + solicitud.duracionHoras * 3_600_000L
                )
                .toEntity()
            solicitudDao.insertar(entidad)
            equipoDao.actualizarEstado(solicitud.equipoId, EstadoEquipo.RESERVADO.name)
        }
        Unit
    }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> = runCatching {
        database.withTransaction {
            val solicitud = solicitudDao.obtenerPorId(id)
                ?: throw IllegalArgumentException("La solicitud no existe.")

            if (solicitud.estado != EstadoSolicitud.SOLICITADA.name) {
                throw IllegalStateException("Solo se pueden cancelar solicitudes SOLICITADAS.")
            }

            solicitudDao.actualizarEstado(id, EstadoSolicitud.CANCELADA.name)
            equipoDao.actualizarEstado(solicitud.equipoId, EstadoEquipo.DISPONIBLE.name)
        }
        Unit
    }

    override suspend fun registrarDevolucion(id: Int, evidenciaUri: String?): Result<Unit> = runCatching {
        database.withTransaction {
            val solicitud = solicitudDao.obtenerPorId(id)
                ?: throw IllegalArgumentException("La solicitud no existe.")

            if (solicitud.estado != EstadoSolicitud.ENTREGADA.name) {
                throw IllegalStateException("La devolución debe asociarse a un préstamo activo (ENTREGADA).")
            }

            solicitudDao.actualizarEstado(id, EstadoSolicitud.DEVUELTA.name)
            if (evidenciaUri != null) {
                solicitudDao.actualizarEvidencia(
                    id,
                    uri = evidenciaUri,
                    estado = EstadoEvidencia.SUBIENDO.name
                )
            }
            equipoDao.actualizarEstado(solicitud.equipoId, EstadoEquipo.DISPONIBLE.name)
        }
        Unit
    }

    override suspend fun actualizarEstadoEvidencia(id: Int, estado: EstadoEvidencia): Result<Unit> =
        runCatching {
            solicitudDao.actualizarEstadoEvidencia(id, estado.name)
        }

    override suspend fun sincronizarCatalogo(): Result<Unit> = runCatching {
        val remotos = api.obtenerEquipos()
        val entidades = remotos.map { it.toEntity() }
        // Se sincroniza solo los equipos del servicio; Room sigue siendo
        // la fuente canónica visible para la UI.
        equipoDao.guardarTodos(entidades)
    }

    override suspend fun sincronizarSolicitudes(): Result<Unit> = runCatching {
        // Evidencias pendientes de subida (estado SUBIENDO).
        val pendientes = solicitudDao.observarTodas().first()
            .filter { it.evidenciaEstado == EstadoEvidencia.SUBIENDO.name }

        for (solicitud in pendientes) {
            try {
                val respuesta = api.registrarDevolucion(
                    solicitudId = solicitud.id,
                    cuerpo = DevolucionRequestDto(
                        solicitudId = solicitud.id,
                        evidenciaUri = solicitud.evidenciaUri,
                        latitud = solicitud.latitud,
                        longitud = solicitud.longitud
                    )
                )
                if (respuesta.id > 0) {
                    solicitudDao.actualizarEvidencia(
                        solicitud.id,
                        solicitud.evidenciaUri,
                        EstadoEvidencia.SINCRONIZADA.name
                    )
                    solicitudDao.actualizarSincronizado(solicitud.id, true)
                }
            } catch (e: Exception) {
                solicitudDao.actualizarEvidencia(
                    solicitud.id,
                    solicitud.evidenciaUri,
                    EstadoEvidencia.FALLIDA.name
                )
            }
        }
    }

    override fun observarFiltroCategoria(): Flow<CategoriaEquipo?> =
        userPreferencesStore.filtroCategoria

    override suspend fun guardarFiltroCategoria(categoria: CategoriaEquipo?) {
        userPreferencesStore.guardarFiltroCategoria(categoria)
    }
}