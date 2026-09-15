package com.example.prestamolab.viewmodel

import com.example.prestamolab.data.repository.InMemoryPrestamoRepository
import com.example.prestamolab.data.repository.PrestamoRepository
import com.example.prestamolab.model.CategoriaEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrestamoViewModelTest {

    private fun crearSolicitud(
        vm: PrestamoViewModel,
        equipoId: Int = 1,
        destino: String = "Lab 3",
        proposito: String = "Práctica de redes",
        duracionHoras: Int = 4
    ): Boolean = vm.crearSolicitud(equipoId, destino, proposito, duracionHoras)

    @Test
    fun estado_inicial_carga_equipos_y_solicitudes() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())

        assertEquals(6, vm.uiState.value.equipos.size)
        assertTrue(vm.uiState.value.solicitudes.isEmpty())
        assertFalse(vm.uiState.value.guardando)
        assertNull(vm.uiState.value.mensaje)
    }

    @Test
    fun equipo_devuelve_el_equipo_por_id() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())

        val equipo = vm.equipo(2)

        assertEquals("Video Beam Epson", equipo?.nombre)
        assertNull(vm.equipo(999))
    }

    @Test
    fun crearSolicitud_exitosa_actualiza_el_estado() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())

        val exitoso = crearSolicitud(vm)

        assertTrue(exitoso)
        assertEquals("Solicitud creada correctamente.", vm.uiState.value.mensaje)
        assertFalse(vm.uiState.value.guardando)
        assertEquals(1, vm.uiState.value.solicitudes.size)
        assertEquals(EstadoEquipo.RESERVADO, vm.uiState.value.equipos[0].estado)
    }

    @Test
    fun solicitud_devuelve_la_solicitud_creada() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())
        crearSolicitud(vm)

        val solicitud = vm.solicitud(1)

        assertEquals(1, solicitud?.equipoId)
        assertEquals("Lab 3", solicitud?.ambienteDestino)
        assertEquals(EstadoSolicitud.SOLICITADA, solicitud?.estado)
        assertNull(vm.solicitud(999))
    }

    @Test
    fun crearSolicitud_con_equipo_inexistente_muestra_error() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())

        val exitoso = crearSolicitud(vm, equipoId = 999)

        assertFalse(exitoso)
        assertEquals("El equipo solicitado no existe.", vm.uiState.value.mensaje)
    }

    @Test
    fun crearSolicitud_sobre_equipo_no_disponible_muestra_error() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())

        val exitoso = crearSolicitud(vm, equipoId = 4)

        assertFalse(exitoso)
        assertEquals("El equipo no está disponible.", vm.uiState.value.mensaje)
    }

    @Test
    fun crearSolicitud_con_destino_vacio_muestra_error() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())

        assertFalse(crearSolicitud(vm, destino = ""))
        assertEquals("El ambiente o destino es obligatorio.", vm.uiState.value.mensaje)

        assertFalse(crearSolicitud(vm, destino = "   "))
        assertEquals("El ambiente o destino es obligatorio.", vm.uiState.value.mensaje)
    }

    @Test
    fun crearSolicitud_con_proposito_fuera_de_rango_muestra_error() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())

        assertFalse(crearSolicitud(vm, proposito = "corto"))
        assertEquals("El propósito debe tener entre 10 y 180 caracteres.", vm.uiState.value.mensaje)

        assertFalse(crearSolicitud(vm, proposito = "a".repeat(200)))
        assertEquals("El propósito debe tener entre 10 y 180 caracteres.", vm.uiState.value.mensaje)
    }

    @Test
    fun crearSolicitud_con_duracion_invalida_muestra_error() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())

        assertFalse(crearSolicitud(vm, duracionHoras = 0))
        assertEquals("La duración debe estar entre 1 y 8 horas.", vm.uiState.value.mensaje)

        assertFalse(crearSolicitud(vm, duracionHoras = 9))
        assertEquals("La duración debe estar entre 1 y 8 horas.", vm.uiState.value.mensaje)
    }

    @Test
    fun cancelarSolicitud_exitosa_libera_el_equipo() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())
        crearSolicitud(vm)

        val exitoso = vm.cancelarSolicitud(1)

        assertTrue(exitoso)
        assertEquals(
            "Solicitud cancelada. El equipo volvió a estar disponible.",
            vm.uiState.value.mensaje
        )
        assertEquals(EstadoSolicitud.CANCELADA, vm.uiState.value.solicitudes[0].estado)
        assertEquals(EstadoEquipo.DISPONIBLE, vm.uiState.value.equipos[0].estado)
    }

    @Test
    fun cancelarSolicitud_inexistente_muestra_error() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())

        val exitoso = vm.cancelarSolicitud(123)

        assertFalse(exitoso)
        assertEquals("La solicitud no existe.", vm.uiState.value.mensaje)
    }

    @Test
    fun cancelarSolicitud_que_no_esta_solicitada_muestra_error() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())
        crearSolicitud(vm)
        vm.cancelarSolicitud(1)

        val segunda = vm.cancelarSolicitud(1)

        assertFalse(segunda)
        assertEquals(
            "Solo se pueden cancelar solicitudes SOLICITADAS.",
            vm.uiState.value.mensaje
        )
    }

    @Test
    fun limpiarMensaje_vacia_el_mensaje() {
        val vm = PrestamoViewModel(InMemoryPrestamoRepository())
        crearSolicitud(vm, equipoId = 999)

        vm.limpiarMensaje()

        assertNull(vm.uiState.value.mensaje)
    }

    @Test
    fun no_permite_doble_guardado_mientras_guardando() {
        val vm = PrestamoViewModel(FailingRepository())
        var fallo: Throwable? = null
        try {
            crearSolicitud(vm)
        } catch (e: RuntimeException) {
            fallo = e
        }

        assertTrue(vm.uiState.value.guardando)
        assertFalse(crearSolicitud(vm))
        assertEquals("boom", fallo?.message)
    }

    private class FailingRepository : PrestamoRepository {
        override fun obtenerEquipos(): List<Equipo> =
            listOf(Equipo(1, "Equipo de prueba", CategoriaEquipo.COMPUTO, EstadoEquipo.DISPONIBLE))

        override fun obtenerEquipo(id: Int): Equipo? =
            if (id == 1) Equipo(1, "Equipo de prueba", CategoriaEquipo.COMPUTO, EstadoEquipo.DISPONIBLE)
            else null

        override fun obtenerSolicitudes(): List<SolicitudPrestamo> = emptyList()

        override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = null

        override fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
            throw RuntimeException("boom")
        }

        override fun cancelarSolicitud(id: Int): Result<Unit> = Result.success(Unit)
    }
}