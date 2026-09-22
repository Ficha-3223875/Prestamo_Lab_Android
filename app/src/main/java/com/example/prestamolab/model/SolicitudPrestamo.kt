package com.example.prestamolab.model

data class SolicitudPrestamo(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: EstadoSolicitud,
    val fechaSolicitud: Long = 0L,
    val fechaLimiteDevolucion: Long = 0L,
    val evidenciaUri: String? = null,
    val evidenciaEstado: EstadoEvidencia = EstadoEvidencia.LOCAL,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val sincronizado: Boolean = false
)