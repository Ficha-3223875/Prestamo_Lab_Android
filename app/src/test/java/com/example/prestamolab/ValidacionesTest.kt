package com.example.prestamolab

import com.example.prestamolab.data.duracionValida
import com.example.prestamolab.data.propositoValido
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun proposito_9_invalido() {
        assertFalse(propositoValido("123456789"))
    }

    @Test
    fun proposito_10_valido() {
        assertTrue(propositoValido("1234567890"))
    }

    @Test
    fun proposito_180_valido() {
        assertTrue(propositoValido("a".repeat(180)))
    }

    @Test
    fun proposito_181_invalido() {
        assertFalse(propositoValido("a".repeat(181)))
    }

    @Test
    fun duracion_0_invalida() {
        assertFalse(duracionValida(0))
    }

    @Test
    fun duracion_1_valida() {
        assertTrue(duracionValida(1))
    }

    @Test
    fun duracion_8_valida() {
        assertTrue(duracionValida(8))
    }

    @Test
    fun duracion_9_invalida() {
        assertFalse(duracionValida(9))
    }
}
