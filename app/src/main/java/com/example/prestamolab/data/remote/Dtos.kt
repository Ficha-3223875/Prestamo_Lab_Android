package com.example.prestamolab.data.remote

import com.google.gson.annotations.SerializedName

data class EquipoDto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val estado: String
)

data class SolicitudDto(
    val id: Int,
    @SerializedName("equipoId") val equipoId: Int,
    @SerializedName("ambienteDestino") val ambienteDestino: String,
    val proposito: String,
    @SerializedName("duracionHoras") val duracionHoras: Int,
    val estado: String,
    @SerializedName("fechaSolicitud") val fechaSolicitud: Long,
    @SerializedName("fechaLimiteDevolucion") val fechaLimiteDevolucion: Long,
    @SerializedName("evidenciaUri") val evidenciaUri: String?,
    @SerializedName("evidenciaEstado") val evidenciaEstado: String,
    val latitud: Double?,
    val longitud: Double?,
    val sincronizado: Boolean
)

data class DevolucionRequestDto(
    @SerializedName("solicitudId") val solicitudId: Int,
    @SerializedName("evidenciaUri") val evidenciaUri: String?,
    val latitud: Double?,
    val longitud: Double?
)