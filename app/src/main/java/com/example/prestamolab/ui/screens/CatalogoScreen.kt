package com.example.prestamolab.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolab.model.EstadoEquipo
import com.example.prestamolab.model.Equipo
import com.example.prestamolab.viewmodel.PrestamoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    state: PrestamoUiState,
    onEquipoClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("PréstamoLab CTMA")
                        Text(
                            "Catálogo de equipos",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Selecciona un equipo para consultar su detalle.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            items(state.equipos, key = { it.id }) { equipo ->
                EquipoCard(equipo, onEquipoClick)
            }
        }
    }
}

@Composable
private fun EquipoCard(
    equipo: Equipo,
    onClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(equipo.id) }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(equipo.nombre, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(equipo.categoria.texto)
            Spacer(Modifier.height(10.dp))

            val disponible = equipo.estado == EstadoEquipo.DISPONIBLE

            Text(
                text = "Estado: ${equipo.estado.texto}",
                color = if (disponible)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
