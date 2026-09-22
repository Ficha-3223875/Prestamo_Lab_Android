package com.example.prestamolab.di

import android.content.Context
import com.example.prestamolab.data.local.PrestamoDatabase
import com.example.prestamolab.data.local.UserPreferencesStore
import com.example.prestamolab.data.remote.NetworkModule
import com.example.prestamolab.data.repository.PrestamoRepository
import com.example.prestamolab.data.repository.RoomPrestamoRepository
import com.example.prestamolab.data.security.TokenStore

/**
 * Contenedor de dependencias (inyección a mano, sin frameworks).
 * Centraliza la construcción de la base de datos, preferencias, API y repositorio.
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val database: PrestamoDatabase by lazy {
        PrestamoDatabase.crear(appContext)
    }

    private val userPreferencesStore: UserPreferencesStore by lazy {
        UserPreferencesStore(appContext)
    }

    private val tokenStore: TokenStore by lazy {
        TokenStore(appContext)
    }

    private val api by lazy {
        NetworkModule.crearApi(tokenStore)
    }

    val repository: PrestamoRepository by lazy {
        RoomPrestamoRepository(
            database = database,
            equipoDao = database.equipoDao(),
            solicitudDao = database.solicitudDao(),
            userPreferencesStore = userPreferencesStore,
            api = api
        )
    }
}