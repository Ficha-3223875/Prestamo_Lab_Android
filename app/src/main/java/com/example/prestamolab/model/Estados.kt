package com.example.prestamolab.model

enum class CategoriaEquipo(val texto: String) {
    COMPUTO("Cómputo"),
    AUDIOVISUAL("Audiovisual"),
    ELECTRONICA("Electrónica"),
    REDES("Redes");

    companion object {
        fun fromTexto(texto: String): CategoriaEquipo =
            values().firstOrNull { it.name == texto || it.texto == texto } ?: COMPUTO
    }
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

enum class EstadoEvidencia(val texto: String) {
    LOCAL("Local"),
    SUBIENDO("Subiendo"),
    SINCRONIZADA("Sincronizada"),
    FALLIDA("Fallida")
}
