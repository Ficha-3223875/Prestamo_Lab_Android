package com.example.prestamolab.model

import java.time.LocalDateTime

data class SolicitudPrestamo(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val fechaSolicitud: LocalDateTime = LocalDateTime.now(),
    val estado: EstadoSolicitud = EstadoSolicitud.SOLICITADA,
    val fotoUriString: String? = null,
    val fotoDevolucionUri: String? = null // <-- AGREGADO este parámetro para solucionar el error
)