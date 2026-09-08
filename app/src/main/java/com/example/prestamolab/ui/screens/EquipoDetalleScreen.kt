package com.example.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoEquipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(
    equipo: Equipo?,
    onBack: () -> Unit,
    onSolicitar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del equipo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (equipo == null) {
            ErrorState("No se encontró el equipo solicitado.", Modifier.padding(padding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(equipo.nombre, style = MaterialTheme.typography.headlineSmall)
            Text("Categoría: ${equipo.categoria.texto}")
            Text("Estado: ${equipo.estado.texto}")

            Button(
                onClick = onSolicitar,
                enabled = equipo.estado == EstadoEquipo.DISPONIBLE,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Solicitar préstamo")
            }

            if (equipo.estado != EstadoEquipo.DISPONIBLE) {
                Text(
                    "Este equipo no puede solicitarse porque no está disponible.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(message, style = MaterialTheme.typography.titleMedium)
    }
}
