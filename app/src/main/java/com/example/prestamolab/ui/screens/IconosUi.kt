package com.example.prestamolab.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.prestamolab.model.CategoriaEquipo

fun categoriaIcon(categoria: CategoriaEquipo): ImageVector = when (categoria) {
    CategoriaEquipo.COMPUTO -> Icons.Default.Computer
    CategoriaEquipo.AUDIOVISUAL -> Icons.Default.Videocam
    CategoriaEquipo.ELECTRONICA -> Icons.Default.Memory
    CategoriaEquipo.REDES -> Icons.Default.Router
}