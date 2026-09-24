package com.example.prestamolab.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val fechaSolicitud: LocalDateTime,
    val estado: String,
    val fotoUriString: String?,
    val fotoDevolucionUri: String?
)