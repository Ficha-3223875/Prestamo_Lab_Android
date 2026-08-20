package com.example.prestamolab.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolab.viewmodel.PrestamoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    state: PrestamoUiState,
    onSolicitudClick: (Int) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis solicitudes") }) }
    ) { padding ->
        if (state.solicitudes.isEmpty()) {
            ErrorState(
                "Todavía no tienes solicitudes.",
                Modifier.padding(padding)
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.solicitudes, key = { it.id }) { solicitud ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSolicitudClick(solicitud.id) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Solicitud #${solicitud.id}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Equipo ID: ${solicitud.equipoId}")
                        Text("Destino: ${solicitud.ambienteDestino}")
                        Text("Estado: ${solicitud.estado.texto}")
                    }
                }
            }
        }
    }
}
