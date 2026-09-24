package com.example.prestamolab.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM solicitudes ORDER BY id DESC")
    fun observarTodas(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): SolicitudEntity?

    @Query("""
        SELECT * FROM solicitudes 
        WHERE equipoId = :equipoId 
        AND estado IN ('SOLICITADA', 'APROBADA', 'ENTREGADA')
    """)
    suspend fun obtenerActivasPorEquipo(equipoId: Int): List<SolicitudEntity>

    @Insert
    suspend fun insertar(solicitud: SolicitudEntity): Long

    @Update
    suspend fun actualizar(solicitud: SolicitudEntity)
}