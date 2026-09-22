package com.example.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prestamolab.data.repository.PrestamoRepository
import com.example.prestamolab.util.NotificadorRecordatorio
import com.example.prestamolab.util.UbicacionProvider

class PrestamoViewModelFactory(
    private val repository: PrestamoRepository,
    private val ubicacionProvider: UbicacionProvider = UbicacionProvider.NoDisponible,
    private val notificador: NotificadorRecordatorio = NotificadorRecordatorio.NoOp
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrestamoViewModel::class.java)) {
            return PrestamoViewModel(repository, ubicacionProvider, notificador) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}