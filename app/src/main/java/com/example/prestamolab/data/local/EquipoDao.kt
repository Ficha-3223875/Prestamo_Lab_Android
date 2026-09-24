package com.example.prestamolab.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    @Query("SELECT * FROM equipos")
    fun observarTodos(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos")
    suspend fun obtenerTodos(): List<EquipoEntity>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): EquipoEntity?

    @Update
    suspend fun actualizar(equipo: EquipoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(equipos: List<EquipoEntity>)
}