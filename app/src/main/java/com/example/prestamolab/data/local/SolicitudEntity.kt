package com.example.prestamolab.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prestamolab.model.EstadoEvidencia
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String,
    val fechaSolicitud: Long,
    val fechaLimiteDevolucion: Long,
    val evidenciaUri: String?,
    val evidenciaEstado: String,
    val latitud: Double?,
    val longitud: Double?,
    val sincronizado: Boolean
)

fun SolicitudEntity.toModel(): SolicitudPrestamo = SolicitudPrestamo(
    id = id,
    equipoId = equipoId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    estado = EstadoSolicitud.values().firstOrNull { it.name == estado } ?: EstadoSolicitud.SOLICITADA,
    fechaSolicitud = fechaSolicitud,
    fechaLimiteDevolucion = fechaLimiteDevolucion,
    evidenciaUri = evidenciaUri,
    evidenciaEstado = EstadoEvidencia.values().firstOrNull { it.name == evidenciaEstado } ?: EstadoEvidencia.LOCAL,
    latitud = latitud,
    longitud = longitud,
    sincronizado = sincronizado
)

fun SolicitudPrestamo.toEntity(): SolicitudEntity = SolicitudEntity(
    id = id,
    equipoId = equipoId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    estado = estado.name,
    fechaSolicitud = fechaSolicitud,
    fechaLimiteDevolucion = fechaLimiteDevolucion,
    evidenciaUri = evidenciaUri,
    evidenciaEstado = evidenciaEstado.name,
    latitud = latitud,
    longitud = longitud,
    sincronizado = sincronizado
)