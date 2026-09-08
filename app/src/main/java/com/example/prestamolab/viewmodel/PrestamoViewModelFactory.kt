package com.example.prestamolab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prestamolab.data.repository.PrestamoRepository

class PrestamoViewModelFactory(
    private val repository: PrestamoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrestamoViewModel::class.java)) {
            return PrestamoViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
