package com.example.prestamolab.data.repository

import com.example.prestamolab.model.CategoriaEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEvidencia
import com.example.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    fun observarEquipos(): Flow<List<Equipo>>
    fun observarEquipo(id: Int): Flow<Equipo?>
    fun observarSolicitudes(): Flow<List<SolicitudPrestamo>>
    suspend fun obtenerEquipo(id: Int): Equipo?
    suspend fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>
    suspend fun cancelarSolicitud(id: Int): Result<Unit>
    suspend fun registrarDevolucion(id: Int, evidenciaUri: String?): Result<Unit>
    suspend fun actualizarEstadoEvidencia(id: Int, estado: EstadoEvidencia): Result<Unit>

    /** Sincroniza el catálogo con el servicio remoto conservando Room como fuente local. */
    suspend fun sincronizarCatalogo(): Result<Unit>

    /** Sincroniza solicitudes (incluidas evidencias pendientes). */
    suspend fun sincronizarSolicitudes(): Result<Unit>

    fun observarFiltroCategoria(): Flow<CategoriaEquipo?>
    suspend fun guardarFiltroCategoria(categoria: CategoriaEquipo?)
}