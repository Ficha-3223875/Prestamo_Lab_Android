package com.example.prestamolab.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.prestamolab.model.CategoriaEquipo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore by preferencesDataStore(name = "prestamolab_preferencias")

/**
 * Preferencias simples del usuario persistidas con DataStore (Semana 6).
 * Actualmente guarda el filtro de categoría seleccionado en el catálogo.
 */
class UserPreferencesStore(private val context: Context) {

    private val keyCategoria = stringPreferencesKey("filtro_categoria")

    val filtroCategoria: Flow<CategoriaEquipo?> = context.userPreferencesDataStore.data
        .map { prefs ->
            prefs[keyCategoria]?.let { CategoriaEquipo.fromTexto(it) }
        }

    suspend fun guardarFiltroCategoria(categoria: CategoriaEquipo?) {
        context.userPreferencesDataStore.edit { prefs ->
            if (categoria == null) {
                prefs.remove(keyCategoria)
            } else {
                prefs[keyCategoria] = categoria.name
            }
        }
    }
}