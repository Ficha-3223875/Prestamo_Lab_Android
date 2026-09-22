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
    fun observarPorId(id: Int): Flow<SolicitudEntity?>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerPorId(id: Int): SolicitudEntity?

    @Insert
    suspend fun insertar(solicitud: SolicitudEntity): Long

    @Update
    suspend fun actualizar(solicitud: SolicitudEntity)

    @Query("UPDATE solicitudes SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query(
        "UPDATE solicitudes SET evidenciaUri = :uri, evidenciaEstado = :estado WHERE id = :id"
    )
    suspend fun actualizarEvidencia(id: Int, uri: String?, estado: String)

    @Query("UPDATE solicitudes SET evidenciaEstado = :estado WHERE id = :id")
    suspend fun actualizarEstadoEvidencia(id: Int, estado: String)

    @Query("UPDATE solicitudes SET sincronizado = :sincronizado WHERE id = :id")
    suspend fun actualizarSincronizado(id: Int, sincronizado: Boolean)

    @Query("SELECT * FROM solicitudes WHERE equipoId = :equipoId")
    suspend fun porEquipo(equipoId: Int): List<SolicitudEntity>
}