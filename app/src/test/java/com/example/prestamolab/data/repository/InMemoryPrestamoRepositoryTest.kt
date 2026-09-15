package com.example.prestamolab.data.repository

import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryPrestamoRepositoryTest {

    private val repo = InMemoryPrestamoRepository()

    private fun solicitudValida(equipoId: Int = 1) = SolicitudPrestamo(
        id = 0,
        equipoId = equipoId,
        ambienteDestino = "Lab 3",
        proposito = "Práctica de redes",
        duracionHoras = 4,
        estado = EstadoSolicitud.SOLICITADA
    )

    @Test
    fun obtenerEquipos_devuelve_los_seis_iniciales() {
        assertEquals(6, repo.obtenerEquipos().size)
    }

    @Test
    fun obtenerEquipo_existente_devuelve_el_equipo() {
        val equipo = repo.obtenerEquipo(4)
        assertEquals("Router TP-Link", equipo?.nombre)
        assertEquals(EstadoEquipo.RESERVADO, equipo?.estado)
    }

    @Test
    fun obtenerEquipo_inexistente_devuelve_null() {
        assertNull(repo.obtenerEquipo(999))
    }

    @Test
    fun crearSolicitud_exitosa_asigna_id_y_reserva_equipo() {
        val resultado = repo.crearSolicitud(solicitudValida(1))

        assertTrue(resultado.isSuccess)
        val solicitudes = repo.obtenerSolicitudes()
        assertEquals(1, solicitudes.size)
        assertEquals(1, solicitudes[0].id)
        assertEquals(EstadoSolicitud.SOLICITADA, solicitudes[0].estado)
        assertEquals(EstadoEquipo.RESERVADO, repo.obtenerEquipo(1)?.estado)
    }

    @Test
    fun crearSolicitud_con_equipo_inexistente_falla() {
        val resultado = repo.crearSolicitud(solicitudValida(999))

        assertTrue(resultado.isFailure)
        assertEquals("El equipo no existe.", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun crearSolicitud_sobre_equipo_no_disponible_falla() {
        val resultado = repo.crearSolicitud(solicitudValida(4))
        val noReservado = repo.crearSolicitud(solicitudValida(5))

        assertTrue(resultado.isFailure)
        assertTrue(noReservado.isFailure)
        assertEquals("El equipo ya no está disponible.", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun crear_solicitud_duplicada_sobre_el_mismo_equipo_es_rechazada() {
        assertTrue(repo.crearSolicitud(solicitudValida(1)).isSuccess)
        assertTrue(repo.crearSolicitud(solicitudValida(1)).isFailure)
        assertEquals(1, repo.obtenerSolicitudes().size)
    }

    @Test
    fun obtenerSolicitud_existente_devuelve_la_solicitud() {
        repo.crearSolicitud(solicitudValida(2))

        val solicitud = repo.obtenerSolicitud(1)
        assertEquals(2, solicitud?.equipoId)
        assertEquals("Lab 3", solicitud?.ambienteDestino)
        assertEquals("Práctica de redes", solicitud?.proposito)
    }

    @Test
    fun obtenerSolicitud_inexistente_devuelve_null() {
        assertNull(repo.obtenerSolicitud(999))
    }

    @Test
    fun cancelarSolicitud_exitosa_cancela_y_libera_equipo() {
        repo.crearSolicitud(solicitudValida(3))

        val resultado = repo.cancelarSolicitud(1)

        assertTrue(resultado.isSuccess)
        assertEquals(EstadoSolicitud.CANCELADA, repo.obtenerSolicitud(1)?.estado)
        assertEquals(EstadoEquipo.DISPONIBLE, repo.obtenerEquipo(3)?.estado)
    }

    @Test
    fun cancelarSolicitud_permite_solicitar_de_nuevo() {
        repo.crearSolicitud(solicitudValida(3))
        repo.cancelarSolicitud(1)

        assertTrue(repo.crearSolicitud(solicitudValida(3)).isSuccess)
        assertEquals(2, repo.obtenerSolicitudes().size)
    }

    @Test
    fun cancelarSolicitud_inexistente_falla() {
        val resultado = repo.cancelarSolicitud(500)

        assertTrue(resultado.isFailure)
        assertEquals("La solicitud no existe.", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun cancelarSolicitud_ya_cancelada_falla() {
        repo.crearSolicitud(solicitudValida(2))
        repo.cancelarSolicitud(1)

        val segunda = repo.cancelarSolicitud(1)

        assertTrue(segunda.isFailure)
        assertEquals(
            "Solo se pueden cancelar solicitudes SOLICITADAS.",
            segunda.exceptionOrNull()?.message
        )
    }
}