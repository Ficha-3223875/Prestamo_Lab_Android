package com.example.prestamolab.data.remote

import com.example.prestamolab.data.local.EquipoEntity
import com.example.prestamolab.data.local.SolicitudEntity
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEvidencia
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo

fun EquipoDto.toEntity(): EquipoEntity = EquipoEntity(
    id = id,
    nombre = nombre,
    categoria = categoria,
    estado = estado
)

fun Equipo.toDto(): EquipoDto = EquipoDto(
    id = id,
    nombre = nombre,
    categoria = categoria.name,
    estado = estado.name
)

fun SolicitudDto.toEntity(): SolicitudEntity = SolicitudEntity(
    id = id,
    equipoId = equipoId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    estado = estado,
    fechaSolicitud = fechaSolicitud,
    fechaLimiteDevolucion = fechaLimiteDevolucion,
    evidenciaUri = evidenciaUri,
    evidenciaEstado = evidenciaEstado,
    latitud = latitud,
    longitud = longitud,
    sincronizado = sincronizado
)

fun SolicitudPrestamo.toDto(): SolicitudDto = SolicitudDto(
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

fun SolicitudEntity.toDto(): SolicitudDto = SolicitudDto(
    id = id,
    equipoId = equipoId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    estado = estado,
    fechaSolicitud = fechaSolicitud,
    fechaLimiteDevolucion = fechaLimiteDevolucion,
    evidenciaUri = evidenciaUri,
    evidenciaEstado = evidenciaEstado,
    latitud = latitud,
    longitud = longitud,
    sincronizado = sincronizado
)