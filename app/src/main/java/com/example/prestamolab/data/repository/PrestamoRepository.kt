package com.example.prestamolab.data.repository

import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    fun observarEquipos(): Flow<List<Equipo>>
    fun observarSolicitudes(): Flow<List<SolicitudPrestamo>>
    suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>
    suspend fun cancelarSolicitud(id: Int): Result<Unit>
    suspend fun devolverSolicitud(id: Int, fotoDevolucionUri: String?): Result<Unit>
}