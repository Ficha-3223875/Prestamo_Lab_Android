package com.example.prestamolab.data.repository

import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEquipo

fun equipoDisponible(equipo: Equipo): Boolean =
    equipo.estado == EstadoEquipo.DISPONIBLE

fun destinoValido(texto: String): Boolean =
    texto.trim().isNotEmpty()

fun propositoValido(texto: String): Boolean =
    texto.trim().length in 10..180

fun duracionValida(horas: Int): Boolean =
    horas in 1..8