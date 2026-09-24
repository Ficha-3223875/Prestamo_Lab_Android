package com.example.prestamolab.data.repository

import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakePrestamoRepositoryTest {

    private val repo = FakePrestamoRepository()

    private fun solicitudValida(equipoId: Int = 1) = SolicitudPrestamo(
        id = 0,
        equipoId = equipoId,
        ambienteDestino = "Lab 3",
        proposito = "Práctica de redes",
        duracionHoras = 4,
        estado = EstadoSolicitud.SOLICITADA
    )

    @Test
    fun observarEquipos_devuelve_los_seis_iniciales() = runTest {
        val equipos = repo.observarEquipos().first()
        assertEquals(6, equipos.size)
    }

    @Test
    fun equipo_reservado_de_fabrica_tiene_el_estado_correcto() = runTest {
        val equipo = repo.observarEquipos().first().find { it.id == 4 }
        assertEquals("Router TP-Link", equipo?.nombre)
        assertEquals(EstadoEquipo.RESERVADO, equipo?.estado)
    }

    @Test
    fun crearSolicitud_exitosa_asigna_id_y_marca_equipo_prestado() = runTest {
        val resultado = repo.crearSolicitud(solicitudValida(1))

        assertTrue(resultado.isSuccess)
        val solicitudes = repo.observarSolicitudes().first()
        assertEquals(1, solicitudes.size)
        assertEquals(1, solicitudes[0].id)
        assertEquals(EstadoSolicitud.SOLICITADA, solicitudes[0].estado)

        val equipo = repo.observarEquipos().first().find { it.id == 1 }
        assertEquals(EstadoEquipo.PRESTADO, equipo?.estado)
    }

    @Test
    fun crearSolicitud_con_equipo_inexistente_falla() = runTest {
        val resultado = repo.crearSolicitud(solicitudValida(999))

        assertTrue(resultado.isFailure)
        assertEquals("El equipo no existe.", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun crearSolicitud_sobre_equipo_no_disponible_falla() = runTest {
        val sobreReservado = repo.crearSolicitud(solicitudValida(4))
        val sobrePrestado = repo.crearSolicitud(solicitudValida(5))

        assertTrue(sobreReservado.isFailure)
        assertTrue(sobrePrestado.isFailure)
        assertEquals(
            "El equipo no está disponible para préstamo.",
            sobreReservado.exceptionOrNull()?.message
        )
    }

    @Test
    fun crear_solicitud_duplicada_sobre_el_mismo_equipo_es_rechazada() = runTest {
        assertTrue(repo.crearSolicitud(solicitudValida(1)).isSuccess)

        val segunda = repo.crearSolicitud(solicitudValida(1))

        assertTrue(segunda.isFailure)
        assertEquals(
            "El equipo no está disponible para préstamo.",
            segunda.exceptionOrNull()?.message
        )
        assertEquals(1, repo.observarSolicitudes().first().size)
    }

    @Test
    fun cancelarSolicitud_exitosa_cancela_y_libera_equipo() = runTest {
        repo.crearSolicitud(solicitudValida(3))

        val resultado = repo.cancelarSolicitud(1)

        assertTrue(resultado.isSuccess)
        val solicitud = repo.observarSolicitudes().first().find { it.id == 1 }
        assertEquals(EstadoSolicitud.CANCELADA, solicitud?.estado)

        val equipo = repo.observarEquipos().first().find { it.id == 3 }
        assertEquals(EstadoEquipo.DISPONIBLE, equipo?.estado)
    }

    @Test
    fun cancelarSolicitud_permite_solicitar_de_nuevo() = runTest {
        repo.crearSolicitud(solicitudValida(3))
        repo.cancelarSolicitud(1)

        val segunda = repo.crearSolicitud(solicitudValida(3))

        assertTrue(segunda.isSuccess)
        assertEquals(2, repo.observarSolicitudes().first().size)
    }

    @Test
    fun cancelarSolicitud_inexistente_falla() = runTest {
        val resultado = repo.cancelarSolicitud(500)

        assertTrue(resultado.isFailure)
        assertEquals("La solicitud no existe.", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun devolverSolicitud_exitosa_libera_equipo_y_guarda_foto() = runTest {
        repo.crearSolicitud(solicitudValida(2))

        val resultado = repo.devolverSolicitud(1, "content://foto-devolucion")

        assertTrue(resultado.isSuccess)
        val solicitud = repo.observarSolicitudes().first().find { it.id == 1 }
        assertEquals(EstadoSolicitud.DEVUELTA, solicitud?.estado)
        assertEquals("content://foto-devolucion", solicitud?.fotoDevolucionUri)

        val equipo = repo.observarEquipos().first().find { it.id == 2 }
        assertEquals(EstadoEquipo.DISPONIBLE, equipo?.estado)
    }
}