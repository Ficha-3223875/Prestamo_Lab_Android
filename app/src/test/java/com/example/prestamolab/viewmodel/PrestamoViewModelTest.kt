package com.example.prestamolab.viewmodel

import com.example.prestamolab.data.repository.FakePrestamoRepository
import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.EstadoSolicitud
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrestamoViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun estado_inicial_carga_equipos_y_solicitudes() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }

        assertEquals(6, vm.uiState.value.equipos.size)
        assertTrue(vm.uiState.value.solicitudes.isEmpty())
        assertFalse(vm.uiState.value.guardando)
        assertNull(vm.uiState.value.mensaje)
    }

    @Test
    fun equipo_devuelve_el_equipo_por_id() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }

        assertEquals("Video Beam Epson", vm.equipo(2)?.nombre)
        assertNull(vm.equipo(999))
    }

    @Test
    fun crearSolicitud_exitosa_actualiza_el_estado() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }
        var resultado = false

        vm.crearSolicitud(1, "Lab 3", "Práctica de redes", 4) { resultado = it }

        assertTrue(resultado)
        assertEquals("Solicitud creada correctamente.", vm.uiState.value.mensaje)
        assertFalse(vm.uiState.value.guardando)
        assertEquals(1, vm.uiState.value.solicitudes.size)
        assertEquals(EstadoEquipo.PRESTADO, vm.equipo(1)?.estado)
    }

    @Test
    fun solicitud_devuelve_la_solicitud_creada() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }
        vm.crearSolicitud(1, "Lab 3", "Práctica de redes", 4)

        val solicitud = vm.solicitud(1)

        assertEquals(1, solicitud?.equipoId)
        assertEquals("Lab 3", solicitud?.ambienteDestino)
        assertEquals(EstadoSolicitud.SOLICITADA, solicitud?.estado)
        assertNull(vm.solicitud(999))
    }

    @Test
    fun crearSolicitud_con_equipo_inexistente_muestra_error() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }
        var resultado = true

        vm.crearSolicitud(999, "Lab 3", "Práctica de redes", 4) { resultado = it }

        assertFalse(resultado)
        assertEquals("El equipo solicitado no existe.", vm.uiState.value.mensaje)
    }

    @Test
    fun crearSolicitud_sobre_equipo_no_disponible_muestra_error() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }
        var resultado = true

        vm.crearSolicitud(4, "Lab 3", "Práctica de redes", 4) { resultado = it }

        assertFalse(resultado)
        assertEquals("El equipo no está disponible.", vm.uiState.value.mensaje)
    }

    @Test
    fun crearSolicitud_con_destino_vacio_muestra_error() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }

        vm.crearSolicitud(1, "", "Práctica de redes", 4)
        assertEquals("El ambiente o destino es obligatorio.", vm.uiState.value.mensaje)

        vm.crearSolicitud(1, "   ", "Práctica de redes", 4)
        assertEquals("El ambiente o destino es obligatorio.", vm.uiState.value.mensaje)
    }

    @Test
    fun crearSolicitud_con_proposito_fuera_de_rango_muestra_error() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }

        vm.crearSolicitud(1, "Lab 3", "corto", 4)
        assertEquals("El propósito debe tener entre 10 y 180 caracteres.", vm.uiState.value.mensaje)

        vm.crearSolicitud(1, "Lab 3", "a".repeat(200), 4)
        assertEquals("El propósito debe tener entre 10 y 180 caracteres.", vm.uiState.value.mensaje)
    }

    @Test
    fun crearSolicitud_con_duracion_invalida_muestra_error() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }

        vm.crearSolicitud(1, "Lab 3", "Práctica de redes", 0)
        assertEquals("La duración debe estar entre 1 y 8 horas.", vm.uiState.value.mensaje)

        vm.crearSolicitud(1, "Lab 3", "Práctica de redes", 9)
        assertEquals("La duración debe estar entre 1 y 8 horas.", vm.uiState.value.mensaje)
    }

    @Test
    fun crearSolicitud_duplicada_sobre_el_mismo_equipo_es_rechazada() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }
        var primero = false
        var segundo = true

        vm.crearSolicitud(1, "Lab 3", "Práctica de redes", 4) { primero = it }
        vm.crearSolicitud(1, "Lab 3", "Práctica de redes", 4) { segundo = it }

        assertTrue(primero)
        assertFalse(segundo)
        assertEquals("Ya existe una solicitud activa para este equipo.", vm.uiState.value.mensaje)
        assertEquals(1, vm.uiState.value.solicitudes.size)
    }

    @Test
    fun cancelarSolicitud_exitosa_libera_el_equipo() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }
        vm.crearSolicitud(1, "Lab 3", "Práctica de redes", 4)
        var resultado = false

        vm.cancelarSolicitud(1) { resultado = it }

        assertTrue(resultado)
        assertEquals(
            "Solicitud cancelada. El equipo volvió a estar disponible.",
            vm.uiState.value.mensaje
        )
        assertEquals(EstadoSolicitud.CANCELADA, vm.solicitud(1)?.estado)
        assertEquals(EstadoEquipo.DISPONIBLE, vm.equipo(1)?.estado)
    }

    @Test
    fun cancelarSolicitud_inexistente_muestra_error() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }
        var resultado = true

        vm.cancelarSolicitud(123) { resultado = it }

        assertFalse(resultado)
        assertEquals("La solicitud no existe.", vm.uiState.value.mensaje)
    }

    @Test
    fun limpiarMensaje_vacia_el_mensaje() = runTest {
        val vm = PrestamoViewModel(FakePrestamoRepository())
        backgroundScope.launch { vm.uiState.collect {} }
        vm.crearSolicitud(999, "Lab 3", "Práctica de redes", 4)

        vm.limpiarMensaje()

        assertNull(vm.uiState.value.mensaje)
    }
}