package com.example.prestamolab.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PrestamoApiService {

    @GET("equipos")
    suspend fun obtenerEquipos(): List<EquipoDto>

    @GET("equipos/{id}")
    suspend fun obtenerEquipo(@Path("id") id: Int): EquipoDto

    @GET("solicitudes")
    suspend fun obtenerSolicitudes(): List<SolicitudDto>

    @POST("equipos/{id}/solicitudes")
    suspend fun crearSolicitud(@Path("id") equipoId: Int, @Body solicitud: SolicitudDto): SolicitudDto

    @POST("solicitudes/{id}/devolucion")
    suspend fun registrarDevolucion(@Path("id") solicitudId: Int, @Body cuerpo: DevolucionRequestDto): SolicitudDto
}