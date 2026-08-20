package com.example.prestamolab.model

enum class CategoriaEquipo(val texto: String) {
    COMPUTO("Cómputo"),
    AUDIOVISUAL("Audiovisual"),
    ELECTRONICA("Electrónica"),
    REDES("Redes")
}

enum class EstadoEquipo(val texto: String) {
    DISPONIBLE("Disponible"),
    RESERVADO("Reservado"),
    PRESTADO("Prestado")
}

enum class EstadoSolicitud(val texto: String) {
    SOLICITADA("Solicitada"),
    APROBADA("Aprobada"),
    ENTREGADA("Entregada"),
    DEVUELTA("Devuelta"),
    CANCELADA("Cancelada"),
    RECHAZADA("Rechazada")
}
