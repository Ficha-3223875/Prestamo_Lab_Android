package com.example.prestamolab.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prestamolab.model.CategoriaEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEquipo

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val categoria: String,
    val estado: String
)

fun EquipoEntity.toModel(): Equipo = Equipo(
    id = id,
    nombre = nombre,
    categoria = CategoriaEquipo.fromTexto(categoria),
    estado = EstadoEquipo.values().firstOrNull { it.name == estado } ?: EstadoEquipo.DISPONIBLE
)

fun Equipo.toEntity(): EquipoEntity = EquipoEntity(
    id = id,
    nombre = nombre,
    categoria = categoria.name,
    estado = estado.name
)