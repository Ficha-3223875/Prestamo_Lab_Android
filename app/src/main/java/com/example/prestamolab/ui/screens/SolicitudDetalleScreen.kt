package com.example.prestamolab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.model.EstadoSolicitud
import com.example.prestamolab.model.SolicitudPrestamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    equipo: Equipo?,
    onBack: () -> Unit,
    onCancelar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de solicitud") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (solicitud == null) {
            ErrorState("La solicitud no existe o ya no está disponible.", Modifier.padding(padding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Solicitud #${solicitud.id}",
                style = MaterialTheme.typography.headlineSmall
            )

            Text("Equipo: ${equipo?.nombre ?: "Equipo no encontrado"}")
            Text("Destino: ${solicitud.ambienteDestino}")
            Text("Propósito: ${solicitud.proposito}")
            Text("Duración: ${solicitud.duracionHoras} horas")
            Text("Estado: ${solicitud.estado.texto}")

            Button(
                onClick = onCancelar,
                enabled = solicitud.estado == EstadoSolicitud.SOLICITADA,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar solicitud")
            }

            if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
                Text(
                    "Esta solicitud no puede cancelarse porque su estado no es SOLICITADA.",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
