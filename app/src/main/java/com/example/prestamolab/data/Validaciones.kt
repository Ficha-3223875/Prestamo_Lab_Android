package com.example.prestamolab.data

fun destinoValido(texto: String): Boolean =
    texto.trim().isNotEmpty()

fun propositoValido(texto: String): Boolean =
    texto.trim().length in 10..180

fun duracionValida(horas: Int): Boolean =
    horas in 1..8
